package com.catalin.vibelog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Configures application security using JWT tokens within a stateless
 * Spring Security filter chain. Sets up CORS, CSRF disabling,
 * session management, endpoint authorization rules, and password encoding.
 */
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * Constructs SecurityConfig with the JWT utility for token operations.
     *
     * @param jwtUtil utility for generating and validating JWT tokens
     */
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates a JWT authentication filter bean to validate tokens
     * on incoming requests before the username/password filter.
     *
     * @return a configured {@link JwtAuthenticationFilter}
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    /**
     * Defines the main security filter chain for HTTP requests.
     * Configures CORS for the React frontend, disables CSRF,
     * enforces stateless sessions, and sets authorization rules:
     * <ul>
     *   <li>Public access to uploads and auth endpoints</li>
     *   <li>Public access to Swagger/OpenAPI documentation</li>
     *   <li>JWT authentication required for all other routes</li>
     * </ul>
     * The JWT filter is inserted before the standard authentication filter.
     *
     * @param http the {@link HttpSecurity} builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS configuration for React frontend
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // disable CSRF since tokens are used
                .csrf(csrf -> csrf.disable())
                // enforce stateless session; no HTTP sessions
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // authorization rules
                .authorizeHttpRequests(auth -> auth
                        // allow unauthenticated access to uploads and auth endpoints
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        // allow Swagger/OpenAPI UI and docs
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // require authentication for any other request
                        .anyRequest().authenticated()
                )
                // add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Provides a password encoder bean using BCrypt hashing algorithm
     * for secure password storage and verification.
     *
     * @return a {@link PasswordEncoder} instance for password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
