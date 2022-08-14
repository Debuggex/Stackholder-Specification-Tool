package spring.framework.stackholder.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AdminServices {

    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;




    public AdminServices(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Transactional
    public Response<User> register(SignUpDTO signUpDTO) throws MessagingException, UnsupportedEncodingException {

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
            response.setResponseMessage("User is already registered with this Username. Try a Different One");
            response.setResponseBody(null);
            return response;
        }else if (isUserExists.get()){
            response.setResponseCode(Constants.EMAIL_EXISTS);
            response.setResponseMessage("User is already registered with this Email. Try a Different One");
            response.setResponseBody(null);
            return response;
        }

        User user=new User();
        user.setEmail(signUpDTO.getEmail());
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setUsername(signUpDTO.getUsername());
        user.setIsActive(true);
        User savedUser=userRepository.save(user);

        response.setResponseCode(1);
        response.setResponseMessage("User registered Successfully");
        response.setResponseBody(savedUser);
        return response;
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

    public List<User> get(){
        List<User> users=new ArrayList<>();

        userRepository.findAll().forEach(
                user ->
                {
                    if (!user.getIsAdmin()) {
                        users.add(user);
                    }
                }
        );

        return users;
    }

    public Response<SignUpDTO> deleteUser(DeleteAccountDTO deleteAccountDTO){

        Response<SignUpDTO> response=new Response<>();
        User user=userRepository.findById(deleteAccountDTO.getId()).get();
        if (user.getIsAdmin()){
            response.setResponseCode(Constants.ADMIN_ACCOUNT_DEL_FAILED);
            response.setResponseMessage("Account Deletion Failed. Please Contact Customer Support");
            response.setResponseBody(null);
            return response;
        }
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
            response.setResponseMessage("Password Updated Succesfully");
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
                                response.setResponseMessage("Username already Exists. Try different one");
                                response.setResponseBody(null);
                                return true;
                            }else if (user.getEmail().equals(updateDTO.getEmail())){
                                response.setResponseCode(Constants.EMAIL_EXISTS);
                                response.setResponseMessage("Email already registered. Try a different one.");
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
}
