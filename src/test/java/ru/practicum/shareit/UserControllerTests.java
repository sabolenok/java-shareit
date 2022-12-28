package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

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
    UserService userServiceMock;

    private User user;

    private UserDto userDto;

    @Mock
    UserRepository userRepository;
    UserService userService;

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

        userService = new UserService();
        userService.setRepository(userRepository);
    }

    //++ Тесты контроллера
    @Test
    void createNewUserTest() throws Exception {
        when(userServiceMock.create(any()))
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
        when(userServiceMock.findAll())
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
        when(userServiceMock.findById(anyInt()))
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
        when(userServiceMock.put(anyInt(), any()))
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
    //-- Тесты контроллера

    //++ Unit-Тесты сервиса
    @Test
    public void createNewUserSuccessful() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        Assertions.assertEquals(userService.create(user), user);
    }

    @Test
    public void updateUserSuccessful() {
        User update = new User();
        update.setId(1);
        update.setName("update_test_user");
        update.setEmail("testUpd@test.ru");

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(update);

        Assertions.assertEquals(userService.put(1, update), update);
    }

    @Test
    public void updateUserNameEmailIsNullSuccessful() {
        User update = new User();
        update.setId(1);
        update.setName("update_test_user");

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(update);

        Assertions.assertEquals(userService.put(1, update).getEmail(), user.getEmail());
        Assertions.assertEquals(update.getName(), "update_test_user");
    }

    @Test
    public void updateUserNameEmailIsBlankSuccessful() {
        User update = new User();
        update.setId(1);
        update.setName("update_test_user");
        update.setEmail("");

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(update);

        Assertions.assertEquals(userService.put(1, update).getEmail(), user.getEmail());
        Assertions.assertEquals(update.getName(), "update_test_user");
    }

    @Test
    public void updateUserEMailNameIsNullSuccessful() {
        User update = new User();
        update.setId(1);
        update.setEmail("testUpd@test.ru");

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(update);

        Assertions.assertEquals(userService.put(1, update).getName(), user.getName());
        Assertions.assertEquals(update.getEmail(), "testUpd@test.ru");
    }

    @Test
    public void updateUserEMailNameIsBlankSuccessful() {
        User update = new User();
        update.setId(1);
        update.setEmail("testUpd@test.ru");
        update.setName("");

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(update);

        Assertions.assertEquals(userService.put(1, update).getName(), user.getName());
        Assertions.assertEquals(update.getEmail(), "testUpd@test.ru");
    }

    @Test
    public void updateUserNotFoundThrowsException() {
        User update = new User();
        update.setId(1);
        update.setName("update_test_user");
        update.setEmail("testUpd@test.ru");

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            userService.put(1, update);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден", e.getMessage());
        }
    }

    @Test
    public void findByIdUserSuccessful() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(userService.findById(1), user);
    }

    @Test
    public void findByIdUserNotFoundThrowsException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        try {
            userService.findById(1);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь не найден", e.getMessage());
        }
    }

    @Test
    public void findAllUserSuccessful() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));

        Assertions.assertEquals(userService.findAll().size(), 1);
    }

    @Test
    public void deleteUser() {
        userService.deleteUser(1);
    }

    @Test
    public void gettersTest() {
        Assertions.assertEquals(userService.getRepository(), userRepository);
    }

    @Test
    public void mapperTest() {
        userDto = UserMapper.toUserDto(user);

        User userNew = UserMapper.toUser(userDto);

        Assertions.assertEquals(user.getId(), userDto.getId());
        Assertions.assertEquals(user.getName(), userDto.getName());
        Assertions.assertEquals(user.getEmail(), userDto.getEmail());

        Assertions.assertEquals(userNew.getId(), userDto.getId());
        Assertions.assertEquals(userNew.getName(), userDto.getName());
        Assertions.assertEquals(userNew.getEmail(), userDto.getEmail());
    }
    //-- Unit-Тесты сервиса
}
