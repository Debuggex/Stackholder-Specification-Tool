package spring.framework.stackholder.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetObjectivesResponseDTO {

    List<ObjectiveResponseDTO> objectiveResponseDTOS=new ArrayList<>();

}
