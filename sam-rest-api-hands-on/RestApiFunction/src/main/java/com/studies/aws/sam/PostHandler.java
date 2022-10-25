package com.studies.aws.sam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

/**
 * Handler for requests to Lambda function.
 */
public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        
        String requestBody = input.getBody();
        Gson gson = new Gson();
        Map<String, String> fromJson =  gson.fromJson(requestBody, Map.class);
        fromJson.put("userId", UUID.randomUUID().toString());
        fromJson.put("change", UUID.randomUUID().toString());
        
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.withStatusCode(200);
        response.withBody(gson.toJson(fromJson, Map.class));
        
        HashMap<String, String> responseHeader = new HashMap<String, String>();
        responseHeader.put("Content-Type", "application/json");
        
        response.withHeaders(responseHeader);
        
        return response;
    }

}
