package spring.framework.stackholder.Controllers;

import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.UpdatePasswordResponse;
import spring.framework.stackholder.Services.UserService;
import spring.framework.stackholder.domain.User;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public Response<User> signUp(@RequestBody @Validated SignUpDTO signUpDTO, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        return userService.register(signUpDTO,getSiteURL(request));
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
    @GetMapping("/get")
    public List<User> get(){
        return userService.get();
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code){
        return userService.verifyUser(code);
    }

    @PutMapping("/update")
    public Response<User> update(@RequestBody @Validated UpdateDTO updateDTO){
        return userService.updateUser(updateDTO);
    }

    @PutMapping("/updatepassword")
    public Response<UpdatePasswordResponse> updatePassword(@RequestBody @Validated UpdatePasswordDTO updatePasswordDTO){
        return userService.updatePassword(updatePasswordDTO);
    }

    @PutMapping("/forgotpassword")
    public String forgotPassword(@RequestBody @Validated ForgotPasswordDTO forgotPasswordDTO) throws MessagingException, UnsupportedEncodingException {
        return userService.forgotPassword(forgotPasswordDTO.getEmail());
    }

    @PutMapping("/updateforgotpassword")
    public Response<UpdatePasswordResponse> updateForgotPassword(@RequestBody @Validated UpdatePasswordDTO updatePasswordDTO){
        return userService.updateForgotPassword(updatePasswordDTO);
    }

    @DeleteMapping("/deleteaccount")
    public Response<SignUpDTO> deleteUser(@RequestBody @Validated DeleteAccountDTO deleteAccountDTO){

        return userService.deleteUser(deleteAccountDTO);

    }

}
