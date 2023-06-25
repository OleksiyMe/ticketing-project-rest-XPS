package com.cydeo.unit_test_review;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
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
    private UserMapper userMapper = new UserMapper(new ModelMapper());

    @InjectMocks
    private UserServiceImpl userService;

    User user;
    UserDTO userDTO;


    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
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
/*
        assertThrowsExactly(
                NoSuchElementException.class,
                () -> userService.findByUserName("SomeUserName"), "User not found");

        assertThrows(
                NoSuchElementException.class,
                () -> userService.findByUserName("SomeUserName"));*/

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

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(user);

        when(userRepository.save(any())).thenReturn(user);


        UserDTO actualDTO = userService.update(userDTO);

        verify(passwordEncoder).encode(anyString());

        assertThat(actualDTO).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(user);

    }

    @Test
    void shouldDelete_manager() throws TicketingProjectException {
        User managerUser = getUser("Manager");
        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(managerUser);
        when(userRepository.save(any())).thenReturn(managerUser);
        //return empty ArrayList (no projects -- u can delete)
        when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(new ArrayList<>());

        userService.delete(userDTO.getUserName());

        assertTrue(managerUser.getIsDeleted());
        assertNotEquals("user3", managerUser.getUserName());

    }

    @Test
    void shouldDelete_employee() throws TicketingProjectException {

        User user_employee = getUser("Employee");
        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(user_employee);
        when(userRepository.save(any())).thenReturn(user_employee);
        //return empty ArrayList (no projects -- u can delete)
        when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(new ArrayList<>());

        userService.delete(userDTO.getUserName());

        assertTrue(user_employee.getIsDeleted());
        assertNotEquals("user3", user_employee.getUserName());

    }

    @Test
    void shouldThrowExceptionWhenDeleteManager() throws TicketingProjectException {
        User managerUser = getUser("Manager");
        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(managerUser);

        when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(List.of(new ProjectDTO(), new ProjectDTO()));

        Throwable throwable = catchThrowable(() -> userService.delete(userDTO.getUserName()));


        assertInstanceOf(TicketingProjectException.class, throwable);
        assertEquals("User can not be deleted", throwable.getMessage());

        verify(userMapper, atLeastOnce()).convertToDto(any());

    }

    @Test
    void shouldThrowExceptionWhenDeleteEmployee() throws TicketingProjectException {
        User employeeUser = getUser("Employee");
        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(employeeUser);

        when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(List.of(new TaskDTO(), new TaskDTO()));

        Throwable throwable = catchThrowable(() -> userService.delete(userDTO.getUserName()));

        assertInstanceOf(TicketingProjectException.class, throwable);
        assertEquals("User can not be deleted", throwable.getMessage());

        verify(userMapper, atLeastOnce()).convertToDto(any());

    }


    @ParameterizedTest
    @ValueSource(strings = {"Manager", "Employee"})
    void shouldDeleteUser(String value) throws TicketingProjectException {

        User testUser = getUser(value);

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(testUser);
        when(userRepository.save(any())).thenReturn(testUser);
        //return empty ArrayList (no projects -- u can delete)

        lenient().when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(new ArrayList<>());
        lenient().when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(new ArrayList<>());

        userService.delete(userDTO.getUserName());

        assertTrue(testUser.getIsDeleted());
        assertNotEquals("user3", testUser.getUserName());


    }

    private User getUser(String role) {

        User user3 = new User();

        user3.setUserName("user3");
        user3.setEnabled(false);
        user3.setIsDeleted(false);
        user3.setRole(new Role(role));

        return user3;
    }

//WithMockUser(username="someUsername", password ="Abc1", roles="Manager")


}













































