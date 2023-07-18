package com.macalsandair.library.user;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.macalsandair.library.auth.PasswordChangeDTO;
import com.macalsandair.library.auth.UserRegistrationDTO;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerNewUser(UserRegistrationDTO registration) {
        String username = registration.getUsername();
        Optional<User> optUser = userRepository.findByUsername(username);
        if(optUser.isPresent()){
            throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "Username already exists!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setEnabled(true);
        List<Role> roles = Arrays.asList(Role.USER);
        user.setRoles(roles);
        userRepository.save(user);
        return "User Registered Successfully!";
    }

    public String registerNewAdmin(UserRegistrationDTO registration) {
        String username = registration.getUsername();
        Optional<User> optUser = userRepository.findByUsername(username);
        if(optUser.isPresent()){
            throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "Username already exists!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setEnabled(true);
        List<Role> roles = Arrays.asList(Role.USER, Role.ADMIN);
        user.setRoles(roles);
        userRepository.save(user);
        return "User Registered Successfully!";
    }

    public String changePassword(PasswordChangeDTO passwordChange, String username) {
    	if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();
            if(passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword())){
                if(!passwordChange.getNewPassword().equals(passwordChange.getOldPassword())){
                    String encryptedPassword = passwordEncoder.encode(passwordChange.getNewPassword());
                    user.setPassword(encryptedPassword);
                    userRepository.save(user);
                    return "Password Changed Successfully!";
                } else {
                    throw new ResponseStatusException(
                      HttpStatus.BAD_REQUEST, "New Password is the same as the old one!");
                }
            } else {
                throw new ResponseStatusException(
                  HttpStatus.BAD_REQUEST, "Invalid Old Password!");
            }
    	}
    	else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username not found");
    	}
    }

    public String deleteUser(String username) {
    	if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();
            userRepository.delete(user);
            return "User Deleted Successfully!";
    	}
    	else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username not found");
    	}
    }
}

