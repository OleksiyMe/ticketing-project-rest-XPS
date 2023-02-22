package com.cydeo.unit_test_review;

import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.KeycloakService;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskService taskService;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private PasswordEncoder passwordEncoder;

//    @Spy  We do not have Beans here in test. No ApplicationContext
//    private final UserMapper userMapper;

    @Spy
    private UserMapper userMapper1 = new UserMapper(new ModelMapper());

    @InjectMocks
    private UserServiceImpl userService;

    User user;
    UserDTO userDTO;


    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Joht");
        user.setLastName("Doe");
        user.setUserName("user");
        user.setPassWord("Abc1");
        user.setEnabled(true);
        user.setRole(new Role("Manager"));

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("Joht");
        userDTO.setLastName("Doe");
        userDTO.setUserName("user");
        userDTO.setPassWord("Abc1");
        userDTO.setEnabled(true);
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setDescription("Manager");
        userDTO.setRole(roleDTO);
    }

    private List<User> getUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Oleks");
        user2.setLastName("First");
        user2.setUserName("user2");

        return Arrays.asList(user, user2);
    }

    private List<UserDTO> getUsersDTOs() {
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setFirstName("Oleks");
        userDTO2.setLastName("First");
        userDTO2.setUserName("user2");

        return Arrays.asList(userDTO, userDTO2);
    }

    @Test
    void shouldListAllUsers() {
        //define our stubs
        when(userRepository.findAllByIsDeletedOrderByFirstNameDesc(false)).thenReturn(getUsers());

        List<UserDTO> expectedList = getUsersDTOs();
        //   expectedList.sort(Comparator.comparing(UserDTO::getFirstName).reversed());

        List<UserDTO> actualList = userService.listAllUsers();
        //   actualList.sort(Comparator.comparing(UserDTO::getFirstName).reversed());

        // Assertions.assertEquals(expectedList,actualList);

//AssertJ
//        assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);
        assertThat(actualList).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedList);
//        assertThat(actualList).usingRecursiveComparison()
//                .ignoringExpectedNullFields(id)
//                .isEqualTo(expectedList);
        // verify()  do not needed here
    }


    @Test
    void shouldFindUserByUsername() {

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean()))
                .thenReturn(user);

        UserDTO actual = userService.findByUserName("user");

        //  Assertions.assertEquals(userDTO,actual);
        assertThat(actual).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(user);

    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {

        //   when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(null);

        // from AssertJ
        Throwable throwable = catchThrowable(() -> userService.findByUserName("SomeUserName"));
        Assertions.assertInstanceOf(NoSuchElementException.class, throwable);
        Assertions.assertEquals("User not found", throwable.getMessage());
        //These 3 steps we can do with AssertJ

        assertThrowsExactly(
                NoSuchElementException.class,
                () -> userService.findByUserName("SomeUserName"), "User not found");

    }

    @Test
    void should_save_user() {

        when(userRepository.save(any())).thenReturn(user);
        UserDTO actualDTO = userService.save(userDTO);

        assertThat(actualDTO).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(user);

        verify(passwordEncoder).encode(anyString());

    }

    @Test
    void shouldUpdateUser() {

        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(user);

        when(userRepository.save(any())).thenReturn(user);


        UserDTO actualDTO =userService.update(userDTO);

        verify(passwordEncoder).encode(anyString());

        assertThat(actualDTO).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(user);

    }


}













































