package com.catalin.vibelog.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for serving static file resources.
 * <p>
 * Configures a resource handler that exposes the local "uploads" directory
 * under the "/uploads/**" URL pattern, allowing files to be served directly from disk.
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Register resource handlers for static file serving.
     * <p>
     * Maps HTTP requests matching "/uploads/**" to the local filesystem path "uploads/".
     * This enables direct access to uploaded media through HTTP URLs.
     * </p>
     *
     * @param registry the registry to which resource handlers can be added
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
