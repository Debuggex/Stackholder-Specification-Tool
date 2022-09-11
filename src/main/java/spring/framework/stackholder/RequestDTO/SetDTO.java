package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class SetDTO {

    private String userId;

    private String name;

    @Nullable
    private String description;
}
