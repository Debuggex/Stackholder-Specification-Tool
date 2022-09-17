package spring.framework.stackholder.Services;

import lombok.Synchronized;
import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.SetRespository;
import spring.framework.stackholder.Repositories.SetStakeholderRepository;
import spring.framework.stackholder.Repositories.StakeholderRepository;
import spring.framework.stackholder.RequestDTO.DeleteStakeholderDTO;
import spring.framework.stackholder.RequestDTO.GetStakeholdersDTO;
import spring.framework.stackholder.RequestDTO.StakeholderDTO;
import spring.framework.stackholder.RequestDTO.UpdateStakeholderDTO;
import spring.framework.stackholder.ResponseDTO.GetStakeholderResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.StakeholderResponseDTO;
import spring.framework.stackholder.ServicesInterface.StakeholderInterface;
import spring.framework.stackholder.StackHolderConstants.Constants;
import spring.framework.stackholder.domain.Set;
import spring.framework.stackholder.domain.SetStakeholder;
import spring.framework.stackholder.domain.Stakeholder;
import javax.transaction.Transactional;

@Service
public class StakeholderService implements StakeholderInterface {


    private final SetStakeholderRepository setStakeholderRepository;

    private final StakeholderRepository stakeholderRepository;

    private final SetRespository setRespository;

