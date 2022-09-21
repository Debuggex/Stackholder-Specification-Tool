package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Setter
@Getter
public class AdminUpdateStakeholderObjectiveDTO {

    private String updateType;

    private String id;

    private String name;

    @Nullable
    private String description;

}
