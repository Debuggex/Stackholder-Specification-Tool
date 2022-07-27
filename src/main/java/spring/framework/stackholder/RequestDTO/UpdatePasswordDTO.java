package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class UpdatePasswordDTO {

    @Nullable
    private Long id;

    private String currentPassword;

    private String newPassword;

}
