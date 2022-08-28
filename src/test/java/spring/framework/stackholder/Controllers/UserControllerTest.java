package spring.framework.stackholder.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.framework.stackholder.Repositories.UserRepository;
import spring.framework.stackholder.RequestDTO.*;
import spring.framework.stackholder.ResponseDTO.Response;
import spring.framework.stackholder.Services.UserService;
import spring.framework.stackholder.StackHolderApplication;
import spring.framework.stackholder.StackHolderConstants.Constants;
import spring.framework.stackholder.domain.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StackHolderApplication.class)
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {


    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;


    @Mock
    UserService userService;

    @Mock
    MockHttpServletRequest request;

    @InjectMocks
    UserController userController;

    public Long id;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userController = new UserController(userService);
        request = new MockHttpServletRequest();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/user/get")).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        List<User> users = new ObjectMapper().readValue(content, List.class);
        User user = new ObjectMapper().convertValue(users.get(users.size() - 1), User.class);
        id = user.getId();
        System.out.println(id);
    }

    @Test
    @Order(1)
    void signUp() throws Exception {
        SignUpDTO user = new SignUpDTO();
        user.setUsername("UnitTest");
        user.setEmail("unit@test.com");
        user.setPassword("UnitTest1@");
        user.setFirstName("Unit");
        user.setLastName("Test");

        request.setRequestURI("http://localhost:5000");
        String uri = "/user/signup";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(new ObjectMapper().writeValueAsString(user))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Response<User> response = new Response<>();
        response = new ObjectMapper().readValue(content, Response.class);
        System.out.println(response.getResponseBody());
        User user1 = new ObjectMapper().convertValue(response.getResponseBody(), User.class);


        if (response.getResponseBody() == null) {
            if (response.getResponseMessage().equals("User is already registered with this Username. Try a Different One")) {
                assertEquals(Constants.USERNAME_EXISTS, response.getResponseCode());
            } else {
                assertEquals(Constants.EMAIL_EXISTS, response.getResponseCode());
            }
        } else {
            assertEquals(1, response.getResponseCode());
            id = user1.getId();
        }
    }

    @Test
    @Order(2)
    void update() throws Exception {
        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setEmail("unit@test.com");
        updateDTO.setId(String.valueOf(id));
        updateDTO.setIsActive(true);
        updateDTO.setFirstName("Unit");
        updateDTO.setLastName("Test");
        updateDTO.setUsername("UnitTest");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/user/update")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(new ObjectMapper().writeValueAsString(updateDTO))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Response<Object> response = new Response<>();
        response = new ObjectMapper().readValue(content, Response.class);
        if (response.getResponseBody() == null) {
            if (response.getResponseMessage().equals("Username already Exists. Try different one")) {
                assertEquals(Constants.USERNAME_EXISTS, response.getResponseCode());
            } else {
                assertEquals(Constants.EMAIL_EXISTS, response.getResponseCode());
            }
        } else {
            assertEquals(1, response.getResponseCode());
        }

    }

    @Test
    @Order(3)
    void updatePassword() throws Exception {

        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setId(id);
        updatePasswordDTO.setCurrentPassword("UnitTest1@");
        updatePasswordDTO.setNewPassword("UnitTest1@");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/user/updatepassword")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(new ObjectMapper().writeValueAsString(updatePasswordDTO))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Response<Object> response = new Response<>();
        response = new ObjectMapper().readValue(content, Response.class);
        if (response.getResponseBody() == null) {
            if (response.getResponseMessage().equals("Current Password MisMatch")) {
                assertEquals(Constants.CURRENT_PASSWORD_MISMATCH, response.getResponseCode());
            }
        } else {
            assertEquals(1, response.getResponseCode());
        }
    }

    @Test
    @Order(4)
    void forgotPassword() throws Exception {

        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setEmail("unit@test.com");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/user/forgotpassword")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(new ObjectMapper().writeValueAsString(forgotPasswordDTO))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Response<Object> response = new Response<>();
        response = new ObjectMapper().readValue(content, Response.class);
        if (response.getResponseBody() == null) {
            if (response.getResponseMessage().equals("Your email does not match to our any of Records")) {
                assertEquals(Constants.EMAIL_NOT_FOUND, response.getResponseCode());
            }
        } else {
            assertEquals(1, response.getResponseCode());
        }

    }

    @Test
    @Order(5)
    void deleteUser() throws Exception {

        DeleteAccountDTO deleteAccountDTO = new DeleteAccountDTO();
        deleteAccountDTO.setId(id);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete("/user/deleteaccount")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(new ObjectMapper().writeValueAsString(deleteAccountDTO))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Response<Object> response = new Response<>();
        response = new ObjectMapper().readValue(content, Response.class);
        if (response.getResponseBody() == null) {
            if (response.getResponseMessage().equals("Account Deletion Failed. Please Contact Customer Support")) {
                assertEquals(Constants.ADMIN_ACCOUNT_DEL_FAILED, response.getResponseCode());
            }
        } else {
            assertEquals(1, response.getResponseCode());
        }
    }
}