package spring.framework.stackholder.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStakeholderDTO {

    private String setId;

    private String stakeholderId;

    private String name;

    private String description;
}