    public StakeholderService(SetStakeholderRepository setStakeholderRepository, StakeholderRepository stakeholderRepository, SetRespository setRespository) {
        this.setStakeholderRepository = setStakeholderRepository;
        this.stakeholderRepository = stakeholderRepository;
        this.setRespository = setRespository;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<StakeholderResponseDTO> addStakeholder(StakeholderDTO stakeholderDTO){

        /**
         * @Initialization
         */
        Response<StakeholderResponseDTO> response=new Response<>();
        Stakeholder stakeholder=new Stakeholder();
        SetStakeholder setStakeholder=new SetStakeholder();

        /**
         * @Checking if Stakeholder exists with input name
         */
        Set set = setRespository.findById(Long.valueOf(stakeholderDTO.getSetId())).get();
        boolean isStakeholder=set.getSetStakeholders().stream().anyMatch(
                stakeholder1 -> stakeholder1.getName().equals(stakeholderDTO.getName())
        );
        if (isStakeholder){
            response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
            response.setResponseMessage("Stakeholder with this name is already Exists. Try a different one.");
            return response;
        }

        /**
         * @Saving Stakeholder to StakeholderTable
         */
        stakeholder.setName(stakeholderDTO.getName());
        stakeholder.setDescription(stakeholderDTO.getDescription());
        Stakeholder savedStakeholder=stakeholderRepository.save(stakeholder);

        /**
         * @Saving Stakeholder to @SetStakeholderTable
         */
        setStakeholder.setName(stakeholderDTO.getName());
        setStakeholder.setDescription(stakeholderDTO.getDescription());
        SetStakeholder savedSetStakeholder=setStakeholderRepository.save(setStakeholder);
        set.addStakeholder(savedSetStakeholder);

        /**
         * @Setting up Response
         */

        StakeholderResponseDTO responseDTO=new StakeholderResponseDTO();
        responseDTO.setId(setStakeholder.getId());
        responseDTO.setName(setStakeholder.getName());
        responseDTO.setDescription(setStakeholder.getDescription());

        /**
         * @Returning Response
         */

        response.setResponseCode(1);
        response.setResponseMessage("Stakeholder Added Successfully");
        response.setResponseBody(responseDTO);
        return response;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<StakeholderResponseDTO> updateStakeholder(UpdateStakeholderDTO updateStakeholderDTO) {
        /**
         * @Initialization
         */
        Response<StakeholderResponseDTO> response=new Response<>();
        Stakeholder stakeholder=stakeholderRepository.findById(Long.parseLong(updateStakeholderDTO.getStakeholderId())-1L).get();
        SetStakeholder setStakeholder=setStakeholderRepository.findById(Long.valueOf(updateStakeholderDTO.getStakeholderId())).get();

        /**
         * @Checking if Stakeholder exists with input name
         */
        Set set = setRespository.findById(Long.valueOf(updateStakeholderDTO.getSetId())).get();
        boolean isStakeholder=set.getSetStakeholders().stream().anyMatch(
                stakeholder1 -> stakeholder1.getName().equals(updateStakeholderDTO.getName())
        );
        if (isStakeholder){
            response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
            response.setResponseMessage("Stakeholder with this name is already Exists. Try a different one.");
            return response;
        }

        /**
         * @Updating Stakeholder to StakeholderTable
         */
        stakeholder.setName(updateStakeholderDTO.getName());
        stakeholder.setDescription(updateStakeholderDTO.getDescription());
        stakeholderRepository.save(stakeholder);

        /**
         * @Updating Stakeholder to @SetStakeholderTable
         */
        setStakeholder.setName(updateStakeholderDTO.getName());
        setStakeholder.setDescription(updateStakeholderDTO.getDescription());
        setStakeholderRepository.save(setStakeholder);

        /**
         * @Setting up Response
         */

        StakeholderResponseDTO responseDTO=new StakeholderResponseDTO();
        responseDTO.setId(setStakeholder.getId());
        responseDTO.setName(setStakeholder.getName());
        responseDTO.setDescription(setStakeholder.getDescription());

        /**
         * @Returning response
         */

        response.setResponseCode(1);
        response.setResponseMessage("Stakeholder updated Successfully");
        response.setResponseBody(responseDTO);
        return response;

    }

    @Transactional
    @Synchronized
    @Override
    public Response<StakeholderResponseDTO> deleteStakeholder(DeleteStakeholderDTO deleteStakeholderDTO) {

        /**
         * @Initialization
         */
        Response<StakeholderResponseDTO> response=new Response<>();
        SetStakeholder setStakeholder=setStakeholderRepository.findById(Long.valueOf(deleteStakeholderDTO.getStakeholderId())).get();

        /**
         * @Deleting Stakeholder from Stakeholder and SetStakeholder
         */
        setStakeholderRepository.deleteById(Long.valueOf(deleteStakeholderDTO.getStakeholderId()));
        stakeholderRepository.deleteById(Long.parseLong(deleteStakeholderDTO.getStakeholderId())-1L);

        /**
         * @Setting up Response
         */

        StakeholderResponseDTO responseDTO=new StakeholderResponseDTO();
        responseDTO.setId(setStakeholder.getId());
        responseDTO.setName(setStakeholder.getName());
        responseDTO.setDescription(setStakeholder.getDescription());

        /**
         * @Returning response
         */

        response.setResponseCode(1);
        response.setResponseMessage("Stakeholder Deleted Successfully");
        response.setResponseBody(responseDTO);
        return response;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<GetStakeholderResponseDTO> getStakeholder(GetStakeholdersDTO getStakeholdersDTO) {

        /**
         * @Initilization
         */
        Response<GetStakeholderResponseDTO> response=new Response<>();
        GetStakeholderResponseDTO getStakeholderResponseDTO = new GetStakeholderResponseDTO();

        /**
         * @Getting Stakeholders
         */
        Set set = setRespository.findById(Long.valueOf(getStakeholdersDTO.getSetId())).get();
        for (int i = 0; i < set.getSetStakeholders().size(); i++) {
            StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();
            stakeholderResponseDTO.setId(set.getSetStakeholders().get(i).getId());
            stakeholderResponseDTO.setName(set.getSetStakeholders().get(i).getName());
            stakeholderResponseDTO.setDescription(set.getSetStakeholders().get(i).getDescription());
            getStakeholderResponseDTO.getStakeholderResponseDTOS().add(stakeholderResponseDTO);
        }

        /**
         * @Settting up Response
         */
        response.setResponseCode(1);
        response.setResponseMessage("Stakeholders have been fetched Successfully.");
        response.setResponseBody(getStakeholderResponseDTO);

        /**
         * @Returning the Response
         */
        return response;
    }


}
