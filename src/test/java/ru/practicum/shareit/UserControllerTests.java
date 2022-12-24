package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("test_user");
        userDto.setEmail("test@test.ru");

        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");
    }

    @Test
    void createNewUserTest() throws Exception {
        when(userService.create(any()))
                .thenReturn(user);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['email']").value(userDto.getEmail()));
    }

    @Test
    void getAllUserTest() throws Exception {
        when(userService.findAll())
                .thenReturn(List.of(user));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    void getOneUserTest() throws Exception {
        when(userService.findById(anyInt()))
                .thenReturn(user);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['email']").value(userDto.getEmail()));
    }

    @Test
    void updateUserTest() throws Exception {
        user.setEmail("new@test.ru");
        user.setName("test_user_new");
        when(userService.put(anyInt(), any()))
                .thenReturn(user);
        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value("test_user_new"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['email']").value("new@test.ru"));
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
