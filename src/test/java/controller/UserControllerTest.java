package controller;

import dto.UserDTO;
import dto.UserResponseDTO;
import service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponseDTO userResponseDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("Test User");
        userResponseDTO.setEmail("test@example.com");
        userResponseDTO.setAge(30);
        userResponseDTO.setCreatedAt(LocalDateTime.now());

        userDTO = new UserDTO();
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setAge(30);
    }

    @Test
    void testGetAllUsersReturningUsersList() {
        List<UserResponseDTO> userList = new ArrayList<>();
        userList.add(userResponseDTO);
        when(userService.getAllUsers()).thenReturn(userList);

        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testCreateValidUserReturningCreatedUser() {
        when(userService.createUser(userDTO)).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = userController.createUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponseDTO.getName(), response.getBody().getName());
    }

    @Test
    void testGetUserByIdReturningValidUser() {
        when(userService.getUserById(1L)).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponseDTO.getName(), response.getBody().getName());
    }

    @Test
    void testGetByEmailReturningValidUser() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = userController.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponseDTO.getName(), response.getBody().getName());
    }

    @Test
    void testUpdateByIdReturningUpdatedUser() {
        when(userService.updateUser(1L, userDTO)).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = userController.updateUser(1L, userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponseDTO.getName(), response.getBody().getName());
    }

    @Test
    void testDeleteByIdNoReturning() {
        ResponseEntity<HttpStatus> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }
}
