package spring.framework.stackholder.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.GetPriorityResponse;
import spring.framework.stackholder.ResponseDTO.PriorityResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.SetStakeholderObjectiveVerificationResponse;
import spring.framework.stackholder.Services.PriorityServices;

import javax.print.attribute.standard.Media;
import java.util.List;

@RestController
@RequestMapping("/priority")
public class PriorityController {


    private final PriorityServices priorityServices;

    public PriorityController(PriorityServices priorityServices) {
        this.priorityServices = priorityServices;
    }

    @PostMapping(value = "/addPriority", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<PriorityResponseDTO>> addPriority(@RequestBody @Validated PriorityDTO priorityDTO){

        Response<PriorityResponseDTO> response= priorityServices.addPriority(priorityDTO);
        if (response.getResponseBody() == null) return new ResponseEntity<>(response, HttpStatus.CONFLICT);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @DeleteMapping(value = "/deletePriority",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<PriorityResponseDTO>> deletePriority(@RequestBody @Validated DeletePriorityDTO deletePriorityDTO){

        return new ResponseEntity<>(priorityServices.deletePriority(deletePriorityDTO),HttpStatus.OK);

    }

    @PutMapping(value = "/updatePriority",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<PriorityResponseDTO>> updatePriority(@RequestBody @Validated UpdatePriorityDTO updatePriorityDTO){

        return new ResponseEntity<>(priorityServices.updatePriority(updatePriorityDTO),HttpStatus.OK);

    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<SetStakeholderObjectiveVerificationResponse>> verify(@RequestBody @Validated SetStakeholderObjectiveVerifyDTO setStakeholderObjectiveVerifyDTO){

        return new ResponseEntity<>(priorityServices.verify(setStakeholderObjectiveVerifyDTO),HttpStatus.OK);
    }

    @PostMapping(value = "/getPriority",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<List<GetPriorityResponse>>> getPriority(@RequestBody @Validated GetPriorityDTO getPriorityDTO){

        return new ResponseEntity<>(priorityServices.getPriority(getPriorityDTO),HttpStatus.OK);

    }

}
