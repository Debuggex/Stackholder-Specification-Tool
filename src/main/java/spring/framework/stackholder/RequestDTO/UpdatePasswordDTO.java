package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class UpdatePasswordDTO {

    private String email;

    private String currentPassword;

    private String newPassword;

}
