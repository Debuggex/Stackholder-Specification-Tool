package spring.framework.stackholder.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.UserRepository;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.ForgotPasswordResponse;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.UpdatePasswordResponse;
import spring.framework.stackholder.StackHolderConstants.Constants;
import spring.framework.stackholder.domain.User;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;




    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Transactional
    public Response<User> register(SignUpDTO signUpDTO, String siteUrl) throws MessagingException, UnsupportedEncodingException {

        Response<User> response=new Response<>();

        AtomicReference<Boolean> isUserNameExists= new AtomicReference<>(false);
        AtomicReference<Boolean> isUserExists= new AtomicReference<>(false);

        userRepository.findAll().forEach(
                user -> {
                    if (user.getUsername().equals(signUpDTO.getUsername())) {
                        isUserNameExists.set(true);
                    }
                    if (user.getEmail().equals(signUpDTO.getEmail())) {
                        isUserExists.set(true);
                    }

                }
        );

        if (isUserNameExists.get()){
            response.setResponseCode(Constants.USERNAME_EXISTS);
            response.setResponseMessage("User is already registered with this Username! Try a Different One");
            response.setResponseBody(null);
            return response;
        }else if (isUserExists.get()){
            response.setResponseCode(Constants.EMAIL_EXISTS);
            response.setResponseMessage("User is already registered with this Email! Try a Different One");
            response.setResponseBody(null);
            return response;
        }

        User user=new User();
        user.setEmail(signUpDTO.getEmail());
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setUsername(signUpDTO.getUsername());

        User savedUser=userRepository.save(user);
        Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
        String accessToken= JWT.create().withSubject(user.getEmail()).sign(algorithm);
        sendVerificationEmail(user,accessToken,siteUrl);
        response.setResponseCode(1);
        response.setResponseMessage("User registered Successfully!");
        response.setResponseBody(savedUser);
        return response;
    }

    private void sendVerificationEmail(User user, String token, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String userName= user.getUsername();
        String fullName=user.getFirstName()+" "+user.getLastName();
        String toEmail=user.getEmail();
        String fromEmail="sahbaanalam34@gmail.com";
        String subject="StackHolder Account Email Verification";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Copyright © 2012 - 2022 StackHolder Specification, all rights reserved.";

        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromEmail,"StackHolder Specification Tool");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setTo(toEmail);

        content = content.replace("[[name]]", fullName);
        String verifyURL = siteURL + "/user/verify?code=" + token;

        content = content.replace("[[URL]]", verifyURL);

        mimeMessageHelper.setText(content, true);

        mailSender.send(mimeMessage);

    }

    public boolean checkUser(String username){

        return userRepository.findAll().stream().anyMatch(
                user -> user.getUsername().equals(username)
        );
    }

    public boolean checkEmail(String email){

        return userRepository.findAll().stream().anyMatch(
                user -> user.getEmail().equals(email)
        );
    }



    public String verifyUser(String code){
        Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
        JWTVerifier jwtVerifier= JWT.require(algorithm).build();
        DecodedJWT decodedJWT= jwtVerifier.verify(code);
        String email=decodedJWT.getSubject();

        User user=userRepository.findAll().stream().filter(
                user1 -> user1.getEmail().equals(email)
        ).findFirst().get();

        user.setIsActive(true);
        userRepository.save(user);


        return "VerifyEmail";
    }

    public List<User> get(){
        List<User> users=new ArrayList<>();

        userRepository.findAll().forEach(
                users::add
        );
        return users;
    }

    public Response<SignUpDTO> deleteUser(DeleteAccountDTO deleteAccountDTO){

        Response<SignUpDTO> response=new Response<>();
        User user=userRepository.findById(deleteAccountDTO.getId()).get();
        SignUpDTO signUpDTO=new SignUpDTO();
        signUpDTO.setEmail(user.getEmail());
        signUpDTO.setFirstName(user.getFirstName());
        signUpDTO.setLastName(user.getLastName());
        signUpDTO.setUsername(user.getUsername());

        userRepository.deleteById(deleteAccountDTO.getId());
        response.setResponseCode(1);
        response.setResponseMessage("Account Delete Successfully");
        response.setResponseBody(signUpDTO);
        return response;
    }

    public Response<ForgotPasswordResponse> forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {

        Response<ForgotPasswordResponse> response=new Response<>();
        ForgotPasswordResponse forgotPasswordResponse=new ForgotPasswordResponse();

        String RandomCode= RandomString.make(5);
        Optional<User> userOptional=userRepository.findAll().stream().filter(
                user1 -> user1.getEmail().equals(email)
        ).findFirst();
        if (userOptional.isEmpty()){
            response.setResponseCode(Constants.EMAIL_NOT_FOUND);
            response.setResponseMessage("Your email does not match to our any of Records");
            return response;
        }
        User user=userOptional.get();

        user.setPassword(RandomCode);
        userRepository.save(user);


        String fullName=user.getFirstName()+" "+user.getLastName();
        String toEmail=user.getEmail();
        String fromEmail="sahbaanalam34@gmail.com";
        String subject="Forgot Password Request";
        String content = "Dear [[name]],<br>"
                + "We have received your Forgot Password Request. Below is your new current random generated password. Use this password as your current password."
                + "<h1>[[code]]</h1>"
                + "Thank you,<br>"
                + "Copyright © 2012 - 2022 StackHolder Specification, all rights reserved.";

        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromEmail,"StackHolder Specification Tool");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setTo(toEmail);

        content = content.replace("[[name]]", fullName);

        content = content.replace("[[code]]", RandomCode);

        mimeMessageHelper.setText(content, true);
        mailSender.send(mimeMessage);

        forgotPasswordResponse.setEmail(email);
        response.setResponseCode(1);
        response.setResponseBody(forgotPasswordResponse);
        response.setResponseMessage("Password has been sent successfully to "+email);
        return response;

    }

    public Response<UpdatePasswordResponse> updateForgotPassword(UpdateForgotPasswordDTO updatePasswordDTO){
        UpdatePasswordResponse updatePasswordResponse=new UpdatePasswordResponse();
        Response<UpdatePasswordResponse> response=new Response<>();
        User user=userRepository.findAll().stream().filter(
                user1 -> user1.getEmail().equals(updatePasswordDTO.getEmail())
        ).findFirst().get();
        user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
        userRepository.save(user);
        updatePasswordResponse.setNewPassword(user.getPassword());
        response.setResponseCode(1);
        response.setResponseMessage("Password has been changed successfully");
        response.setResponseBody(updatePasswordResponse);
        return response;
    }

    public Response<UpdatePasswordResponse> updatePassword(UpdatePasswordDTO updatePasswordDTO){
        User user=userRepository.findById(updatePasswordDTO.getId()).get();
        boolean isSame= BCrypt.checkpw(updatePasswordDTO.getCurrentPassword(),user.getPassword());
        UpdatePasswordResponse updatePasswordResponse=new UpdatePasswordResponse();
        Response<UpdatePasswordResponse> response=new Response<>();
        if (isSame){
            user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
            userRepository.save(user);
            updatePasswordResponse.setNewPassword(user.getPassword());
            response.setResponseCode(1);
            response.setResponseMessage("Password Updated Succesfully!");
            response.setResponseBody(updatePasswordResponse);
            return response;
        }
        response.setResponseMessage("Current Password MisMatch");
        response.setResponseBody(null);
        response.setResponseCode(Constants.CURRENT_PASSWORD_MISMATCH);
        return response;
    }

    public Response<User> updateUser(UpdateDTO updateDTO){
        Optional<User> isUserExists=userRepository.findById(Long.valueOf(updateDTO.getId()));
        Response<User> response=new Response<>();
        if (isUserExists.isPresent()){

            if(userRepository.findAll().stream().anyMatch(
                    user -> {
                        if (user.getId().compareTo(Long.valueOf(updateDTO.getId()))!=0) {
                            if (user.getUsername().equals(updateDTO.getUsername())){
                                response.setResponseCode(Constants.EMAIL_EXISTS);
                                response.setResponseMessage("Username already Exists!Try different one");
                                response.setResponseBody(null);
                                return true;
                            }else if (user.getEmail().equals(updateDTO.getEmail())){
                                response.setResponseCode(Constants.EMAIL_EXISTS);
                                response.setResponseMessage("Email already registered! Try a different one!");
                                response.setResponseBody(null);
                                return true;
                            }

                        }
                        return false;
                    }
            )){
                return response;
            }


            User user=isUserExists.get();
            user.setUsername(updateDTO.getUsername());
            user.setFirstName(updateDTO.getFirstName());
            user.setLastName(updateDTO.getLastName());
            user.setEmail(updateDTO.getEmail());
            userRepository.save(user);
            response.setResponseBody(user);
        }
        response.setResponseCode(1);
        response.setResponseMessage("User Details Updated Successfully");
        return response;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> author=userRepository.findById(userRepository.findAll().stream().filter(
                author1 -> author1.getEmail().equals(username)
        ).findFirst().get().getId());

        if (author.isEmpty()){
            throw new UsernameNotFoundException("Author with this name does not exist");
        }else {
            Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));

            return new org.springframework.security.core.userdetails.User(author.get().getEmail(),author.get().getPassword(),authorities);
        }
    }
}
