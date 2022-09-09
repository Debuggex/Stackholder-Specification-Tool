package spring.framework.stackholder.ServicesInterface;

import spring.framework.stackholder.RequestDTO.DeleteSetDTO;
import spring.framework.stackholder.RequestDTO.SetDTO;
import spring.framework.stackholder.RequestDTO.UpdateSetDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.SetResponseDTO;

public interface SetInterface {


    Response<SetResponseDTO> addSet(SetDTO setDTO);

    Response<SetResponseDTO> deleteSet(DeleteSetDTO deleteSetDTO);

    Response<SetResponseDTO> updateSet(UpdateSetDTO updateSetDTO);


}
