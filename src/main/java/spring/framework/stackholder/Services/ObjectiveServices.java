package spring.framework.stackholder.Services;

import lombok.Synchronized;
import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.ObjectiveRepository;
import spring.framework.stackholder.Repositories.SetObjectiveRespository;
import spring.framework.stackholder.Repositories.SetRespository;
import spring.framework.stackholder.RequestDTO.DeleteObjectiveDTO;
import spring.framework.stackholder.RequestDTO.GetObjectivesDTO;
import spring.framework.stackholder.RequestDTO.ObjectivesDTO;
import spring.framework.stackholder.RequestDTO.UpdateObjectiveDTO;
import spring.framework.stackholder.ResponseDTO.GetObjectivesResponseDTO;
import spring.framework.stackholder.ResponseDTO.ObjectiveResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ServicesInterface.ObjectiveInterface;
import spring.framework.stackholder.StackHolderConstants.Constants;
import spring.framework.stackholder.domain.Objective;
import spring.framework.stackholder.domain.Set;
import spring.framework.stackholder.domain.SetObjective;
import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class ObjectiveServices implements ObjectiveInterface {

    private final SetRespository setRespository;

    private final ObjectiveRepository objectiveRepository;

    private final SetObjectiveRespository setObjectiveRespository;

    public ObjectiveServices(SetRespository setRespository, ObjectiveRepository objectiveRepository, SetObjectiveRespository setObjectiveRespository) {
        this.setRespository = setRespository;
        this.objectiveRepository = objectiveRepository;
        this.setObjectiveRespository = setObjectiveRespository;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<ObjectiveResponseDTO> addObjective(ObjectivesDTO objectivesDTO) {

        /**
         * @Initialization
         */

        Response<ObjectiveResponseDTO> response = new Response<>();
        ObjectiveResponseDTO responseDTO=new ObjectiveResponseDTO();
        Objective objective=new Objective();
        SetObjective setObjective= new SetObjective();

        /**
         * @Checking if Objective exists with input name
         */
        Set set = setRespository.findById(Long.valueOf(objectivesDTO.getSetId())).get();
        boolean isObjective=set.getSetObjectives().stream().anyMatch(
                objective1 -> objective1.getName().equals(objectivesDTO.getName())
        );
        if (isObjective){
            response.setResponseCode(Constants.OBJECTIVE_NAME_EXISTS);
            response.setResponseMessage("Objective with this name is already Exists. Try a different one.");
            return response;
        }


        /**
         * @Saving Objective to ObjectiveTable
         */
        objective.setName(objectivesDTO.getName());
        objective.setDescription(objectivesDTO.getDescription());
        Objective savedObjective=objectiveRepository.save(objective);

        /**
         * @Saving Objective to SetObjectiveTable
         */
        setObjective.setName(objectivesDTO.getName());
        setObjective.setDescription(objectivesDTO.getDescription());
        SetObjective savedSetObjective=setObjectiveRespository.save(setObjective);
        set.addObjective(savedSetObjective);

        /**
         * @Setting up Response
         */


        responseDTO.setId(setObjective.getId());
        responseDTO.setName(setObjective.getName());
        responseDTO.setDescription(setObjective.getDescription());

        /**
         * @Returning Response
         */

        response.setResponseCode(1);
        response.setResponseMessage("Objective has been added Successfully");
        response.setResponseBody(responseDTO);
        return response;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<ObjectiveResponseDTO> updateObjective(UpdateObjectiveDTO updateObjectiveDTO) {

        /**
         * @Initialization
         */

        Response<ObjectiveResponseDTO> response = new Response<>();
        ObjectiveResponseDTO responseDTO=new ObjectiveResponseDTO();
        Objective objective=objectiveRepository.findById(Long.parseLong(updateObjectiveDTO.getObjectiveId())-1L).get();
        SetObjective setObjective= setObjectiveRespository.findById(Long.valueOf(updateObjectiveDTO.getObjectiveId())).get();

        /**
         * @Checking if Objective exists with input name
         */
        Set set = setRespository.findById(Long.valueOf(updateObjectiveDTO.getSetId())).get();
        boolean isObjective=set.getSetObjectives().stream().anyMatch(
                objective1 -> objective1.getName().equals(updateObjectiveDTO.getName()) && !Objects.equals(objective1.getId(),Long.valueOf(updateObjectiveDTO.getObjectiveId()))
        );
        if (isObjective){
            response.setResponseCode(Constants.OBJECTIVE_NAME_EXISTS);
            response.setResponseMessage("Objective with this name is already Exists. Try a different one.");
            return response;
        }

        /**
         * @Updating Objective to ObjectiveTable
         */
        objective.setName(updateObjectiveDTO.getName());
        objective.setDescription(updateObjectiveDTO.getDescription());
        objectiveRepository.save(objective);

        /**
         * @Updating Objective to SetObjectiveTable
         */
        setObjective.setName(updateObjectiveDTO.getName());
        setObjective.setDescription(updateObjectiveDTO.getDescription());
        setObjectiveRespository.save(setObjective);

        /**
         * @Setting up Response
         */

        responseDTO.setId(setObjective.getId());
        responseDTO.setName(setObjective.getName());
        responseDTO.setDescription(setObjective.getDescription());

        /**
         * @Returning response
         */

        response.setResponseCode(1);
        response.setResponseMessage("Objective has been updated Successfully");
        response.setResponseBody(responseDTO);
        return response;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<ObjectiveResponseDTO> deleteObjective(DeleteObjectiveDTO deleteObjectiveDTO) {

        /**
         * @Initialization
         */
        Response<ObjectiveResponseDTO> response=new Response<>();
        SetObjective setObjective=setObjectiveRespository.findById(Long.valueOf(deleteObjectiveDTO.getObjectiveId())).get();

        /**
         * @Deleting Stakeholder from Objective and SetObjective
         */
        setObjectiveRespository.deleteById(Long.valueOf(deleteObjectiveDTO.getObjectiveId()));
        objectiveRepository.deleteById(Long.parseLong(deleteObjectiveDTO.getObjectiveId())-1L);

        /**
         * @Setting up Response
         */

        ObjectiveResponseDTO responseDTO=new ObjectiveResponseDTO();
        responseDTO.setId(setObjective.getId());
        responseDTO.setName(setObjective.getName());
        responseDTO.setDescription(setObjective.getDescription());

        /**
         * @Returning response
         */

        response.setResponseCode(1);
        response.setResponseMessage("Objective Deleted Successfully");
        response.setResponseBody(responseDTO);
        return response;
    }

    @Transactional
    @Synchronized
    @Override
    public Response<GetObjectivesResponseDTO> getObjectives(GetObjectivesDTO getObjectivesDTO) {

        /**
         * @Initilization
         */
        Response<GetObjectivesResponseDTO> response=new Response<>();
        GetObjectivesResponseDTO getObjectivesResponseDTO=new GetObjectivesResponseDTO();

        /**
         * @Getting Objectives
         */
        Set set = setRespository.findById(Long.valueOf(getObjectivesDTO.getSetId())).get();
        for (int i = 0; i < set.getSetObjectives().size(); i++) {
            ObjectiveResponseDTO objectiveResponseDTO=new ObjectiveResponseDTO();
            objectiveResponseDTO.setId(set.getSetObjectives().get(i).getId());
            objectiveResponseDTO.setName(set.getSetObjectives().get(i).getName());
            objectiveResponseDTO.setDescription(set.getSetObjectives().get(i).getDescription());
            getObjectivesResponseDTO.getObjectiveResponseDTOS().add(objectiveResponseDTO);
        }

        /**
         * @Settting up Response
         */
        response.setResponseCode(1);
        response.setResponseMessage("Objectives have been fetched Successfully.");
        response.setResponseBody(getObjectivesResponseDTO);

        /**
         * @Returning the Response
         */
        return response;

    }
}
