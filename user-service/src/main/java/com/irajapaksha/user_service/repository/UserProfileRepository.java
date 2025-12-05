package com.irajapaksha.user_service.repository;

import com.irajapaksha.user_service.model.UserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.Optional;

@Repository
public class UserProfileRepository {

    private final DynamoDbTable<UserProfile> table;

    public UserProfileRepository(DynamoDbEnhancedClient enhancedClient,
                                 @Value("${aws.dynamodb.table-name}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(UserProfile.class));
    }

    public Optional<UserProfile> findById(String userId) {
        Key key = Key.builder().partitionValue(userId).build();
        UserProfile profile = table.getItem(r -> r.key(key));
        return Optional.ofNullable(profile);
    }

    public void save(UserProfile profile) {
        table.putItem(profile);
    }

    public void update(UserProfile profile) {
        table.updateItem(profile);
    }

    public Optional<UserProfile> findByEmail(String email) {
        QueryConditional cond = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(email).build()
        );

        // Query the GSI (returns SdkIterable<Page<UserProfile>>)
        SdkIterable<Page<UserProfile>> pages = table.index("email-index")
                .query(r -> r.queryConditional(cond));

        // Iterate pages directly (SdkIterable<Page<T>> is Iterable<Page<T>>)
        for (Page<UserProfile> page : pages) {
            if (!page.items().isEmpty()) {
                return Optional.of(page.items().get(0));
            }
        }

        return Optional.empty();
    }

    public void delete(String userId) {
        Key key = Key.builder().partitionValue(userId).build();
        table.deleteItem(r -> r.key(key));
    }
}
