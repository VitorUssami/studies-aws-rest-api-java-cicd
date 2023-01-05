package com.studies.aws.sam.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.JsonObject;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

public class CognitoUserService {

    
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;
    
    public CognitoUserService(String region) {
        this.cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .build();
    }
    
    public CognitoUserService(CognitoIdentityProviderClient cognitoIdentityProviderClient) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
    }

    public JsonObject createUser(JsonObject jsonObject, String appClientId, String appClientSecret) {
        
        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();
        String userId = UUID.randomUUID().toString();
        String firstName = jsonObject.get("firstName").getAsString();
        String lastName = jsonObject.get("lastName").getAsString();
        
        AttributeType customUserId = AttributeType.builder()
                .name("custom:userId")
                .value(userId)
                .build();
        
        AttributeType name = AttributeType.builder()
                .name("name")
                .value(firstName+ " "+ lastName)
                .build();

        String calculateSecretHash = calculateSecretHash(appClientId, appClientSecret, email);
        
        SignUpRequest signUpRequst = SignUpRequest.builder()
                .username(email)
                .password(password)
                .userAttributes(List.of(customUserId, name))
                .clientId(appClientId)
                .secretHash(calculateSecretHash)
                .build();
                
        SignUpResponse response = cognitoIdentityProviderClient.signUp(signUpRequst);
        
        JsonObject createUserResult = new JsonObject();
        createUserResult.addProperty("isSuccessful", response.sdkHttpResponse().isSuccessful());
        createUserResult.addProperty("statusCode", response.sdkHttpResponse().statusCode());
        createUserResult.addProperty("cognitoUserId", response.userSub());
        createUserResult.addProperty("isConfirmed", response.userConfirmed());
        
        return createUserResult;
    }
    
    
    public JsonObject confirmUserSignup(String appClientId, String appClientSecret, 
            String email, String confirmationCode) {
        
        String calculateSecretHash = calculateSecretHash(appClientId, appClientSecret, email);
        
        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
            .secretHash(calculateSecretHash)
            .username(email)
            .confirmationCode(confirmationCode)
            .clientId(appClientId)
            .build();
        
        ConfirmSignUpResponse confirmSignUpResponse = cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);
        
        JsonObject response = new JsonObject();
        response.addProperty("isSuccessful", confirmSignUpResponse.sdkHttpResponse().isSuccessful());
        response.addProperty("statusCode", confirmSignUpResponse.sdkHttpResponse().statusCode());
        return response;
       
    }
    
    public JsonObject userLogin(String appClientId, String appClientSecret, JsonObject loginDetails) {
        
        String email = loginDetails.get("email").getAsString();
        String password = loginDetails.get("password").getAsString();
        String calculateSecretHash = calculateSecretHash(appClientId, appClientSecret, email);
        
        Map<String, String> authParams = new HashMap<String, String>() {
            {
                put("USERNAME", email);
                put("PASSWORD", password);
                put("SECRET_HASH", calculateSecretHash);
            }
        };
        
        InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
            .clientId(appClientId)
            .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
            .authParameters(authParams)
            .build();
        
        InitiateAuthResponse initiateAuthResponse = cognitoIdentityProviderClient.initiateAuth(initiateAuthRequest);
        AuthenticationResultType authenticationResult = initiateAuthResponse.authenticationResult();
        
        
        JsonObject response = new JsonObject();
        response.addProperty("isSuccessful", initiateAuthResponse.sdkHttpResponse().isSuccessful());
        response.addProperty("statusCode", initiateAuthResponse.sdkHttpResponse().statusCode());
        response.addProperty("idToken", authenticationResult.idToken());
        response.addProperty("accessToken", authenticationResult.accessToken());
        response.addProperty("refreshToken", authenticationResult.refreshToken());
        
        return response;
    }
    
    public JsonObject addUserToGroup(String groupName, String userName, String userPoolId) {
        
        AdminAddUserToGroupRequest request = AdminAddUserToGroupRequest.builder().groupName(groupName)
                                            .username(userName)
                                            .userPoolId(userPoolId)
                                            .build();
        
        AdminAddUserToGroupResponse groupResponse = cognitoIdentityProviderClient.adminAddUserToGroup(request);
        
        JsonObject response = new JsonObject();
        response.addProperty("isSuccessful", groupResponse.sdkHttpResponse().isSuccessful());
        response.addProperty("statusCode", groupResponse.sdkHttpResponse().statusCode());
        
        return response;
    }
    
    //https://docs.aws.amazon.com/cognito/latest/developerguide/signing-up-users-in-your-app.html
    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        
        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
