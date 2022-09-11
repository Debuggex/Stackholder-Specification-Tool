package spring.framework.stackholder.ServicesInterface;

import spring.framework.stackholder.RequestDTO.DeletePriorityDTO;
import spring.framework.stackholder.RequestDTO.GetPriorityDTO;
import spring.framework.stackholder.ResponseDTO.GetPriorityResponse;
import spring.framework.stackholder.RequestDTO.PriorityDTO;
import spring.framework.stackholder.RequestDTO.UpdatePriorityDTO;
import spring.framework.stackholder.ResponseDTO.PriorityResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;

public interface PriorityInterface {

    Response<PriorityResponseDTO> addPriority(PriorityDTO priorityDTO);

    Response<PriorityResponseDTO> deletePriority(DeletePriorityDTO deletePriorityDTO);

    Response<PriorityResponseDTO> updatePriority(UpdatePriorityDTO updatePriorityDTO);

    Response<GetPriorityResponse> getPriority(GetPriorityDTO getPriorityDTO);

}
