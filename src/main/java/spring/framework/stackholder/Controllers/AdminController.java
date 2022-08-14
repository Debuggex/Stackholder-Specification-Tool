package spring.framework.stackholder.Controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.ForgotPasswordResponse;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.UpdatePasswordResponse;
import spring.framework.stackholder.Services.AdminServices;
import spring.framework.stackholder.Services.UserService;
import spring.framework.stackholder.domain.User;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminServices adminService;

    public AdminController(AdminServices adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/signup")
    public Response<User> signUp(@RequestBody @Validated SignUpDTO signUpDTO) throws MessagingException, UnsupportedEncodingException {
        return adminService.register(signUpDTO);
    }

    @GetMapping("/get")
    public List<User> get(){
        return adminService.get();
    }

    @PutMapping("/update")
    public Response<User> update(@RequestBody @Validated UpdateDTO updateDTO){
        return adminService.updateUser(updateDTO);
    }

    @PutMapping("/updatepassword")
    public Response<UpdatePasswordResponse> updatePassword(@RequestBody @Validated UpdatePasswordDTO updatePasswordDTO){
        return adminService.updatePassword(updatePasswordDTO);
    }


    @DeleteMapping("/deleteaccount")
    public Response<SignUpDTO> deleteUser(@RequestBody @Validated DeleteAccountDTO deleteAccountDTO){

        return adminService.deleteUser(deleteAccountDTO);

    }

    @PostMapping("/checkusername")
    public Boolean checkUsername(@RequestBody @Validated CheckUsernameDTO checkUsernameDTO){
        return adminService.checkUser(checkUsernameDTO.getUsername());
    }

    @PostMapping("/checkemail")
    public Boolean checkEmail(@RequestBody @Validated CheckEmailDTO checkEmailDTO){
        return adminService.checkEmail(checkEmailDTO.getEmail());
    }
}
