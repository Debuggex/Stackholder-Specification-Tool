package spring.framework.stackholder.ServicesInterface;

import spring.framework.stackholder.RequestDTO.DeleteStakeholderDTO;
import spring.framework.stackholder.RequestDTO.GetStakeholdersDTO;
import spring.framework.stackholder.RequestDTO.StakeholderDTO;
import spring.framework.stackholder.RequestDTO.UpdateStakeholderDTO;
import spring.framework.stackholder.ResponseDTO.GetStakeholderResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.StakeholderResponseDTO;

public interface StakeholderInterface {

    Response<StakeholderResponseDTO> addStakeholder(StakeholderDTO stakeholderDTO);

    Response<StakeholderResponseDTO> updateStakeholder(UpdateStakeholderDTO updateStakeholderDTO);

    Response<StakeholderResponseDTO> deleteStakeholder(DeleteStakeholderDTO deleteStakeholderDTO);

    Response<GetStakeholderResponseDTO> getStakeholder(GetStakeholdersDTO getStakeholdersDTO);

}
