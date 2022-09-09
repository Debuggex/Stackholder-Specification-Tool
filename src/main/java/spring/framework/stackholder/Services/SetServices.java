package spring.framework.stackholder.Services;

import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.SetRespository;
import spring.framework.stackholder.Repositories.UserRepository;
import spring.framework.stackholder.RequestDTO.DeleteSetDTO;
import spring.framework.stackholder.RequestDTO.SetDTO;
import spring.framework.stackholder.RequestDTO.UpdateSetDTO;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.ResponseDTO.SetResponseDTO;
import spring.framework.stackholder.ServicesInterface.SetInterface;
import spring.framework.stackholder.StackHolderConstants.Constants;
import spring.framework.stackholder.domain.Set;
import spring.framework.stackholder.domain.User;

import java.util.Objects;

@Service
public class SetServices implements SetInterface {

    private final SetRespository setRespository;

    private final UserRepository userRepository;

    public SetServices(SetRespository setRespository, UserRepository userRepository) {
        this.setRespository = setRespository;
        this.userRepository = userRepository;
    }

    @Override
    public Response<SetResponseDTO> addSet(SetDTO setDTO) {

        /*
        Initialization
         */
        Response<SetResponseDTO> response=new Response<>();
        SetResponseDTO setResponseDTO=new SetResponseDTO();

        /*
        Finding User and checking if set with given name exists or not.
         */
        User user=userRepository.findById(Long.valueOf(setDTO.getUserId())).get();
        boolean isSetNameUnique=user.getSets().stream().anyMatch(
                set -> set.getName().equals(setDTO.getName())
        );
        if (isSetNameUnique) {
            response.setResponseBody(null);
            response.setResponseCode(Constants.SET_NAME_EXISTS);
            response.setResponseMessage("The Set name must be unique");
            return response;
        }

        /*
        Creating a new set and adding a foreign key relation with user.
         */
        Set set =new Set();
        set.setName(setDTO.getName());
        set.setDescription(setDTO.getDescription());
        Set savedSet=setRespository.save(set);
        user.addSet(savedSet);

        /*
        Creating response
         */
        setResponseDTO.setDescription(savedSet.getDescription());
        setResponseDTO.setName(savedSet.getName());
        setResponseDTO.setId(savedSet.getId());
        response.setResponseBody(setResponseDTO);
        response.setResponseCode(1);
        response.setResponseMessage("Set Added Successfully");

        /*
        returning Response
         */
        return response;
    }

    @Override
    public Response<SetResponseDTO> deleteSet(DeleteSetDTO deleteSetDTO) {

        /*
        Initialization
         */
        Response<SetResponseDTO> response=new Response<>();
        SetResponseDTO setResponseDTO=new SetResponseDTO();

        /*
        Deleting Set
         */
        Set set=setRespository.findById(Long.valueOf(deleteSetDTO.getSetId())).get();
        setResponseDTO.setName(set.getName());
        setResponseDTO.setDescription(set.getDescription());
        setResponseDTO.setId(set.getId());
        setRespository.deleteById(Long.valueOf(deleteSetDTO.getSetId()));

        /*
        Setting and returning response
         */
        response.setResponseBody(setResponseDTO);
        response.setResponseCode(1);
        response.setResponseMessage("Set has been added Successfully");
        return response;
    }

    @Override
    public Response<SetResponseDTO> updateSet(UpdateSetDTO updateSetDTO) {

        /*
        Initialization
         */
        Response<SetResponseDTO> response=new Response<>();
        SetResponseDTO setResponseDTO=new SetResponseDTO();

        /*
        Checking if any other set with that name exists
         */
        User user=userRepository.findById(Long.valueOf(updateSetDTO.getUserId())).get();
        boolean isSetNameUnique=user.getSets().stream().anyMatch(
                set -> set.getName().equals(updateSetDTO.getName()) && !Objects.equals(set.getId(), Long.valueOf(updateSetDTO.getId()))
        );
        if (isSetNameUnique) {
            response.setResponseBody(null);
            response.setResponseCode(Constants.SET_NAME_EXISTS);
            response.setResponseMessage("The Set name must be unique");
            return response;
        }

        /*
        Updating the set
         */
        Set set=setRespository.findById(Long.valueOf(updateSetDTO.getId())).get();
        set.setName(updateSetDTO.getName());
        set.setDescription(updateSetDTO.getDescription());
        setRespository.save(set);
        set=setRespository.findById(Long.valueOf(updateSetDTO.getId())).get();

        /*
        Creating the Response
         */
        setResponseDTO.setId(set.getId());
        setResponseDTO.setDescription(set.getDescription());
        setResponseDTO.setName(set.getName());
        response.setResponseBody(setResponseDTO);
        response.setResponseCode(1);
        response.setResponseMessage("Set has been added Successfully");

        /*
        returning response
         */

        return response;
    }
}
