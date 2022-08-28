package spring.framework.stackholder.Controllers;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.framework.stackholder.Services.UserService;

@RequestMapping("/user")
@Controller
public class ViewController {


    private final UserService userService;

    public ViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        return userService.verifyUser(code);
    }
}
