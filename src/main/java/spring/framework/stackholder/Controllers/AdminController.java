package spring.framework.stackholder.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.*;
import spring.framework.stackholder.Services.AdminServices;
import spring.framework.stackholder.domain.User;

import javax.mail.MessagingException;
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
    public List<User> get() {
        return adminService.get();
    }

    @PutMapping("/update")
    public Response<User> update(@RequestBody @Validated UpdateDTO updateDTO) {
        return adminService.updateUser(updateDTO);
    }

    @PutMapping("/updatepassword")
    public Response<UpdatePasswordResponse> updatePassword(@RequestBody @Validated UpdatePasswordDTO updatePasswordDTO) {
        return adminService.updatePassword(updatePasswordDTO);
    }


    @DeleteMapping("/deleteaccount")
    public Response<SignUpDTO> deleteUser(@RequestBody @Validated DeleteAccountDTO deleteAccountDTO) {

        return adminService.deleteUser(deleteAccountDTO);

    }

    @PostMapping("/checkusername")
    public Boolean checkUsername(@RequestBody @Validated CheckUsernameDTO checkUsernameDTO) {
        return adminService.checkUser(checkUsernameDTO.getUsername());
    }

    @PostMapping("/checkemail")
    public Boolean checkEmail(@RequestBody @Validated CheckEmailDTO checkEmailDTO) {
        return adminService.checkEmail(checkEmailDTO.getEmail());
    }

    @GetMapping("/getObjectivesStakeholders")
    public ResponseEntity<GetObjectivesStakeholdersResponseDTO> getObjectives(){
        return new ResponseEntity<>(adminService.getObjectivesStakeholders(), HttpStatus.OK);
    }

    @PutMapping("/updateStakeholderObjective")
    public ResponseEntity<Response<StakeholderResponseDTO>> updateStakeholderObjective(@RequestBody @Validated AdminUpdateStakeholderObjectiveDTO adminUpdateStakeholderObjectiveDTO){

        Response<StakeholderResponseDTO> response= adminService.updateStakeholderObjective(adminUpdateStakeholderObjectiveDTO);
        if (response.getResponseBody() == null) {
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PostMapping("/addStakeholderObjective")
    public ResponseEntity<Response<StakeholderResponseDTO>> addStakeholderObjective(@RequestBody @Validated AdminAddStakeholderObjectiveDTO adminAddStakeholderObjectiveDTO){

        Response<StakeholderResponseDTO> response= adminService.addStakeholderObjective(adminAddStakeholderObjectiveDTO);
        if (response.getResponseBody() == null) {
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @DeleteMapping("/deleteStakeholderObjective")
    public ResponseEntity<Response<StakeholderResponseDTO>> deleteStakeholderObjective(@RequestBody @Validated AdminDeleteStakeholderObjectiveDTO adminDeleteStakeholderObjectiveDTO){

        return new ResponseEntity<>(adminService.deleteStakeholderObjective(adminDeleteStakeholderObjectiveDTO),HttpStatus.OK);

    }

    @GetMapping("/getSets")
    public ResponseEntity<Response<GetSetsResponseDTO>> getSets(){

        return new ResponseEntity<>(adminService.getSets(),HttpStatus.OK);

    }
}
