package com.studies.aws.sam.lamda_authorizer.service;

import java.util.List;

import com.google.gson.JsonObject;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

public class CognitoUserService {

    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    public CognitoUserService(String region) {
        cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
            .region(Region.of(region)).build();
    }
    
    public JsonObject getUserByUserName(String userName, String poolId) {
        
        AdminGetUserRequest adminGetUserRequest = AdminGetUserRequest.builder()
            .username(userName)
            .userPoolId(poolId)
            .build();
        
        AdminGetUserResponse adminGetUserResponse = cognitoIdentityProviderClient.adminGetUser(adminGetUserRequest);
        
        JsonObject jsonObject = new JsonObject();
        
        if(!adminGetUserResponse.sdkHttpResponse().isSuccessful()) {
            throw new IllegalArgumentException(
                    "http status code " + adminGetUserResponse.sdkHttpResponse().statusCode());
        }
        
        List<AttributeType> userAttributes = adminGetUserResponse.userAttributes();
        userAttributes.stream().forEach(attr -> 
            jsonObject.addProperty(attr.name(), attr.value()));
        
        return jsonObject;
    }
}
