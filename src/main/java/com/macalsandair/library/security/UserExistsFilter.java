package com.macalsandair.library.security;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;


public class UserExistsFilter extends OncePerRequestFilter {

    //@Autowired
    private UserRepository userRepository;
    
	private final RSAPublicKey key;
	
    public UserExistsFilter(
           RSAPublicKey key, UserRepository userRepository
        ) {
            this.key = key;
            this.userRepository = userRepository;
        }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    	
        String jwtToken = resolveToken(request);

        if (jwtToken == null) {
            chain.doFilter(request, response);
            return;
        }
    	
    	String username = getUsernameFromToken(request);
        
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }
        
        chain.doFilter(request, response);
    }
    

    private String getUsernameFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
   

}
