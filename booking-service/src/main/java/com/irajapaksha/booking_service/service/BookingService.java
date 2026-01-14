package com.irajapaksha.booking_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irajapaksha.booking_service.dto.AvailabilityResponseDto;
import com.irajapaksha.booking_service.dto.CreateBookingRequestDto;
import com.irajapaksha.booking_service.model.BookingItem;
import com.irajapaksha.booking_service.util.DateRangeUtil;
import com.online_hotel_booking_system.event.BookingCreatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.http.HttpHeaders;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class BookingService {

    private final DynamoDbClient ddb;
    private final WebClient webClient;
    private final String bookingsTable;
    private final String locksTable;
    private final String idempotencyTable;
    private final SnsPublisher snsPublisher;

    public BookingService(
            DynamoDbClient ddb,
            @Qualifier("hotelWebClient") WebClient hotelWebClient,
            @Value("${aws.dynamodb.bookings-table}") String bookingsTable,
            @Value("${aws.dynamodb.locks-table}") String locksTable,
            @Value("${aws.dynamodb.idempotency-table}") String idempotencyTable,
            SnsPublisher snsPublisher
    ) {
        this.ddb = ddb;
        this.webClient = hotelWebClient; // use hotel's client
        this.bookingsTable = bookingsTable;
        this.locksTable = locksTable;
        this.idempotencyTable = idempotencyTable;
        this.snsPublisher = snsPublisher;
    }

    public String createBooking(CreateBookingRequestDto req) {
        // 1) Validate dates
        if (req.checkOutDate.isBefore(req.checkInDate)) {
            throw new IllegalArgumentException("checkOutDate must be >= checkInDate");
        }

        // 2) Verify room exists and basic availability from hotel service
        //    Hotel Service expected response: { "roomId": "...", "isAvailable": true/false }
        var roomCheck = webClient.get()
                .uri(uriBuilder -> uriBuilder

                        .path("/rooms/{roomId}")
                        .build(req.roomId))
                .retrieve()
                .bodyToMono(Map.class)
                .block(); // blocking for simplicity; can be reactive

        if (roomCheck == null) throw new RuntimeException("Hotel service returned null");
        Boolean isAvailable = (Boolean) roomCheck.getOrDefault("isAvailable", true);
        if (!isAvailable) {
            throw new IllegalStateException("Room not available (hotel service said unavailable)");
        }

        // 3) Idempotency: if client provided idempotencyKey - check table first
        if (req.idempotencyKey != null && !req.idempotencyKey.isBlank()) {
            // try to get idempotency record
            GetItemRequest get = GetItemRequest.builder()
                    .tableName(idempotencyTable)
                    .key(Map.of("pk", AttributeValue.builder().s(req.idempotencyKey).build(),
                            "sk", AttributeValue.builder().s("IDEMPOTENCY").build() ))
                    .build();
            GetItemResponse getResp = ddb.getItem(get);
            if (getResp.hasItem()) {
                // return existing booking id - client must handle idempotent response
                var existing = getResp.item();
                return existing.get("bookingId").s();
            }
        }

        // 4) Prepare transactional writes: booking item + locks for each date + idempotency record (optional)
        String bookingId = UUID.randomUUID().toString();
        BookingItem booking = new BookingItem();
        booking.bookingId = bookingId;
        booking.userId = req.userId;
        booking.hotelId = req.hotelId;
        booking.roomId = req.roomId;
        booking.checkInDate = req.checkInDate.toString();
        booking.checkOutDate = req.checkOutDate.toString();
        booking.status = "CONFIRMED";
        booking.createdAt = Instant.now();

        // Build booking put request
        Map<String, AttributeValue> bookingItem = new HashMap<>();
        bookingItem.put("pk", AttributeValue.builder().s("BOOKING#" + bookingId).build());
        bookingItem.put("sk", AttributeValue.builder().s("META").build());
        bookingItem.put("bookingId", AttributeValue.builder().s(bookingId).build());
        bookingItem.put("userId", AttributeValue.builder().s(booking.userId).build());
        bookingItem.put("hotelId", AttributeValue.builder().s(booking.hotelId).build());
        bookingItem.put("roomId", AttributeValue.builder().s(booking.roomId).build());
        bookingItem.put("checkInDate", AttributeValue.builder().s(booking.checkInDate).build());
        bookingItem.put("checkOutDate", AttributeValue.builder().s(booking.checkOutDate).build());
        bookingItem.put("status", AttributeValue.builder().s(booking.status).build());
        bookingItem.put("createdAt", AttributeValue.builder().s(booking.createdAt.toString()).build());
        System.out.println("bookingsTable pk: " + bookingItem.get("pk").s());
        System.out.println("bookingsTable sk: " + bookingItem.get("sk").s());

        Put putBooking = Put.builder()
                .tableName(bookingsTable)
                .item(bookingItem)
                // ensure no existing booking with same ID (shouldn't) and avoid overwrite
                .conditionExpression("attribute_not_exists(pk)")
                .build();

        List<TransactWriteItem> transactItems = new ArrayList<>();
        transactItems.add(TransactWriteItem.builder().put(putBooking).build());

        // Build lock items for each date
        List<LocalDate> dates = DateRangeUtil.inclusive(req.checkInDate, req.checkOutDate);
        for (LocalDate d : dates) {
            String lockKey = "LOCK#" + req.roomId + "#" + d.toString();
            Map<String, AttributeValue> lockItem = Map.of(
                    "pk", AttributeValue.builder().s(lockKey).build(),
                    "sk", AttributeValue.builder().s("LOCK").build(),
                    "roomId", AttributeValue.builder().s(req.roomId).build(),
                    "date", AttributeValue.builder().s(d.toString()).build(),
                    "bookingId", AttributeValue.builder().s(bookingId).build()
            );
            System.out.println("locksTable pk: " + lockItem.get("pk").s());
            System.out.println("locksTable sk: " + lockItem.get("sk").s());
            Put putLock = Put.builder()
                    .tableName(locksTable)
                    .item(lockItem)
                    .conditionExpression("attribute_not_exists(pk)") // fail if lock exists
                    .build();
            transactItems.add(TransactWriteItem.builder().put(putLock).build());
        }

        // Idempotency record put (if provided)
        if (req.idempotencyKey != null && !req.idempotencyKey.isBlank()) {
            Map<String, AttributeValue> idempItem = Map.of(
                    "pk", AttributeValue.builder().s(req.idempotencyKey).build(),
                    "sk", AttributeValue.builder().s("IDEMPOTENCY").build(),
                    "idempotencyKey", AttributeValue.builder().s(req.idempotencyKey).build(),
                    "bookingId", AttributeValue.builder().s(bookingId).build(),
                    "createdAt", AttributeValue.builder().s(Instant.now().toString()).build()
            );
            System.out.println("idempotencyTable idempotencyKey: " + idempItem.get("idempotencyKey").s());
            Put putIdemp = Put.builder()
                    .tableName(idempotencyTable)
                    .item(idempItem)
                    .conditionExpression("attribute_not_exists(pk)")
                    .build();
            transactItems.add(TransactWriteItem.builder().put(putIdemp).build());
        }

        // 5) Execute transaction
        TransactWriteItemsRequest twr = TransactWriteItemsRequest.builder()
                .transactItems(transactItems)
                .build();
        try {
            ddb.transactWriteItems(twr);
        } catch (TransactionCanceledException tce) {
            // Find reason: usually ConditionalCheckFailed
            throw new IllegalStateException("Booking conflict: room already booked for requested dates");
        } catch (DynamoDbException dde) {
            throw new RuntimeException("DynamoDB error: " + dde.getMessage(), dde);
        }

        // 6) Publish SNS event (non-blocking best-effort)

        Map<String, Object> event = Map.of(
                "eventType", "booking_created",
                "bookingId", bookingId,
                "userId", booking.userId,
                "hotelId", booking.hotelId,
                "roomId", booking.roomId,
                "checkInDate", booking.checkInDate,
                "checkOutDate", booking.checkOutDate,
                "createdAt", booking.createdAt.toString() );
        snsPublisher.publishBookingCreated(event);




        // Return booking ID
        return bookingId;
    }

    public AvailabilityResponseDto checkAvailability(String roomId, String date) {
        String pk = "LOCK#" + roomId + "#" + date;

        GetItemRequest req = GetItemRequest.builder()
                .tableName(locksTable)
                .key(Map.of(
                        "pk", AttributeValue.builder().s(pk).build(),
                        "sk", AttributeValue.builder().s("LOCK").build()
                ))
                .build();

        GetItemResponse resp = ddb.getItem(req);

        return new AvailabilityResponseDto(
                roomId,
                !resp.hasItem(),
                date
        );
    }
}