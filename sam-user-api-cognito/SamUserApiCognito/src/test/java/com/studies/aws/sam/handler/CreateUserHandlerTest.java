package com.studies.aws.sam.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.studies.aws.sam.service.CognitoUserService;
import com.studies.aws.sam.shared.Constants;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.http.SdkHttpResponse;

@ExtendWith(MockitoExtension.class)
public class CreateUserHandlerTest {

    @Mock
    private CognitoUserService service;
    @Mock
    private APIGatewayProxyRequestEvent aPIGatewayProxyRequestEvent;
    @Mock
    private Context context;
    @Mock
    private LambdaLogger lambdaLogger;
    
    @InjectMocks
    private CreateUserHandler createUserHandler;
    
    @BeforeEach
    public void beforeEach() {
        when(context.getLogger()).thenReturn(lambdaLogger);
    }
    
    @Test
    public void test_handleRequest_valid() {
        
        JsonObject userDetails = new JsonObject();
        userDetails.addProperty("firstName", "User");
        userDetails.addProperty("lastName", "test");
        userDetails.addProperty("email", "test.user@abc.com");
        userDetails.addProperty("password", "123456");
        String sUserDetails = new Gson().toJson(userDetails, JsonObject.class);
        
        JsonObject createUserResult = new JsonObject();
        createUserResult.addProperty(Constants.IS_SUCCESSFUL, true);
        createUserResult.addProperty(Constants.STATUS_CODE, 200);
        createUserResult.addProperty(Constants.COGNITO_USER_ID, "123");
        createUserResult.addProperty(Constants.IS_CONFIRMED, false);
        
        
        when(aPIGatewayProxyRequestEvent.getBody()).thenReturn(sUserDetails);
        when(service.createUser(any(), any(), any())).thenReturn(createUserResult);
        
        
        APIGatewayProxyResponseEvent response = createUserHandler.handleRequest(aPIGatewayProxyRequestEvent, context);
        String responseBody = response.getBody();
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        
        
        verify(lambdaLogger, times(1)).log(anyString());
        assertTrue(jsonResponse.get(Constants.IS_SUCCESSFUL).getAsBoolean());
        assertEquals(200, jsonResponse.get(Constants.STATUS_CODE).getAsInt());
        assertNotNull(jsonResponse.get(Constants.COGNITO_USER_ID));
        assertEquals(200, response.getStatusCode(),"Should have http status code 200");
        assertFalse(jsonResponse.get(Constants.IS_CONFIRMED).getAsBoolean());
        verify(service, times(1)).createUser(any(), any(), any());
    }
    
    
    @Test
    public void test_handleRequest_invalid_emptyBody() {
        
        
        when(aPIGatewayProxyRequestEvent.getBody()).thenReturn("");
        
        
        APIGatewayProxyResponseEvent response = createUserHandler.handleRequest(aPIGatewayProxyRequestEvent, context);
        String responseBody = response.getBody();
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        
        
        assertEquals(500, response.getStatusCode());
        assertNotNull(jsonResponse.get("message"), "Missing 'message' property in json response");
        assertFalse(jsonResponse.get("message").getAsString().isEmpty(), "error message should not be empty");
    }
    
    @Test
    public void test_handleRequest_invalid_AwsServiceException() {
        
        AwsErrorDetails errorDetails = AwsErrorDetails.builder()
            .errorCode("")
            .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
            .errorMessage("exception message")
            .build();
        
        AwsServiceException serviceException = AwsServiceException.builder()
            .statusCode(500)
            .awsErrorDetails(errorDetails).build();
        
        when(aPIGatewayProxyRequestEvent.getBody()).thenReturn("{}");
        
        when(service.createUser(any(), any(), any())).thenThrow(serviceException);
        
        APIGatewayProxyResponseEvent response = createUserHandler.handleRequest(aPIGatewayProxyRequestEvent, context);
        String responseBody = response.getBody();
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        
        
        assertEquals(errorDetails.sdkHttpResponse().statusCode(), response.getStatusCode());
        assertNotNull(jsonResponse.get("message"), "Missing 'message' property in json response");
        assertEquals(errorDetails.errorMessage(), jsonResponse.get("message").getAsString());
    }
}
