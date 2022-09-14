package spring.framework.stackholder.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.DeleteSetDTO;
import spring.framework.stackholder.RequestDTO.GetSetsDTO;
import spring.framework.stackholder.RequestDTO.SetDTO;
import spring.framework.stackholder.RequestDTO.UpdateSetDTO;
import spring.framework.stackholder.ResponseDTO.GetSetsResponseDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.SetResponseDTO;
import spring.framework.stackholder.Services.SetServices;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/set")
public class SetController {

    private final SetServices setServices;

    public SetController(SetServices setServices) {
        this.setServices = setServices;
    }

    @PostMapping(value = "/addSet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<SetResponseDTO>> addSet(@RequestBody @Validated SetDTO setDTO){

        Response<SetResponseDTO> response= setServices.addSet(setDTO);
        if (response.getResponseBody()==null){
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping(value = "/deleteSet",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<SetResponseDTO>> deleteSet(@RequestBody @Validated DeleteSetDTO deleteSetDTO){

        Response<SetResponseDTO> response = setServices.deleteSet(deleteSetDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PutMapping(value = "/updateSet",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<SetResponseDTO>> updateSet(@RequestBody @Validated UpdateSetDTO updateSetDTO){


        Response<SetResponseDTO> response= setServices.updateSet(updateSetDTO);
        if (response.getResponseBody()==null){
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/getSets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<GetSetsResponseDTO>> getSets(@RequestBody @Validated GetSetsDTO getSetsDTO){

        return new ResponseEntity<>(setServices.getSets(getSetsDTO),HttpStatus.OK);

    }
}
