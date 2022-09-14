package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class UpdateObjectiveDTO {

    private String setId;

    private String objectiveId;

    private String name;

    @Nullable
    private String description;
}
