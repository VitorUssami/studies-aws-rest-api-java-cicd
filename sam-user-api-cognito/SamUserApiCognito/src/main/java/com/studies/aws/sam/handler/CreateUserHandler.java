package com.studies.aws.sam.handler;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.studies.aws.sam.service.CognitoUserService;
import com.studies.aws.sam.util.Utils;

import software.amazon.awssdk.awscore.exception.AwsServiceException;

/**
 * Handler for requests to Lambda function.
 */
public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private CognitoUserService cognitoUserService;
    private String appClientId;
    private String appClientSecret;
    public CreateUserHandler() {
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
        
        String body = input.getBody();
        logger.log("Original body: "+ body);
        
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        try {
            JsonObject createUserResult = cognitoUserService.createUser(jsonObject, appClientId, appClientSecret);
            response
                .withStatusCode(200)
                .withBody(new Gson().toJson(createUserResult, JsonObject.class));
        }catch (AwsServiceException e) {
            logger.log(e.awsErrorDetails().errorMessage());
            response
                .withStatusCode(500)
                .withBody(e.awsErrorDetails().errorMessage());
        }
        return response;
    }
}
