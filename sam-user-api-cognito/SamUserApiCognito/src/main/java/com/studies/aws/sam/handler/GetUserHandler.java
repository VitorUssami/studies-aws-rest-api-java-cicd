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
import com.studies.aws.sam.service.CognitoUserService;

import software.amazon.awssdk.awscore.exception.AwsServiceException;

/**
 * Handler for requests to Lambda function.
 */
public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private CognitoUserService cognitoUserService;
    
    
    public GetUserHandler() {
        cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        
        LambdaLogger logger = context.getLogger();
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);
        
        try {
            Map<String, String> requestHeaders = input.getHeaders();
            JsonObject user = cognitoUserService.getUser(requestHeaders.get("accessToken"));
            response
                .withStatusCode(200)
                .withBody(new Gson().toJson(user, JsonObject.class));
        }catch (AwsServiceException e) {
            logger.log(e.awsErrorDetails().errorMessage());
            response
                .withStatusCode(500)
                .withBody(e.awsErrorDetails().errorMessage());
        }
        
        return response;
    }
}
