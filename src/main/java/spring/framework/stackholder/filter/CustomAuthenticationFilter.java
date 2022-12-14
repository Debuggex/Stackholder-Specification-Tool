package spring.framework.stackholder.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.framework.stackholder.Repositories.UserRepository;
import spring.framework.stackholder.RequestDTO.LoginDTO;
import spring.framework.stackholder.ResponseDTO.LogInResponse;
import spring.framework.stackholder.ResponseDTO.Response;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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

        LoginDTO loginDTO;
        try {

            loginDTO = new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
            log.info(loginDTO.getEmail(), loginDTO.getPassword());
            spring.framework.stackholder.domain.User isUserActive;

            //Splitting the email string to check if it is a email or username;
            String[] user = loginDTO.getEmail().split("@");
            /*checking if username exists*/
            if (user.length == 1) {
                Optional<spring.framework.stackholder.domain.User> user1 = userRepository.findAll().stream().filter(
                        user2 -> user2.getUsername().equals(loginDTO.getEmail())
                ).findFirst();

                if (user1.isPresent()) {
                    isUserActive = user1.get();
                    loginDTO.setEmail(user1.get().getEmail());
                } else {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("Username/password is invalid");
                    return null;
                }
            }/** @Checking if email exists */ else {
                Optional<spring.framework.stackholder.domain.User> user1 = userRepository.findAll().stream().filter(
                        user2 -> user2.getEmail().equals(loginDTO.getEmail())
                ).findFirst();

                if (user1.isPresent()) {
                    isUserActive = user1.get();
                    loginDTO.setEmail(user1.get().getEmail());
                } else {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("Email/password is invalid");
                    return null;
                }
            }

            if (!isUserActive.getIsActive()) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("Email/User Name is not active");
                return null;
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(), loginDTO.getPassword());
            try {
                return authenticationManager.authenticate(authenticationToken);
            } catch (RuntimeException e) {

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("Email/Username Password Combination does not match");

                return null;
            }
        } catch (IOException e) {
            return null;
        }


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String accessToken = JWT.create().withSubject(user.getUsername()).withIssuer(request.getRequestURI())
                .sign(algorithm);

        String refreshToken = JWT.create().withSubject(user.getUsername()).withIssuer(request.getRequestURI())
                .sign(algorithm);
//        response.setHeader("accessToken",accessToken);
//        response.setHeader("refreshToken",refreshToken);
        Map<String, String> tokens = new HashMap<>();

        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(accessToken);
        String username = decodedJWT.getSubject();
        spring.framework.stackholder.domain.User isUserActive = userRepository.findAll().stream().filter(
                user1 -> user1.getEmail().equals(username)
        ).findFirst().get();
        LogInResponse logInResponse = new LogInResponse();

        {
            Response<LogInResponse> response1 = new Response<>();
            logInResponse.setId(isUserActive.getId());
            logInResponse.setEmail(isUserActive.getEmail());
            logInResponse.setUsername(isUserActive.getUsername());
            logInResponse.setFirstName(isUserActive.getFirstName());
            logInResponse.setLastName(isUserActive.getLastName());
            logInResponse.setIsAdmin(isUserActive.getIsAdmin());
            logInResponse.setAccessToken(accessToken);
            logInResponse.setRefreshToken(refreshToken);

            response1.setResponseCode(1);
            response1.setResponseMessage("LogIn Successfully");
            response1.setResponseBody(logInResponse);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), response1);
        }


//        tokens.put("accessToken",accessToken);
//        tokens.put("refreshToken",refreshToken);


    }

}
