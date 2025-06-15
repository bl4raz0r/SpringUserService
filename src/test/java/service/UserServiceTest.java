package service;

import dto.UserDTO;
import dto.UserResponseDTO;
import model.User;
import repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "test@example.com", 30, LocalDateTime.now());
        userDTO = new UserDTO();
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setAge(30);
    }

    @Test
    void testGetAllUsersReturningUsersListResponseDTO() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDTO> responseDTOs = userService.getAllUsers();

        assertFalse(responseDTOs.isEmpty());
        assertEquals(1, responseDTOs.size());
        assertEquals(user.getName(), responseDTOs.get(0).getName());
    }

    @Test
    void testCreateValidUserReturningCreatedUserResponseDTO() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO responseDTO = userService.createUser(userDTO);

        assertNotNull(responseDTO);
        assertEquals(user.getName(), responseDTO.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithDuplicateEmailThrowsBadRequest() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(user);

        assertThrows(ResponseStatusException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void testGetByEmailReturningValidUserResponseDTO() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        UserResponseDTO responseDTO = userService.getUserByEmail(user.getEmail());

        assertNotNull(responseDTO);
        assertEquals(user.getName(), responseDTO.getName());
    }

    @Test
    void testGetByNonExistingEmailThrowsNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.getUserByEmail("nonexistent@example.com"));
    }

    @Test
    void testGetUserByIdReturningValidUserResponseDTO() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponseDTO responseDTO = userService.getUserById(user.getId());

        assertNotNull(responseDTO);
        assertEquals(user.getName(), responseDTO.getName());
    }

    @Test
    void testGetUserByNonExistingIdThrowsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(999L));
    }

    @Test
    void testUpdateByIdReturningUpdatedUserResponseDTO() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO responseDTO = userService.updateUser(user.getId(), userDTO);

        assertNotNull(responseDTO);
        assertEquals(user.getName(), responseDTO.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserByNonExistingIdThrowsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(999L, userDTO));
    }

    @Test
    void testDeleteByIdNoReturning() {
        when(userRepository.existsById(user.getId())).thenReturn(true);

        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void testDeleteUserByNonExistingIdThrowsNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(999L));
    }
}
