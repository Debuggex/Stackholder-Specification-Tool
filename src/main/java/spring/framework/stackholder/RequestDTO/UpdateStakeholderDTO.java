package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class UpdateStakeholderDTO {

    private String setId;

    private String stakeholderId;

    private String name;

    @Nullable
    private String description;
}
