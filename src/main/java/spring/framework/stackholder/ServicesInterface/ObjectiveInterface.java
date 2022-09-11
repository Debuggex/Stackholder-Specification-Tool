package spring.framework.stackholder.ServicesInterface;

import spring.framework.stackholder.RequestDTO.DeleteObjectiveDTO;
import spring.framework.stackholder.RequestDTO.GetObjectivesDTO;
import spring.framework.stackholder.RequestDTO.ObjectivesDTO;
import spring.framework.stackholder.RequestDTO.UpdateObjectiveDTO;
import spring.framework.stackholder.ResponseDTO.GetObjectivesResponseDTO;
import spring.framework.stackholder.ResponseDTO.ObjectiveResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;

public interface ObjectiveInterface {

    Response<ObjectiveResponseDTO> addObjective(ObjectivesDTO objectivesDTO);

    Response<ObjectiveResponseDTO> updateObjective(UpdateObjectiveDTO updateObjectiveDTO);

    Response<ObjectiveResponseDTO> deleteObjective(DeleteObjectiveDTO deleteObjectiveDTO);

    Response<GetObjectivesResponseDTO> getObjectives(GetObjectivesDTO getObjectivesDTO);

}
