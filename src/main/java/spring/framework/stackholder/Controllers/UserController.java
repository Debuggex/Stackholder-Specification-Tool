package spring.framework.stackholder.Controllers;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.ForgotPasswordResponse;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.UpdatePasswordResponse;
import spring.framework.stackholder.Services.UserService;
import spring.framework.stackholder.domain.User;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping(path = "/user")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<User> signUp(@RequestBody @Validated SignUpDTO signUpDTO, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        return userService.register(signUpDTO, getSiteURL(request));
    }

    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/get")
    public List<User> get() {
        return userService.get();
    }

    @PutMapping("/update")
    public Response<User> update(@RequestBody @Validated UpdateDTO updateDTO) {
        return userService.updateUser(updateDTO);
    }

    @PutMapping("/updatepassword")
    public Response<UpdatePasswordResponse> updatePassword(@RequestBody @Validated UpdatePasswordDTO updatePasswordDTO) {
        return userService.updatePassword(updatePasswordDTO);
    }

    @PutMapping("/forgotpassword")
    public Response<ForgotPasswordResponse> forgotPassword(@RequestBody @Validated ForgotPasswordDTO forgotPasswordDTO) throws MessagingException, UnsupportedEncodingException {
        return userService.forgotPassword(forgotPasswordDTO.getEmail());
    }

    @PutMapping("/updateforgotpassword")
    public Response<UpdatePasswordResponse> updateForgotPassword(@RequestBody @Validated UpdateForgotPasswordDTO updatePasswordDTO) {
        return userService.updateForgotPassword(updatePasswordDTO);
    }

    @DeleteMapping("/deleteaccount")
    public Response<SignUpDTO> deleteUser(@RequestBody @Validated DeleteAccountDTO deleteAccountDTO) {

        return userService.deleteUser(deleteAccountDTO);

    }

    @PostMapping("/checkusername")
    public Boolean checkUsername(@RequestBody @Validated CheckUsernameDTO checkUsernameDTO) {
        return userService.checkUser(checkUsernameDTO.getUsername());
    }

    @PostMapping("/checkemail")
    public Boolean checkEmail(@RequestBody @Validated CheckEmailDTO checkEmailDTO) {
        return userService.checkEmail(checkEmailDTO.getEmail());
    }
}
