package spring.framework.stackholder.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.DeleteStakeholderDTO;
import spring.framework.stackholder.RequestDTO.StakeholderDTO;
import spring.framework.stackholder.RequestDTO.UpdateStakeholderDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.StakeholderResponseDTO;
import spring.framework.stackholder.Services.StakeholderService;

@RequestMapping("/stakeholder")
@RestController
public class StakeholderController {

    private final StakeholderService stakeholderService;

    public StakeholderController(StakeholderService stakeholderService) {
        this.stakeholderService = stakeholderService;
    }

    @PostMapping(path = "/addStakeholder", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<StakeholderResponseDTO>> addStakeholder(StakeholderDTO stakeholderDTO){

        Response<StakeholderResponseDTO> response= stakeholderService.addStakeholder(stakeholderDTO);
        if (response.getResponseBody()==null){
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PutMapping(path = "/updateStakeholder", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<StakeholderResponseDTO>> updateStakeholder(UpdateStakeholderDTO updateStakeholderDTO){

        Response<StakeholderResponseDTO> response= stakeholderService.updateStakeholder(updateStakeholderDTO);
        if (response.getResponseBody()==null){
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping(path = "/deleteStakeholder", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<StakeholderResponseDTO>> deleteStakeholder(DeleteStakeholderDTO deleteStakeholderDTO){

        Response<StakeholderResponseDTO> response= stakeholderService.deleteStakeholder(deleteStakeholderDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

}
