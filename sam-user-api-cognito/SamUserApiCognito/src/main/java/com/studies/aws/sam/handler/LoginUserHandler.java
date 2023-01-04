package com.studies.aws.sam.handler;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.studies.aws.sam.service.CognitoUserService;
import com.studies.aws.sam.util.Utils;

import software.amazon.awssdk.awscore.exception.AwsServiceException;

public class LoginUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private CognitoUserService cognitoUserService;
    private String appClientId;
    private String appClientSecret;
    
    public LoginUserHandler() {
        cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        appClientId = Utils.decryptKey("MY_COGNITO_POLL_APP_CLIENT_ID");
        appClientSecret = Utils.decryptKey("MY_COGNITO_POLL_APP_CLIENT_SECRET");
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        
        LambdaLogger logger = context.getLogger();
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        
        
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);
        
        
        try {

            JsonObject loginDetails = JsonParser.parseString(input.getBody()).getAsJsonObject();

            JsonObject loginResult = cognitoUserService.userLogin(appClientId, appClientSecret, loginDetails);

            response.withStatusCode(200);
            response.withBody(new Gson().toJson(loginResult, JsonObject.class));

        } catch (AwsServiceException e) {
            logger.log(e.awsErrorDetails().errorMessage());
            String errorMessage = toJsonErrorMessage(e.awsErrorDetails().errorMessage());
            response
                .withStatusCode(500)
                .withBody(errorMessage);
        } catch (Exception e) {
            logger.log(e.getMessage());
            String errorMessage = toJsonErrorMessage(e.getMessage());
            response
                .withStatusCode(500)
                .withBody(errorMessage);
        }
        return response;
    }

    private String toJsonErrorMessage(String message) {
        String errorMessage = new GsonBuilder().serializeNulls().create().toJson(new ErrorResponse(message),
                ErrorResponse.class);
        return errorMessage;
    }
}
