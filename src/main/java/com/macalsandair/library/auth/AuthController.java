package com.macalsandair.library.auth;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;
import com.macalsandair.library.user.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
    private JwtEncoder encoder;
    @Autowired
    private UserService userService;

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
        String message = userService.registerNewUser(registration);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<String> registerNewAdmin(@RequestBody UserRegistrationDTO registration){
        String message = userService.registerNewAdmin(registration);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO passwordChange, Authentication authentication){
        String message = userService.changePassword(passwordChange, authentication.getName());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(Authentication authentication){
        String message = userService.deleteUser(authentication.getName());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
