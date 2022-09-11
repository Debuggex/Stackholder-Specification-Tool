package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class StakeholderDTO {

    private String setId;

    private String name;

    @Nullable
    private String description;
}
