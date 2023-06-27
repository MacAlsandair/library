package com.macalsandair.library.auth;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtEncoder encoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    @PostMapping("")
    public String auth(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@RequestBody UserRegistrationDTO registration){
        String username = registration.getUsername();
        Optional<User> optUser = userRepository.findByUsername(username);
        if(optUser.isPresent()){
            return new ResponseEntity<>("Username already exists!", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setEnabled(true);
        List<Roles> roles = Arrays.asList(Roles.USER);
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>("User Registered Successfully!", HttpStatus.CREATED);
    }
    
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO passwordChange, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        if(passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword())){
            if(!passwordChange.getNewPassword().equals(passwordChange.getOldPassword())){
                user.setPassword(passwordChange.getNewPassword());
                userRepository.save(user);
                return new ResponseEntity<>("Password Changed Successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("New Password is the same as the old one!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Invalid Old Password!", HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        userRepository.delete(user);
        return new ResponseEntity<>("User Deleted Successfully!", HttpStatus.OK);
    }

}
