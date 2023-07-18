package com.macalsandair.library.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserExistsFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String username = getUsernameFromToken(request); // Implement this method to extract the username from the JWT token
        
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private String getUsernameFromToken(HttpServletRequest request) {
        // Implement this method to extract the username from the JWT token
        // You can use a library like jjwt or nimbus-jose-jwt to parse the token and retrieve the username
        // Return the username as a String
    }
}
