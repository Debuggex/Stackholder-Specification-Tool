package spring.framework.stackholder.Services;

import lombok.Synchronized;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.*;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.*;
import spring.framework.stackholder.StackHolderConstants.Constants;
import spring.framework.stackholder.domain.*;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AdminServices {

    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    private final StakeholderRepository stakeholderRepository;

    private final ObjectiveRepository objectiveRepository;

    private final SetRespository setRespository;

    private final SetStakeholderRepository setStakeholderRepository;

    private final SetObjectiveRespository setObjectiveRespository;

    private final SetStakeholderObjectiveRespository setStakeholderObjectiveRespository;


    public AdminServices(UserRepository userRepository, PasswordEncoder passwordEncoder, StakeholderRepository stakeholderRepository, ObjectiveRepository objectiveRepository, SetRespository setRespository, SetStakeholderRepository setStakeholderRepository, SetObjectiveRespository setObjectiveRespository, SetStakeholderObjectiveRespository setStakeholderObjectiveRespository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.stakeholderRepository = stakeholderRepository;
        this.objectiveRepository = objectiveRepository;
        this.setRespository = setRespository;
        this.setStakeholderRepository = setStakeholderRepository;
        this.setObjectiveRespository = setObjectiveRespository;
        this.setStakeholderObjectiveRespository = setStakeholderObjectiveRespository;
    }

    @Transactional
    public Response<User> register(SignUpDTO signUpDTO) {

        Response<User> response = new Response<>();

        AtomicReference<Boolean> isUserNameExists = new AtomicReference<>(false);
        AtomicReference<Boolean> isUserExists = new AtomicReference<>(false);

        userRepository.findAll().forEach(
                user -> {
                    if (user.getUsername().equals(signUpDTO.getUsername())) {
                        isUserNameExists.set(true);
                    }
                    if (user.getEmail().equals(signUpDTO.getEmail())) {
                        isUserExists.set(true);
                    }

                }
        );

        if (isUserNameExists.get()) {
            response.setResponseCode(Constants.USERNAME_EXISTS);
            response.setResponseMessage("User is already registered with this Username. Try a Different One");
            response.setResponseBody(null);
            return response;
        } else if (isUserExists.get()) {
            response.setResponseCode(Constants.EMAIL_EXISTS);
            response.setResponseMessage("User is already registered with this Email. Try a Different One");
            response.setResponseBody(null);
            return response;
        }

        User user = new User();
        user.setEmail(signUpDTO.getEmail());
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setUsername(signUpDTO.getUsername());
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        response.setResponseCode(1);
        response.setResponseMessage("User registered Successfully");
        response.setResponseBody(savedUser);
        return response;
    }


    public boolean checkUser(String username) {

        return userRepository.findAll().stream().anyMatch(
                user -> user.getUsername().equals(username)
        );
    }

    public boolean checkEmail(String email) {

        return userRepository.findAll().stream().anyMatch(
                user -> user.getEmail().equals(email)
        );
    }

    public List<User> get() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(
                user ->
                {
                    if (!user.getIsAdmin()) {
                        users.add(user);
                    }
                }
        );

        return users;
    }

    public Response<SignUpDTO> deleteUser(DeleteAccountDTO deleteAccountDTO) {

        Response<SignUpDTO> response = new Response<>();
        User user = userRepository.findById(deleteAccountDTO.getId()).get();
        if (user.getIsAdmin()) {
            response.setResponseCode(Constants.ADMIN_ACCOUNT_DEL_FAILED);
            response.setResponseMessage("Account Deletion Failed. Please Contact Customer Support");
            response.setResponseBody(null);
            return response;
        }
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setEmail(user.getEmail());
        signUpDTO.setFirstName(user.getFirstName());
        signUpDTO.setLastName(user.getLastName());
        signUpDTO.setUsername(user.getUsername());

        userRepository.deleteById(deleteAccountDTO.getId());
        response.setResponseCode(1);
        response.setResponseMessage("Account Delete Successfully");
        response.setResponseBody(signUpDTO);
        return response;
    }


    public Response<UpdatePasswordResponse> updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        User user = userRepository.findById(updatePasswordDTO.getId()).get();
        boolean isSame = BCrypt.checkpw(updatePasswordDTO.getCurrentPassword(), user.getPassword());
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse();
        Response<UpdatePasswordResponse> response = new Response<>();
        if (isSame) {
            user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
            userRepository.save(user);
            updatePasswordResponse.setNewPassword(user.getPassword());
            response.setResponseCode(1);
            response.setResponseMessage("Password Updated Successfully");
            response.setResponseBody(updatePasswordResponse);
            return response;
        }
        response.setResponseMessage("Current Password MisMatch");
        response.setResponseBody(null);
        response.setResponseCode(Constants.CURRENT_PASSWORD_MISMATCH);
        return response;
    }

    public Response<User> updateUser(UpdateDTO updateDTO) {
        Optional<User> isUserExists = userRepository.findById(Long.valueOf(updateDTO.getId()));
        Response<User> response = new Response<>();
        if (isUserExists.isPresent()) {

            if (userRepository.findAll().stream().anyMatch(
                    user -> {
                        if (user.getId().compareTo(Long.valueOf(updateDTO.getId())) != 0) {
                            if (user.getUsername().equals(updateDTO.getUsername())) {
                                response.setResponseCode(Constants.EMAIL_EXISTS);
                                response.setResponseMessage("Username already Exists. Try different one");
                                response.setResponseBody(null);
                                return true;
                            } else if (user.getEmail().equals(updateDTO.getEmail())) {
                                response.setResponseCode(Constants.EMAIL_EXISTS);
                                response.setResponseMessage("Email already registered. Try a different one.");
                                response.setResponseBody(null);
                                return true;
                            }

                        }
                        return false;
                    }
            )) {
                return response;
            }


            User user = isUserExists.get();
            user.setUsername(updateDTO.getUsername());
            user.setFirstName(updateDTO.getFirstName());
            user.setLastName(updateDTO.getLastName());
            user.setEmail(updateDTO.getEmail());
            user.setIsActive(updateDTO.getIsActive());
            if (updateDTO.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
            }
            userRepository.save(user);
            response.setResponseBody(user);
        }
        response.setResponseCode(1);
        response.setResponseMessage("User Details Updated Successfully");
        return response;
    }


    public GetObjectivesStakeholdersResponseDTO getObjectivesStakeholders(){

        GetObjectivesStakeholdersResponseDTO getObjectivesStakeholdersResponseDTO=new GetObjectivesStakeholdersResponseDTO();
        GetObjectivesResponseDTO getObjectivesResponseDTO=new GetObjectivesResponseDTO();
        GetStakeholderResponseDTO getStakeholderResponseDTO=new GetStakeholderResponseDTO();


        objectiveRepository.findAll().forEach(
                objective -> {
                    ObjectiveResponseDTO objectiveResponseDTO=new ObjectiveResponseDTO();
                    objectiveResponseDTO.setId(objective.getId());
                    objectiveResponseDTO.setName(objective.getName());
                    objectiveResponseDTO.setDescription(objective.getDescription());
                    getObjectivesResponseDTO.getObjectiveResponseDTOS().add(objectiveResponseDTO);
                }
        );

        stakeholderRepository.findAll().forEach(
                stakeholder -> {
                    StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();
                    stakeholderResponseDTO.setId(stakeholder.getId());
                    stakeholderResponseDTO.setName(stakeholder.getName());
                    stakeholderResponseDTO.setDescription(stakeholder.getDescription());
                    getStakeholderResponseDTO.getStakeholderResponseDTOS().add(stakeholderResponseDTO);
                }
        );

        getObjectivesStakeholdersResponseDTO.setGetObjectivesResponseDTO(getObjectivesResponseDTO);
        getObjectivesStakeholdersResponseDTO.setGetStakeholderResponseDTO(getStakeholderResponseDTO);

        return getObjectivesStakeholdersResponseDTO;

    }

    public GetObjectivesStakeholdersResponseDTO getSetObjectivesStakeholders(){

        GetObjectivesStakeholdersResponseDTO getObjectivesStakeholdersResponseDTO=new GetObjectivesStakeholdersResponseDTO();
        GetObjectivesResponseDTO getObjectivesResponseDTO=new GetObjectivesResponseDTO();
        GetStakeholderResponseDTO getStakeholderResponseDTO=new GetStakeholderResponseDTO();


        setObjectiveRespository.findAll().forEach(
                objective -> {
                    ObjectiveResponseDTO objectiveResponseDTO=new ObjectiveResponseDTO();
                    objectiveResponseDTO.setId(objective.getId());
                    objectiveResponseDTO.setName(objective.getName());
                    objectiveResponseDTO.setDescription(objective.getDescription());
                    objectiveResponseDTO.setSetName(objective.getSetId().getName());
                    objectiveResponseDTO.setSetId(String.valueOf(objective.getSetId().getId()));
                    getObjectivesResponseDTO.getObjectiveResponseDTOS().add(objectiveResponseDTO);
                }
        );

        setStakeholderRepository.findAll().forEach(
                stakeholder -> {
                    StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();
                    stakeholderResponseDTO.setId(stakeholder.getId());
                    stakeholderResponseDTO.setName(stakeholder.getName());
                    stakeholderResponseDTO.setDescription(stakeholder.getDescription());
                    stakeholderResponseDTO.setSetName(stakeholder.getSetId().getName());
                    stakeholderResponseDTO.setSetId(String.valueOf(stakeholder.getSetId().getId()));
                    getStakeholderResponseDTO.getStakeholderResponseDTOS().add(stakeholderResponseDTO);
                }
        );

        getObjectivesStakeholdersResponseDTO.setGetObjectivesResponseDTO(getObjectivesResponseDTO);
        getObjectivesStakeholdersResponseDTO.setGetStakeholderResponseDTO(getStakeholderResponseDTO);

        return getObjectivesStakeholdersResponseDTO;

    }



    public Response<StakeholderResponseDTO> updateStakeholderObjective(AdminUpdateStakeholderObjectiveDTO adminUpdateStakeholderObjectiveDTO){

        Response<StakeholderResponseDTO> response = new Response<>();
        StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();

        if (adminUpdateStakeholderObjectiveDTO.getUpdateType().equals("Stakeholder")) {

            boolean isStakeholder = stakeholderRepository.findAll().stream().anyMatch(
                    stakeholder1 -> stakeholder1.getName().equals(adminUpdateStakeholderObjectiveDTO.getName()) && !Objects.equals(stakeholder1.getId(), Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId()))

            );
            if (isStakeholder) {
                response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
                response.setResponseMessage("Stakeholder with this name already Exists. Please Try Different One");
                return response;
            }

            Stakeholder stakeholder = stakeholderRepository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId())).get();
            stakeholder.setName(adminUpdateStakeholderObjectiveDTO.getName());
            stakeholder.setDescription(adminUpdateStakeholderObjectiveDTO.getDescription());
            stakeholder = stakeholderRepository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId())).get();

            stakeholderRepository.save(stakeholder);

            stakeholderResponseDTO.setId(stakeholder.getId());
            stakeholderResponseDTO.setName(stakeholder.getName());
            stakeholderResponseDTO.setDescription(stakeholder.getDescription());

            response.setResponseCode(1);
            response.setResponseMessage("Stakeholder Updated Successfully.");
            response.setResponseBody(stakeholderResponseDTO);

            return response;

        }

        boolean isObjective = objectiveRepository.findAll().stream().anyMatch(
                objective1 -> objective1.getName().equals(adminUpdateStakeholderObjectiveDTO.getName()) && !Objects.equals(objective1.getId(),Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId()))
        );
        if (isObjective) {
            response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
            response.setResponseMessage("Objective with this name already Exists. Please Try Different One");
            return response;
        }

        Objective objective = objectiveRepository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId())).get();
        objective.setName(adminUpdateStakeholderObjectiveDTO.getName());
        objective.setDescription(adminUpdateStakeholderObjectiveDTO.getDescription());

        objectiveRepository.save(objective);
        objective = objectiveRepository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId())).get();

        stakeholderResponseDTO.setId(objective.getId());
        stakeholderResponseDTO.setName(objective.getName());
        stakeholderResponseDTO.setDescription(objective.getDescription());

        response.setResponseCode(1);
        response.setResponseMessage("Objective Updated Successfully.");
        response.setResponseBody(stakeholderResponseDTO);

        return response;

    }

    public Response<StakeholderResponseDTO> updateSetStakeholderObjective(AdminUpdateStakeholderObjectiveDTO adminUpdateStakeholderObjectiveDTO){

        Response<StakeholderResponseDTO> response = new Response<>();
        StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();

        if (adminUpdateStakeholderObjectiveDTO.getUpdateType().equals("Stakeholder")) {

            boolean isStakeholder = setRespository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getSetId())).get().getSetStakeholders().stream().anyMatch(
                    stakeholder1 -> stakeholder1.getName().equals(adminUpdateStakeholderObjectiveDTO.getName()) && !Objects.equals(stakeholder1.getId(), Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId()))

            );
            if (isStakeholder) {
                response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
                response.setResponseMessage("Stakeholder with this name already Exists. Please Try Different One");
                return response;
            }

            SetStakeholder stakeholder = setStakeholderRepository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId())).get();
            stakeholder.setName(adminUpdateStakeholderObjectiveDTO.getName());
            stakeholder.setDescription(adminUpdateStakeholderObjectiveDTO.getDescription());
            stakeholder = setStakeholderRepository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId())).get();

            setStakeholderRepository.save(stakeholder);

            stakeholderResponseDTO.setId(stakeholder.getId());
            stakeholderResponseDTO.setName(stakeholder.getName());
            stakeholderResponseDTO.setDescription(stakeholder.getDescription());

            response.setResponseCode(1);
            response.setResponseMessage("Stakeholder Updated Successfully.");
            response.setResponseBody(stakeholderResponseDTO);

            return response;

        }

        boolean isObjective = setRespository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getSetId())).get().getSetObjectives().stream().anyMatch(
                objective1 -> objective1.getName().equals(adminUpdateStakeholderObjectiveDTO.getName()) && !Objects.equals(objective1.getId(),Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId()))
        );
        if (isObjective) {
            response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
            response.setResponseMessage("Objective with this name already Exists. Please Try Different One");
            return response;
        }

        SetObjective objective = setObjectiveRespository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId())).get();
        objective.setName(adminUpdateStakeholderObjectiveDTO.getName());
        objective.setDescription(adminUpdateStakeholderObjectiveDTO.getDescription());

        setObjectiveRespository.save(objective);
        objective = setObjectiveRespository.findById(Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId())).get();

        stakeholderResponseDTO.setId(objective.getId());
        stakeholderResponseDTO.setName(objective.getName());
        stakeholderResponseDTO.setDescription(objective.getDescription());

        response.setResponseCode(1);
        response.setResponseMessage("Objective Updated Successfully.");
        response.setResponseBody(stakeholderResponseDTO);

        return response;

    }


    public Response<StakeholderResponseDTO> addStakeholderObjective(AdminAddStakeholderObjectiveDTO adminAddStakeholderObjectiveDTO){

        Response<StakeholderResponseDTO> response = new Response<>();
        StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();

        if (adminAddStakeholderObjectiveDTO.getAddType().equals("Stakeholder")) {

            boolean isStakeholder = stakeholderRepository.findAll().stream().anyMatch(
                    stakeholder -> stakeholder.getName().equals(adminAddStakeholderObjectiveDTO.getName())
            );
            if (isStakeholder) {
                response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
                response.setResponseMessage("Stakeholder with this name already Exists. Please Try Different One");
                return response;
            }

            Stakeholder stakeholder = new Stakeholder();
            stakeholder.setName(adminAddStakeholderObjectiveDTO.getName());
            stakeholder.setDescription(adminAddStakeholderObjectiveDTO.getDescription());

            Stakeholder saved=stakeholderRepository.save(stakeholder);

            stakeholderResponseDTO.setId(saved.getId());
            stakeholderResponseDTO.setName(saved.getName());
            stakeholderResponseDTO.setDescription(saved.getDescription());

            response.setResponseCode(1);
            response.setResponseMessage("Stakeholder Added Successfully.");
            response.setResponseBody(stakeholderResponseDTO);

            return response;
        }

        boolean isObjective = objectiveRepository.findAll().stream().anyMatch(
                objective -> objective.getName().equals(adminAddStakeholderObjectiveDTO.getName())
        );
        if (isObjective) {
            response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
            response.setResponseMessage("Objective with this name already Exists. Please Try Different One");
            return response;
        }

        Objective objective = new Objective();
        objective.setName(adminAddStakeholderObjectiveDTO.getName());
        objective.setDescription(adminAddStakeholderObjectiveDTO.getDescription());

        Objective saved=objectiveRepository.save(objective);

        stakeholderResponseDTO.setId(saved.getId());
        stakeholderResponseDTO.setName(saved.getName());
        stakeholderResponseDTO.setDescription(saved.getDescription());

        response.setResponseCode(1);
        response.setResponseMessage("Objective Added Successfully.");
        response.setResponseBody(stakeholderResponseDTO);

        return response;

    }

    public Response<StakeholderResponseDTO> addSetStakeholderObjective(AdminAddSetStakeholderObjectiveDTO adminAddSetStakeholderObjectiveDTO){

        Response<StakeholderResponseDTO> response = new Response<>();
        StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();
        Set set = setRespository.findById(Long.valueOf(adminAddSetStakeholderObjectiveDTO.getSetId())).get();

        if (adminAddSetStakeholderObjectiveDTO.getAddType().equals("Stakeholder")) {


            boolean isStakeholder = setStakeholderRepository.findAll().stream().anyMatch(
                    stakeholder -> stakeholder.getName().equals(adminAddSetStakeholderObjectiveDTO.getName())
            );
            if (isStakeholder) {
                response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
                response.setResponseMessage("Stakeholder with this name already Exists. Please Try Different One");
                return response;
            }

            SetStakeholder setStakeholder = new SetStakeholder();

            setStakeholder.setName(adminAddSetStakeholderObjectiveDTO.getName());
            setStakeholder.setDescription(adminAddSetStakeholderObjectiveDTO.getDescription());
            setStakeholder.setSetId(set);
            SetStakeholder savedStakeholder=setStakeholderRepository.save(setStakeholder);
            set.addStakeholder(savedStakeholder);

            stakeholderResponseDTO.setId(savedStakeholder.getId());
            stakeholderResponseDTO.setName(savedStakeholder.getName());
            stakeholderResponseDTO.setDescription(savedStakeholder.getDescription());

            response.setResponseCode(1);
            response.setResponseMessage("Stakeholder Added Successfully.");
            response.setResponseBody(stakeholderResponseDTO);

            return response;
        }

        boolean isObjective = setObjectiveRespository.findAll().stream().anyMatch(
                objective -> objective.getName().equals(adminAddSetStakeholderObjectiveDTO.getName())
        );
        if (isObjective) {
            response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
            response.setResponseMessage("Objective with this name already Exists. Please Try Different One");
            return response;
        }

        SetObjective objective = new SetObjective();
        objective.setName(adminAddSetStakeholderObjectiveDTO.getName());
        objective.setDescription(adminAddSetStakeholderObjectiveDTO.getDescription());
        objective.setSetId(set);
        SetObjective saved=setObjectiveRespository.save(objective);

        set.addObjective(saved);
        stakeholderResponseDTO.setId(saved.getId());
        stakeholderResponseDTO.setName(saved.getName());
        stakeholderResponseDTO.setDescription(saved.getDescription());

        response.setResponseCode(1);
        response.setResponseMessage("Objective Added Successfully.");
        response.setResponseBody(stakeholderResponseDTO);

        return response;

    }




    public Response<StakeholderResponseDTO> deleteStakeholderObjective(AdminDeleteStakeholderObjectiveDTO adminDeleteStakeholderObjectiveDTO){

        Response<StakeholderResponseDTO> response = new Response<>();
        StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();

        if (adminDeleteStakeholderObjectiveDTO.getDeleteType().equals("Stakeholder")) {
            Stakeholder stakeholder = stakeholderRepository.findById(Long.valueOf(adminDeleteStakeholderObjectiveDTO.getId())).get();
            stakeholderRepository.deleteById(Long.valueOf(adminDeleteStakeholderObjectiveDTO.getId()));
            stakeholderResponseDTO.setId(stakeholder.getId());
            stakeholderResponseDTO.setName(stakeholder.getName());
            stakeholderResponseDTO.setDescription(stakeholder.getDescription());

            response.setResponseCode(1);
            response.setResponseMessage("Stakeholder Deleted Successfully");
            response.setResponseBody(stakeholderResponseDTO);
            return response;
        }

        Objective objective = objectiveRepository.findById(Long.valueOf(adminDeleteStakeholderObjectiveDTO.getId())).get();
        objectiveRepository.deleteById(Long.valueOf(adminDeleteStakeholderObjectiveDTO.getId()));
        stakeholderResponseDTO.setId(objective.getId());
        stakeholderResponseDTO.setName(objective.getName());
        stakeholderResponseDTO.setDescription(objective.getDescription());

        response.setResponseCode(1);
        response.setResponseMessage("Objective Deleted Successfully");
        response.setResponseBody(stakeholderResponseDTO);
        return response;
    }

    public Response<StakeholderResponseDTO> deleteSetStakeholderObjective(AdminDeleteStakeholderObjectiveDTO adminDeleteStakeholderObjectiveDTO){

        Response<StakeholderResponseDTO> response = new Response<>();
        StakeholderResponseDTO stakeholderResponseDTO=new StakeholderResponseDTO();

        if (adminDeleteStakeholderObjectiveDTO.getDeleteType().equals("Stakeholder")) {
            SetStakeholder stakeholder = setStakeholderRepository.findById(Long.valueOf(adminDeleteStakeholderObjectiveDTO.getId())).get();
            setStakeholderRepository.deleteById(Long.valueOf(adminDeleteStakeholderObjectiveDTO.getId()));
            stakeholderResponseDTO.setId(stakeholder.getId());
            stakeholderResponseDTO.setName(stakeholder.getName());
            stakeholderResponseDTO.setDescription(stakeholder.getDescription());

            response.setResponseCode(1);
            response.setResponseMessage("Stakeholder Deleted Successfully");
            response.setResponseBody(stakeholderResponseDTO);
            return response;
        }

        SetObjective objective = setObjectiveRespository.findById(Long.valueOf(adminDeleteStakeholderObjectiveDTO.getId())).get();
        setObjectiveRespository.deleteById(Long.valueOf(adminDeleteStakeholderObjectiveDTO.getId()));
        stakeholderResponseDTO.setId(objective.getId());
        stakeholderResponseDTO.setName(objective.getName());
        stakeholderResponseDTO.setDescription(objective.getDescription());

        response.setResponseCode(1);
        response.setResponseMessage("Objective Deleted Successfully");
        response.setResponseBody(stakeholderResponseDTO);
        return response;
    }





    public Response<GetSetsResponseDTO> getSets(){

        GetSetsResponseDTO getSetsResponseDTO=new GetSetsResponseDTO();
        Response<GetSetsResponseDTO> response=new Response<>();

        setRespository.findAll().forEach(
                set -> {
                    SetResponseDTO setResponseDTO=new SetResponseDTO();
                    setResponseDTO.setId(set.getId());
                    setResponseDTO.setName(set.getName());
                    setResponseDTO.setDescription(set.getDescription());
                    setResponseDTO.setUserId(String.valueOf(set.getUserId().getId()));
                    setResponseDTO.setUserName(set.getUserId().getUsername());
                    getSetsResponseDTO.getSetResponseDTOS().add(setResponseDTO);
                }
        );

        response.setResponseCode(1);
        response.setResponseBody(getSetsResponseDTO);
        response.setResponseMessage("Sets Fetched Successfully");

        return response;

    }

    @Transactional
    @Synchronized
    public Response<List<GetPriorityResponse>> getPriority() {

        /**
         * @Initialization
         */

        Response<List<GetPriorityResponse>> response=new Response<>();
        List<GetPriorityResponse> getPriorityResponseList=new ArrayList<>();

        /**
         * @Getting Priority
         */

        List<SetStakeholderObjective> setStakeholderObjective = new ArrayList<>(setStakeholderObjectiveRespository.findAll());

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
