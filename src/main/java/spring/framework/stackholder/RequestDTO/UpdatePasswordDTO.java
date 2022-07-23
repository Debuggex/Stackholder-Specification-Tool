package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDTO {

    private Long id;

    private String currentPassword;

    private String newPassword;

}
