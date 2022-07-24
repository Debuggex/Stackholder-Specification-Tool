package spring.framework.stackholder.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.framework.stackholder.Repositories.UserRepository;
import spring.framework.stackholder.ResponseDTO.LogInResponse;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.StackHolderConstants.Constants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {




    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        super.setFilterProcessesUrl("/user/login");

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        StringBuffer stringBuffer=new StringBuffer();
        String line = null;

        try {
            BufferedReader bufferedReader=request.getReader();
            while ((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONObject jsonObject= new JSONObject(String.valueOf(stringBuffer));
            String username=jsonObject.getString("email");
            String password=jsonObject.getString("password");
            log.info("Username is: {}",username); log.info("Password is: {}",password);
            UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username,password);
            return authenticationManager.authenticate(authenticationToken);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();
        Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
        String accessToken= JWT.create().withSubject(user.getUsername()).withExpiresAt(new Date(System.currentTimeMillis() +10*60*1000))
                .withIssuer(request.getRequestURI())
                .sign(algorithm);

        String refreshToken= JWT.create().withSubject(user.getUsername()).withExpiresAt(new Date(System.currentTimeMillis() +30*60*1000))
                .withIssuer(request.getRequestURI())
                .sign(algorithm);
//        response.setHeader("accessToken",accessToken);
//        response.setHeader("refreshToken",refreshToken);
        Map<String, String> tokens=new HashMap<>();

        JWTVerifier jwtVerifier= JWT.require(algorithm).build();
        DecodedJWT decodedJWT= jwtVerifier.verify(accessToken);
        String username=decodedJWT.getSubject();
        spring.framework.stackholder.domain.User isUserActive= userRepository.findAll().stream().filter(
                user1 -> user1.getEmail().equals(username)
        ).findFirst().get();
        LogInResponse logInResponse=new LogInResponse();
        Response<LogInResponse> response1=new Response<>();
        if (!isUserActive.getIsActive()){
            logInResponse.setId(null);
            logInResponse.setAccessToken(null);
            logInResponse.setRefreshToken(null);
            response1.setResponseMessage("Email is not Active!");
            response1.setResponseCode(Constants.EMAIL_NOT_ACTIVE);
            response1.setResponseBody(logInResponse);
        }else {
            logInResponse.setId(isUserActive.getId());
            logInResponse.setAccessToken(accessToken);
            logInResponse.setRefreshToken(refreshToken);

            response1.setResponseCode(1);
            response1.setResponseMessage("LogIn Successfully!");
            response1.setResponseBody(logInResponse);
        }



//        tokens.put("accessToken",accessToken);
//        tokens.put("refreshToken",refreshToken);

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(),response1);

    }

}
