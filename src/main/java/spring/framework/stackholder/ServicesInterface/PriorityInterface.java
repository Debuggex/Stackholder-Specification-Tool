package spring.framework.stackholder.ServicesInterface;

import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.GetPriorityResponse;
import spring.framework.stackholder.ResponseDTO.PriorityResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.SetStakeholderObjectiveVerificationResponse;

import java.util.List;

public interface PriorityInterface {

    Response<PriorityResponseDTO> addPriority(PriorityDTO priorityDTO);

    Response<PriorityResponseDTO> deletePriority(DeletePriorityDTO deletePriorityDTO);

    Response<PriorityResponseDTO> updatePriority(UpdatePriorityDTO updatePriorityDTO);

    Response<List<GetPriorityResponse>> getPriority(GetPriorityDTO getPriorityDTO);

    Response<SetStakeholderObjectiveVerificationResponse> verify(SetStakeholderObjectiveVerifyDTO setStakeholderObjectiveVerifyDTO);

}
