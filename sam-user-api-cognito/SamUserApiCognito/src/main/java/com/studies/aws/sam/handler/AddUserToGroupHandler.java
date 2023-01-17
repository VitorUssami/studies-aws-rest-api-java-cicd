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
public class AddUserToGroupHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private CognitoUserService cognitoUserService;
    private String poolId;
    
    public AddUserToGroupHandler() {
        cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        poolId = Utils.decryptKey("MY_COGNITO_POLL_ID");
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
        String group = jsonObject.get("group").getAsString();
        String userName =  input.getPathParameters().get("userName");
        
        try {
            JsonObject createUserResult = cognitoUserService.addUserToGroup(group, userName, poolId);
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
