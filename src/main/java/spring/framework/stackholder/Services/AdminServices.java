package spring.framework.stackholder.Services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.framework.stackholder.Repositories.ObjectiveRepository;
import spring.framework.stackholder.Repositories.StakeholderRepository;
import spring.framework.stackholder.Repositories.UserRepository;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.*;
import spring.framework.stackholder.StackHolderConstants.Constants;
import spring.framework.stackholder.domain.Objective;
import spring.framework.stackholder.domain.Stakeholder;
import spring.framework.stackholder.domain.User;

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


    public AdminServices(UserRepository userRepository, PasswordEncoder passwordEncoder, StakeholderRepository stakeholderRepository, ObjectiveRepository objectiveRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        this.stakeholderRepository = stakeholderRepository;
        this.objectiveRepository = objectiveRepository;
    }

    @Transactional
    public Response<User> register(SignUpDTO signUpDTO) throws MessagingException, UnsupportedEncodingException {

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
            response.setResponseMessage("Password Updated Succesfully");
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

            Stakeholder stakeholder = new Stakeholder();
            stakeholder.setName(adminUpdateStakeholderObjectiveDTO.getName());
            stakeholder.setDescription(adminUpdateStakeholderObjectiveDTO.getDescription());

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
                objective1 -> objective1.getName().equals(adminUpdateStakeholderObjectiveDTO.getName()) && !Objects.equals(objective1.getId(),Long.valueOf(adminUpdateStakeholderObjectiveDTO.getId()))
        );
        if (isObjective) {
            response.setResponseCode(Constants.STAKEHOLDER_NAME_EXISTS);
            response.setResponseMessage("Objective with this name already Exists. Please Try Different One");
            return response;
        }

        Objective objective = new Objective();
        objective.setName(adminUpdateStakeholderObjectiveDTO.getName());
        objective.setDescription(adminUpdateStakeholderObjectiveDTO.getDescription());

        Objective saved=objectiveRepository.save(objective);

        stakeholderResponseDTO.setId(saved.getId());
        stakeholderResponseDTO.setName(saved.getName());
        stakeholderResponseDTO.setDescription(saved.getDescription());

        response.setResponseCode(1);
        response.setResponseMessage("Objective Added Successfully.");
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

}
