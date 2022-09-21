package spring.framework.stackholder.Services;

import lombok.Synchronized;
import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.SetRespository;
import spring.framework.stackholder.Repositories.SetStakeholderObjectiveRespository;
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
import spring.framework.stackholder.domain.SetStakeholderObjective;
import spring.framework.stackholder.domain.Stakeholder;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StakeholderService implements StakeholderInterface {


    private final SetStakeholderRepository setStakeholderRepository;

    private final SetStakeholderObjectiveRespository setStakeholderObjectiveRespository;

    private final SetRespository setRespository;

    public StakeholderService(SetStakeholderRepository setStakeholderRepository, SetStakeholderObjectiveRespository setStakeholderObjectiveRespository, SetRespository setRespository) {
        this.setStakeholderRepository = setStakeholderRepository;
        this.setStakeholderObjectiveRespository = setStakeholderObjectiveRespository;
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
        SetStakeholder setStakeholder=setStakeholderRepository.findById(Long.valueOf(updateStakeholderDTO.getStakeholderId())).get();

        /**
         * @Checking if Stakeholder exists with input name
         */
        Set set = setRespository.findById(Long.valueOf(updateStakeholderDTO.getSetId())).get();
        boolean isStakeholder=set.getSetStakeholders().stream().anyMatch(
                stakeholder1 -> stakeholder1.getName().equals(updateStakeholderDTO.getName()) && !Objects.equals(stakeholder1.getId(), Long.valueOf(updateStakeholderDTO.getStakeholderId()))
        );
        if (isStakeholder){
            response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
            response.setResponseMessage("Stakeholder with this name is already Exists. Try a different one.");
            return response;
        }

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
         * @Deleting Stakeholder from SetStakeholder
         */
        setStakeholderRepository.deleteById(Long.valueOf(deleteStakeholderDTO.getStakeholderId()));

        /**
         * @Deleting Stakeholder from SetStakeholderObjective(if Exists)
         */

        List<SetStakeholderObjective> setStakeholderObjectiveList = new ArrayList<>();
        setStakeholderObjectiveRespository.findAll().stream().forEach(
                setStakeholderObjective -> {
                    if (setStakeholderObjective.getSetStakeholder().getId().equals(Long.valueOf(deleteStakeholderDTO.getStakeholderId()))) {

                        setStakeholderObjectiveList.add(setStakeholderObjective);
                    }
                }
        );

        if (setStakeholderObjectiveList.size()!=0){
            for (int i = 0; i < setStakeholderObjectiveList.size(); i++) {
                setStakeholderObjectiveRespository.deleteById(setStakeholderObjectiveList.get(i).getId());
            }
        }

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
