package spring.framework.stackholder.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPriorityResponse {

    private String id;

    private String stakeholderName;

    private String objectiveName;

    private String priority;
}
