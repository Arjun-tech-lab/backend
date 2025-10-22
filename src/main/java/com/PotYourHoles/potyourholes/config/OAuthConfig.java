package com.PotYourHoles.potyourholes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OAuthConfig {

    // Inject frontend URL from environment variable
    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Allow certain endpoints publicly
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/**", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Enable Google OAuth login
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl(frontendUrl, true) // redirect to frontend after login
                )
                // Enable logout
                .logout(logout -> logout
                        .logoutSuccessUrl(frontendUrl) // redirect after logout
                        .permitAll()
                )
                // Disable CSRF for simplicity (not recommended in prod without CSRF token)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // Allow frontend requests (CORS)
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(frontendUrl) // use environment variable
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true);
            }
        };
    }
}
