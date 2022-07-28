package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateForgotPasswordDTO {

    private String email;

    private String currentPassword;

    private String newPassword;
}
