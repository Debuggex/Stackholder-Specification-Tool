package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter

public class AdminAddSetStakeholderObjectiveDTO {


    private String addType;

    private String name;

    @Nullable
    private String description;

    private String setId;
}
