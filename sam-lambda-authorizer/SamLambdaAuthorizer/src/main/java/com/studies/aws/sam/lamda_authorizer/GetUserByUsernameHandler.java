package com.studies.aws.sam.lamda_authorizer;

import java.util.HashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.studies.aws.sam.lamda_authorizer.service.CognitoUserService;

import software.amazon.awssdk.http.HttpStatusCode;

public class GetUserByUsernameHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private CognitoUserService service;
    
    public GetUserByUsernameHandler() {
        this.service = new CognitoUserService(System.getenv("AWS_REGION"));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        
        String userName = input.getPathParameters().get("userName");
        String poolId = System.getenv("LAMBDA_AUTH_POOL_ID");
        
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        response.withHeaders(headers);
        
        try {
            JsonObject userDetails = service.getUserByUserName(userName, poolId);
            response.withBody(new Gson().toJson(userDetails, JsonObject.class));
            response.withStatusCode(HttpStatusCode.OK);
        }catch (Exception e) {
            response.withBody(String.format("{\"message\":\"%s\"}", e.getMessage()));
            response.withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
        
        return response;
    }
}
