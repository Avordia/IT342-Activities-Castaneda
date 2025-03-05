package com.castaneda.oauth2login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/images/**", "/logout").permitAll() // Allow access to login, static resources, and logout
                .anyRequest().authenticated() // All other requests require authentication
            )
            .formLogin(form -> form
                .loginPage("/login") // Custom login page
                .permitAll() // Allow everyone to access the login page
                .defaultSuccessUrl("/home", true) // Redirect to /home after successful login
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login") // Use the same login page for OAuth2 login
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // Logout URL
                .logoutSuccessUrl("/login") // Redirect to login page after logout
                .invalidateHttpSession(true) // Invalidate the session
                .clearAuthentication(true) // Clear the authentication
                .addLogoutHandler(logoutHandler()) // Optional: Add a custom logout handler
                .permitAll() // Allow everyone to access the logout endpoint
            );

        return http.build();
    }

    // Optional: Custom logout handler
    @Bean
    public LogoutHandler logoutHandler() {
        return new SecurityContextLogoutHandler();
    }
}