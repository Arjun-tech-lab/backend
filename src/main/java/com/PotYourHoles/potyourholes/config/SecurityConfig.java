package com.PotYourHoles.potyourholes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    // Inject frontend URL from application.properties or environment variable
    @Value("${FRONTEND_URL:http://localhost:5173}") // fallback to localhost
    private String frontendUrl;

    // ------------------- CORS CONFIGURATION -------------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList(frontendUrl));
        configuration.setAllowCredentials(true);

        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization"
        ));

        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // ------------------- SECURITY FILTER CHAIN -------------------
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())       // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // Allow all requests
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Use our CorsConfigurationSource

        return http.build();
    }
}
