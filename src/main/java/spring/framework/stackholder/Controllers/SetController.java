package spring.framework.stackholder.Controllers;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.framework.stackholder.RequestDTO.DeleteSetDTO;
import spring.framework.stackholder.RequestDTO.SetDTO;
import spring.framework.stackholder.RequestDTO.UpdateSetDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.SetResponseDTO;
import spring.framework.stackholder.Services.SetServices;

@RestController
@RequestMapping("/set")
public class SetController {

    private final SetServices setServices;

    public SetController(SetServices setServices) {
        this.setServices = setServices;
    }

    @PostMapping(path = "/addSet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<SetResponseDTO>> addSet(SetDTO setDTO){

        Response<SetResponseDTO> response= setServices.addSet(setDTO);
        if (response.getResponseBody()==null){
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping(path = "/deleteSet",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<SetResponseDTO>> deleteSet(DeleteSetDTO deleteSetDTO){

        Response<SetResponseDTO> response = setServices.deleteSet(deleteSetDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PutMapping(path = "/updateSet",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<SetResponseDTO>> updateSet(UpdateSetDTO updateSetDTO){


        Response<SetResponseDTO> response= setServices.updateSet(updateSetDTO);
        if (response.getResponseBody()==null){
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
