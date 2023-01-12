package com.studies.aws.sam.lamda_authorizer.authorizer;

import java.util.Arrays;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent.ProxyRequestContext;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.studies.aws.sam.lamda_authorizer.utils.JwtUtils;

/**
 * Handler for requests to Lambda function.
 */
public class LambdaAuthorizer implements RequestHandler<APIGatewayProxyRequestEvent, AuthorizerOutput> {

    @Override
    public AuthorizerOutput handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        String userName = input.getPathParameters().get("userName");
        String effect = "Allow";
        
        String jwt = input.getHeaders().get("Authorization");
        String region = System.getenv("AWS_REGION");
        String userPollId = System.getenv("LAMBDA_AUTH_POOL_ID");
        String audience = System.getenv("LAMBDA_AUTH_APP_CLIENT_ID");
        
        JwtUtils jwtUtils = new JwtUtils();
        
        try {
            
            DecodedJWT decodedJWT = jwtUtils.validateJwtForuser(jwt, region, userPollId, userName, audience);
            userName = decodedJWT.getSubject();
            
        } catch (RuntimeException e) {
            effect = "Deny";
        }
        
        ProxyRequestContext proxyRequestContext = input.getRequestContext();
        
        String arn = String.format("arn:aws:execute-api:%s:%s:%s/%s/%s/%s",
                System.getenv("AWS_REGION"),
                proxyRequestContext.getAccountId(),
                proxyRequestContext.getApiId(),
                proxyRequestContext.getStage(),
                proxyRequestContext.getHttpMethod(),
                "*");
        
        Statement statement = Statement.builder()
            .action("execute-api:Invoke")
            .effect(effect)
            .resource(arn)
            .build();
        
        PolicyDocument policyDocument = PolicyDocument.builder()
            .version("2012-10-17")
            .statements(Arrays.asList(statement))
            .build();
        
        
        AuthorizerOutput authOutput = AuthorizerOutput.builder()
            .principalId(userName)
            .policyDocument(policyDocument)
            .build();
        
        return authOutput;
    }

}
