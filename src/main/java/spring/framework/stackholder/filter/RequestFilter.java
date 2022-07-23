package spring.framework.stackholder.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class RequestFilter {

    public boolean getAuthorize(String email, String token){
        token=token.substring("Bearer ".length());
        Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
        JWTVerifier jwtVerifier= JWT.require(algorithm).build();
        DecodedJWT decodedJWT= jwtVerifier.verify(token);
        String username=decodedJWT.getSubject();
        if (username.equals(email)){
            return true;
        }
        return false;
    }
}
