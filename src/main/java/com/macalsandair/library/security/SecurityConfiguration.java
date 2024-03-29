package com.macalsandair.library.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.macalsandair.library.user.Role;
import com.macalsandair.library.user.UserDetailsServiceImpl;
import com.macalsandair.library.user.UserRepository;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import com.auth.core.security.JwtAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {
	
	private final RSAPublicKey key;
	private final RSAPrivateKey priv;
	
	@Autowired
	private UserExistsFilter userExistsFilter;
	
    public SecurityConfiguration(
            @Value("${jwt.public.key}") RSAPublicKey key,
            @Value("${jwt.private.key}") RSAPrivateKey priv
        ) {
            this.key = key;
            this.priv = priv;
        }
    
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
	    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
		
		
		http
			.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers("/api/auth/register").permitAll()
					//.requestMatchers("/api/**").hasAnyRole(Role.ADMIN.name())
					.anyRequest().authenticated())
			.csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/**"))
			.httpBasic(Customizer.withDefaults())
	        .oauth2ResourceServer(oauth2 -> oauth2
	                .jwt()
	                .jwtAuthenticationConverter(jwtAuthenticationConverter))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling((exceptions) -> exceptions
					.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
					.accessDeniedHandler(new BearerTokenAccessDeniedHandler())
					)
			.cors(Customizer.withDefaults())
			.addFilterAfter(userExistsFilter, BearerTokenAuthenticationFilter.class);
		return http.build();
	}

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(this.key).build();
        return jwtDecoder;
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
