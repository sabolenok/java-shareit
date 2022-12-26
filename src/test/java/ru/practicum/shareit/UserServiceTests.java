package ru.practicum.shareit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTests {

    private User user;
    @Mock
    UserRepository userRepository;
    UserService userService;

    @Test
    public void createNewUserSuccessful() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        userService = new UserService();
        userService.setRepository(userRepository);

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        Assertions.assertEquals(userService.create(user), user);
    }

    @Test
    public void updateUserSuccessful() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        User update = new User();
        update.setId(1);
        update.setName("update_test_user");
        update.setEmail("testUpd@test.ru");

        userService = new UserService();
        userService.setRepository(userRepository);

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(update);

        Assertions.assertEquals(userService.put(1, update), update);
    }

    @Test
    public void updateUserNameSuccessful() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        User update = new User();
        update.setId(1);
        update.setName("update_test_user");

        userService = new UserService();
        userService.setRepository(userRepository);

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(update);

        Assertions.assertEquals(userService.put(1, update).getEmail(), user.getEmail());
        Assertions.assertEquals(update.getName(), "update_test_user");
    }

    @Test
    public void updateUserEMailSuccessful() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        User update = new User();
        update.setId(1);
        update.setEmail("testUpd@test.ru");

        userService = new UserService();
        userService.setRepository(userRepository);

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

        userService = new UserService();
        userService.setRepository(userRepository);

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
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        userService = new UserService();
        userService.setRepository(userRepository);

        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(userService.findById(1), user);
    }

    @Test
    public void findByIdUserNotFoundThrowsException() {
        userService = new UserService();
        userService.setRepository(userRepository);

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
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        userService = new UserService();
        userService.setRepository(userRepository);

        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));

        Assertions.assertEquals(userService.findAll().size(), 1);
    }

    @Test
    public void deleteUser() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        userService = new UserService();
        userService.setRepository(userRepository);

        userService.deleteUser(1);
    }

    @Test
    public void gettersTest() {
        userService = new UserService();
        userService.setRepository(userRepository);

        Assertions.assertEquals(userService.getRepository(), userRepository);
    }

    @Test
    public void mapperTest() {
        user = new User();
        user.setId(1);
        user.setName("test_user");
        user.setEmail("test@test.ru");

        UserDto userDto = UserMapper.toUserDto(user);

        User userNew = UserMapper.toUser(userDto);

        Assertions.assertEquals(user.getId(), userDto.getId());
        Assertions.assertEquals(user.getName(), userDto.getName());
        Assertions.assertEquals(user.getEmail(), userDto.getEmail());

        Assertions.assertEquals(userNew.getId(), userDto.getId());
        Assertions.assertEquals(userNew.getName(), userDto.getName());
        Assertions.assertEquals(userNew.getEmail(), userDto.getEmail());
    }
}
