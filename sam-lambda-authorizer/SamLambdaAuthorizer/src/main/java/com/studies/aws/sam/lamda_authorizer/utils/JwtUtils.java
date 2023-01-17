package com.studies.aws.sam.lamda_authorizer.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.RSAKeyProvider;

public class JwtUtils {

    public DecodedJWT validateJwtForuser(String jwt, String region, String userPollId, String principalId,
            String audience) {
        
        RSAKeyProvider awsCognitoRSAKeyProvider = new AwsCognitoRSAKeyProvider(region, userPollId);
        
        Algorithm algorithm = Algorithm.RSA256(awsCognitoRSAKeyProvider);
        JWTVerifier jwtVerifier = JWT.require(algorithm)
//                    .withClaim("sub", principalId)
                    .withSubject(principalId)
//                    .withClaim("aud", audience)
                    .withAudience(audience)
                    .withIssuer(String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPollId))
                    .withClaim("token_use", "id")
                    .build();
        
        return jwtVerifier.verify(jwt);
    }
}
