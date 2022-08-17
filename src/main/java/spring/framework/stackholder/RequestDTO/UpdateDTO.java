package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class UpdateDTO {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean isActive;

    @Nullable
    private String password;

}
