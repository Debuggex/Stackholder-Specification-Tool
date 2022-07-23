package spring.framework.stackholder.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/user/login")||request.getServletPath().equals("/user/signup")){
            filterChain.doFilter(request,response);
        }else {
            String authorizationHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader!=null&&authorizationHeader.startsWith("Bearer ")){
                try{
                    String token=authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
                    JWTVerifier jwtVerifier= JWT.require(algorithm).build();
                    DecodedJWT decodedJWT= jwtVerifier.verify(token);
                    String username=decodedJWT.getSubject();
                    String[] userTypes=decodedJWT.getClaim("userTypes").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
                    Arrays.stream(userTypes).forEach(
                            userType->authorities.add(new SimpleGrantedAuthority(userType))
                    );
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(username,null,authorities);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    filterChain.doFilter(request,response);
                }catch (Exception e){
                    log.info("Error Logging in : {}",e.getMessage());
                    response.setHeader("error",e.getMessage());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    //response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    Map<String, String> error=new HashMap<>();
                    error.put("error_message",e.getMessage());
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(),error);
                }

            }else {
                filterChain.doFilter(request,response);
            }
        }
    }
}
