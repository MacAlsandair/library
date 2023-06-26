package com.macalsandair.library.auth;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
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
        user.setPassword(registration.getPassword());
        user.setEnabled(true);
        userRepository.save(user);
        return new ResponseEntity<>("User Registered Successfully!", HttpStatus.CREATED);
    }

}
