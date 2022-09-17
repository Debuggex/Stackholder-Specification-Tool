package spring.framework.stackholder.Services;

import lombok.Synchronized;
import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.*;
import spring.framework.stackholder.RequestDTO.DeletePriorityDTO;
import spring.framework.stackholder.RequestDTO.GetPriorityDTO;
import spring.framework.stackholder.ResponseDTO.GetPriorityResponse;
import spring.framework.stackholder.RequestDTO.PriorityDTO;
import spring.framework.stackholder.RequestDTO.UpdatePriorityDTO;
import spring.framework.stackholder.ResponseDTO.PriorityResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ServicesInterface.PriorityInterface;
import spring.framework.stackholder.StackHolderConstants.Constants;
import spring.framework.stackholder.domain.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PriorityServices implements PriorityInterface {

    private final SetRespository setRespository;

    private final SetStakeholderRepository setStakeholderRepository;

    private final SetObjectiveRespository setObjectiveRespository;

    private final SetStakeholderObjectiveRespository setStakeholderObjectiveRespository;

    public PriorityServices(SetRespository setRespository, SetStakeholderRepository setStakeholderRepository, SetObjectiveRespository setObjectiveRespository, SetStakeholderObjectiveRespository setStakeholderObjectiveRespository) {
        this.setRespository = setRespository;
        this.setStakeholderRepository = setStakeholderRepository;
        this.setObjectiveRespository = setObjectiveRespository;
        this.setStakeholderObjectiveRespository = setStakeholderObjectiveRespository;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<PriorityResponseDTO> addPriority(PriorityDTO priorityDTO) {

        /**
         * @Initialization
         */
        Response<PriorityResponseDTO> response=new Response<>();
        PriorityResponseDTO priorityResponseDTO=new PriorityResponseDTO();

        /**
         * @Checking if priority for given stakeholder exists
         */

        boolean isPriorityExists=setStakeholderObjectiveRespository.findAll().stream().anyMatch(
                priority -> priority.getSetId().getId().equals(Long.valueOf(priorityDTO.getSetId())) &&
                        priority.getSetObjective().getId().equals(Long.valueOf(priorityDTO.getObjectiveId())) &&
                        priority.getSetStakeholder().getId().equals(Long.valueOf(priorityDTO.getStakeholderId()))
        );

        if (isPriorityExists){
            response.setResponseCode(Constants.PRIORITY_EXISTS);
            response.setResponseMessage("Stakeholder with given objective has already some Priority. Try updating it.");
            return response;
        }

        /**
         * @Creating Priority for Stakeholder
         */
        Set set = setRespository.findById(Long.valueOf(priorityDTO.getSetId())).get();
        SetObjective objective = setObjectiveRespository.findById(Long.valueOf(priorityDTO.getObjectiveId())).get();
        SetStakeholder stakeholder = setStakeholderRepository.findById(Long.valueOf(priorityDTO.getStakeholderId())).get();

        SetStakeholderObjective setStakeholderObjective=new SetStakeholderObjective();
        Priority priority= Arrays.stream(Priority.values()).filter(
                priority1 -> priority1.name().equals(priorityDTO.getPriority())
        ).findFirst().get();
        setStakeholderObjective.setPriority(priority);
        SetStakeholderObjective saved=setStakeholderObjectiveRespository.save(setStakeholderObjective);
        set.addPriority(saved);
        objective.addPriority(saved);
        stakeholder.addPriority(saved);

        /**
         * @Setting up Response
         */
        priorityResponseDTO.setPriority(saved.getPriority().name());
        priorityResponseDTO.setPriorityId(String.valueOf(saved.getId()));
        response.setResponseCode(1);
        response.setResponseMessage("Priority has been added Successfully");
        response.setResponseBody(priorityResponseDTO);

        /**
         * @Returning the response
         */

        return response;
    }

    @Synchronized
    @Transactional
    @Override
    public Response<PriorityResponseDTO> deletePriority(DeletePriorityDTO deletePriorityDTO) {

        /**
         * @Initialization
         */

        Response<PriorityResponseDTO> response=new Response<>();
        PriorityResponseDTO priorityResponseDTO=new PriorityResponseDTO();

        /**
         * @Deleting
         */

        SetStakeholderObjective setStakeholderObjective=setStakeholderObjectiveRespository.findById(Long.valueOf(deletePriorityDTO.getId())).get();
        setStakeholderObjectiveRespository.deleteById(Long.valueOf(deletePriorityDTO.getId()));

        /**
         * @Setting up Response
         */

        priorityResponseDTO.setPriority(setStakeholderObjective.getPriority().name());
        priorityResponseDTO.setPriorityId(String.valueOf(setStakeholderObjective.getId()));

        response.setResponseCode(1);
        response.setResponseMessage("Priority has been deleted Successfully");
        response.setResponseBody(priorityResponseDTO);

        /**
         * @Returning the Response
         */
        return response;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<PriorityResponseDTO> updatePriority(UpdatePriorityDTO updatePriorityDTO) {

        /**
         * @Initialization
         */

        Response<PriorityResponseDTO> response=new Response<>();
        PriorityResponseDTO priorityResponseDTO=new PriorityResponseDTO();

        /**
         * @Updating the Priority
         */
        SetStakeholderObjective setStakeholderObjective=setStakeholderObjectiveRespository.findById(Long.valueOf(updatePriorityDTO.getId())).get();
        Priority priority= Arrays.stream(Priority.values()).filter(
                priority1 -> priority1.name().equals(updatePriorityDTO.getPriority())
        ).findFirst().get();
        setStakeholderObjective.setPriority(priority);
        setStakeholderObjectiveRespository.save(setStakeholderObjective);

        /**
         * @Setting up Response
         */
        priorityResponseDTO.setPriority(setStakeholderObjective.getPriority().name());
        priorityResponseDTO.setPriorityId(String.valueOf(setStakeholderObjective.getId()));
        response.setResponseCode(1);
        response.setResponseMessage("Priority has been updated Successfully");
        response.setResponseBody(priorityResponseDTO);

        /**
         * @Returning Response
         */
        return response;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<List<GetPriorityResponse>> getPriority(GetPriorityDTO getPriorityDTO) {

        /**
         * @Initialization
         */

        Response<List<GetPriorityResponse>> response=new Response<>();
        List<GetPriorityResponse> getPriorityResponseList=new ArrayList<>();

        /**
         * @Getting Priority
         */

        List<SetStakeholderObjective> setStakeholderObjective=new ArrayList<>();
        setStakeholderObjectiveRespository.findAll().stream().forEach(
                setStakeholderObjective1 -> {
                    if (setStakeholderObjective1.getSetStakeholder().getId().equals(Long.valueOf(getPriorityDTO.getStakeholderId()))) {
                        setStakeholderObjective.add(setStakeholderObjective1);
                    }
                }
        );

        for (int i = 0; i < setStakeholderObjective.size(); i++) {
            GetPriorityResponse getPriorityResponse=new GetPriorityResponse();
            getPriorityResponse.setId(String.valueOf(setStakeholderObjective.get(i).getId()));
            getPriorityResponse.setObjectiveName(setStakeholderObjective.get(i).getSetObjective().getName());
            getPriorityResponse.setStakeholderName(setStakeholderObjective.get(i).getSetStakeholder().getName());
            getPriorityResponse.setPriority(setStakeholderObjective.get(i).getPriority().name());

            getPriorityResponseList.add(getPriorityResponse);
        }


        /**
         * @Setting up Response
         */

        response.setResponseCode(1);
        response.setResponseMessage("Priority has been fetched Successfully");
        response.setResponseBody(getPriorityResponseList);

        /**
         * @Returning Response
         */

        return response;
    }


}
