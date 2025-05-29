package com.catalin.vibelog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Global CORS configuration for the application, defining allowed origins, methods,
 * headers, and credentials policy for cross-origin requests.
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a {@link CorsFilter} bean that applies CORS rules to all incoming requests.
     * <p>
     * Configured to allow requests from configured frontend origins with specified methods,
     * headers, and to permit credentials (cookies, authorization headers).
     * </p>
     *
     * @return a configured {@link CorsFilter}
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Allowed origins for front-end applications
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8888"));
        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allowed headers in CORS requests
        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // Allow credentials such as cookies or authorization headers
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
