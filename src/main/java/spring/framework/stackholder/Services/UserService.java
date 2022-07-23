package spring.framework.stackholder.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.UserRepository;
import spring.framework.stackholder.RequestDTO.DeleteAccountDTO;
import spring.framework.stackholder.RequestDTO.SignUpDTO;
import spring.framework.stackholder.RequestDTO.UpdateDTO;
import spring.framework.stackholder.RequestDTO.UpdatePasswordDTO;
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
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    public UserService(UserRepository userRepository,@Lazy PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public Response<User> register(SignUpDTO signUpDTO, String siteUrl) throws MessagingException, UnsupportedEncodingException {

        Response<User> response=new Response<>();

        AtomicReference<Boolean> isUserNameExists= new AtomicReference<>(false);
        AtomicReference<Boolean> isUserExists= new AtomicReference<>(false);

        userRepository.findAll().forEach(
                user -> {
                    if (user.getName().equals(signUpDTO.getName())) {
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
            response.setResponseCode(Constants.USER_EXISTS);
            response.setResponseMessage("User is already registered with this Email! Try a Different One");
            response.setResponseBody(null);
            return response;
        }

        User user=new User();
        user.setEmail(signUpDTO.getEmail());
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setName(signUpDTO.getName());

        User savedUser=userRepository.save(user);
        Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
        String accessToken= JWT.create().withSubject(user.getEmail()).withExpiresAt(new Date(System.currentTimeMillis() +10*60*1000)).sign(algorithm);
        sendVerificationEmail(user,accessToken,siteUrl);
        response.setResponseCode(1);
        response.setResponseMessage("User registered Successfully!");
        response.setResponseBody(savedUser);
        return response;
    }

    private void sendVerificationEmail(User user, String token, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String userName= user.getName();
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


        return "Email Activated Successfully!";
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
        signUpDTO.setName(user.getName());

        userRepository.deleteById(deleteAccountDTO.getId());
        response.setResponseCode(1);
        response.setResponseMessage("Account Delete Successfully");
        response.setResponseBody(signUpDTO);
        return response;
    }

    public String forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {


        String RandomCode= RandomString.make(4);
        Optional<User> userOptional=userRepository.findAll().stream().filter(
                user1 -> user1.getEmail().equals(email)
        ).findFirst();
        if (userOptional.isEmpty()){
            return "Your email does not match to our any of Records";
        }
        User user=userOptional.get();

        Long id= user.getId();
        user.setPassword(RandomCode+ id);
        userRepository.save(user);


        String fullName=user.getFirstName()+" "+user.getLastName();
        String toEmail=user.getEmail();
        String fromEmail="sahbaanalam34@gmail.com";
        String subject="StackHolder Account Email Verification";
        String content = "Dear [[name]],<br>"
                + "We have received your Forgot Password Request. Below is your new current random generated password"
                + "<h1>[[code]]</h1>"
                + "Thank you,<br>"
                + "Copyright © 2012 - 2022 StackHolder Specification, all rights reserved.";

        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromEmail,"StackHolder Specification Tool");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setTo(toEmail);

        content = content.replace("[[name]]", fullName);

        content = content.replace("[[code]]", user.getPassword());

        mimeMessageHelper.setText(content, true);
        mailSender.send(mimeMessage);

        return "Forgot Password Request has been successfully to your email!";

    }

    public Response<UpdatePasswordResponse> updateForgotPassword(UpdatePasswordDTO updatePasswordDTO){
        UpdatePasswordResponse updatePasswordResponse=new UpdatePasswordResponse();
        Response<UpdatePasswordResponse> response=new Response<>();
        Long id=Long.valueOf(updatePasswordDTO.getCurrentPassword().substring(4));
        User user=userRepository.findById(id).get();
        user.setPassword(updatePasswordDTO.getNewPassword());
        userRepository.save(user);
        updatePasswordResponse.setNewPassword(user.getPassword());
        response.setResponseCode(1);
        response.setResponseMessage("Password has been changed successfully");
        response.setResponseBody(updatePasswordResponse);
        return response;
    }

    public Response<UpdatePasswordResponse> updatePassword(UpdatePasswordDTO updatePasswordDTO){
        String currentPassword=userRepository.findById(updatePasswordDTO.getId()).get().getPassword();
        updatePasswordDTO.setCurrentPassword(passwordEncoder.encode(updatePasswordDTO.getCurrentPassword()));
        UpdatePasswordResponse updatePasswordResponse=new UpdatePasswordResponse();
        Response<UpdatePasswordResponse> response=new Response<>();
        if (currentPassword.equals(updatePasswordDTO.getCurrentPassword())){
            User user=userRepository.findById(updatePasswordDTO.getId()).get();
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
            User user=isUserExists.get();
            user.setName(updateDTO.getName());
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
