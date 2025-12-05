package com.irajapaksha.user_service.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.List;
import java.util.Map;

@DynamoDbBean
public class UserProfile {

    private String userId;        // Cognito sub (PK)
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Map<String, String> preferences;
    private List<String> roles;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("userId")
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @DynamoDbSecondaryPartitionKey(indexNames = {"email-index"})
    @DynamoDbAttribute("email")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @DynamoDbAttribute("fullName")
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    @DynamoDbAttribute("phone")
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @DynamoDbAttribute("address")
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @DynamoDbAttribute("preferences")
    public Map<String, String> getPreferences() { return preferences; }
    public void setPreferences(Map<String, String> preferences) { this.preferences = preferences; }

    @DynamoDbAttribute("roles")
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}