package spring.framework.stackholder.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.DeleteObjectiveDTO;
import spring.framework.stackholder.RequestDTO.GetObjectivesDTO;
import spring.framework.stackholder.RequestDTO.ObjectivesDTO;
import spring.framework.stackholder.RequestDTO.UpdateObjectiveDTO;
import spring.framework.stackholder.ResponseDTO.GetObjectivesResponseDTO;
import spring.framework.stackholder.ResponseDTO.ObjectiveResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.Services.ObjectiveServices;

@RestController
@RequestMapping("/objective")
public class ObjectiveController {

    private final ObjectiveServices objectiveServices;

    public ObjectiveController(ObjectiveServices objectiveServices) {
        this.objectiveServices = objectiveServices;
    }

    @PostMapping(value = "/addObjective", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<ObjectiveResponseDTO>> addObjective(@RequestBody @Validated ObjectivesDTO objectivesDTO){

        Response<ObjectiveResponseDTO> response=objectiveServices.addObjective(objectivesDTO);

        if (response.getResponseBody() == null) return new ResponseEntity<>(response, HttpStatus.CONFLICT);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PutMapping(value = "/updateObjective", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<ObjectiveResponseDTO>> updateObjective(@RequestBody @Validated UpdateObjectiveDTO updateObjectiveDTO){

        Response<ObjectiveResponseDTO> response=objectiveServices.updateObjective(updateObjectiveDTO);

        if (response.getResponseBody() == null) return new ResponseEntity<>(response, HttpStatus.CONFLICT);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @DeleteMapping(value = "/deleteObjective", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<ObjectiveResponseDTO>> deleteObjective(@RequestBody @Validated DeleteObjectiveDTO deleteObjectiveDTO) {

        return new ResponseEntity<>(objectiveServices.deleteObjective(deleteObjectiveDTO),HttpStatus.OK);

    }

    @PostMapping(value = "/getObjectives", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<GetObjectivesResponseDTO>> getObjective(@RequestBody @Validated GetObjectivesDTO getObjectivesDTO){

        return new ResponseEntity<>(objectiveServices.getObjectives(getObjectivesDTO),HttpStatus.OK);

    }

}
