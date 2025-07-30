package com.shibam.clinic.online_doctor_clinic.Configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.shibam.clinic.online_doctor_clinic.Services.DoctorService;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private DoctorService doctorService;

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            switch (role) {
                case "ROLE_DOCTOR":
                    String email = authentication.getName();
                    if (doctorService.isDoctorApproved(email)) {
                        response.sendRedirect("/doctor");
                    } else {
                        response.sendRedirect("/doctor/wait");
                    }
                    break;
                case "ROLE_PATIENT":
                    response.sendRedirect("/patient");
                    break;
                case "ROLE_ADMIN":
                    response.sendRedirect("/admin");
                    break;
                default:
                    response.sendRedirect("/");
                    break;
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Explicitly allow GET and POST for registration and login
                        .requestMatchers(HttpMethod.GET, "/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/favicon.ico")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/register", "/authenticate") // Changed /login to /authenticate to avoid conflict
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/doctor/wait").hasRole("DOCTOR")
                        .requestMatchers("/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/patient/**").hasRole("PATIENT")
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login") // Your custom login page
                        .loginProcessingUrl("/authenticate") // Changed from /login to avoid conflict with your GET /login
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())

                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/register")
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}