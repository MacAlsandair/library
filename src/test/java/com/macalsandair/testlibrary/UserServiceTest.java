package com.macalsandair.testlibrary;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.macalsandair.library.auth.PasswordChangeDTO;
import com.macalsandair.library.auth.UserRegistrationDTO;
import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;
import com.macalsandair.library.user.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void registerNewUser_usernameAlreadyExists_throwsException() {
        UserRegistrationDTO registration = new UserRegistrationDTO();
        registration.setUsername("existingUsername");
        when(userRepository.findByUsername(registration.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(ResponseStatusException.class, () -> {
            userService.registerNewUser(registration);
        });
    }
    
    @Test
    public void registerNewAdmin_usernameAlreadyExists_throwsException() {
        UserRegistrationDTO registration = new UserRegistrationDTO();
        registration.setUsername("existingUsername");
        when(userRepository.findByUsername(registration.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(ResponseStatusException.class, () -> {
            userService.registerNewAdmin(registration);
        });
    }
    
    @Test
    public void changePassword_usernameNotFound_throwsException() {
        PasswordChangeDTO changeDTO = new PasswordChangeDTO();
        // set up the DTO here...

        when(userRepository.findByUsername("nonexistingUsername")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            userService.changePassword(changeDTO, "nonexistingUsername");
        });
    }
    
    @Test
    public void deleteUser_usernameNotFound_throwsException() {
        when(userRepository.findByUsername("nonexistingUsername")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            userService.deleteUser("nonexistingUsername");
        });
    }
    
    @Test
    public void deleteUser_usernameFound_invokesDelete() {
        User user = new User();
        when(userRepository.findByUsername("existingUsername")).thenReturn(Optional.of(user));

        userService.deleteUser("existingUsername");

        verify(userRepository).delete(user);
    }
 }