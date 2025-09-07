package com.makemytrip.makemytrip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig
{
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")// Allow all endpoints
                        .allowedOrigins("*")//Allow all origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTION")//Allow Specific HTTP Methods
                        .allowedHeaders("*")// Allow all heads
                        .allowCredentials(false);// Disallow credentials for safety
            }
        };
    }
}
