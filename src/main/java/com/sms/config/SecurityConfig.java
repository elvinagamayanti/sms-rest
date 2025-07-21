package com.sms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sms.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtTokenFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // DISABLE default logout behavior
        http.logout(logout -> logout.disable());

        http.authorizeHttpRequests(auth -> auth
                // .requestMatchers("/", "/login", "/logout", "/docs/**", "/error").permitAll()
                // .requestMatchers("/api/tahap/**", "/api/kegiatan/**",
                // "/api/notifications/**", "/api/activity-logs/**")
                // .hasAnyRole("ADMIN", "USER", "SUPERADMIN")
                // .requestMatchers("/api/direktorats/**",
                // "/api/deputis/**").hasAnyRole("ADMIN", "SUPERADMIN")
                // .requestMatchers("/api/roles/**", "/api/provinces/**", "/api/satkers/**",
                // "/api/programs/**",
                // "/api/outputs/**")
                // .hasRole("SUPERADMIN")
                // .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "SUPERADMIN")
                // .anyRequest().authenticated()
                // Public endpoints
                .requestMatchers("/", "/login", "/logout", "/docs/**", "/error").permitAll()

                // User Management - hierarchical access
                .requestMatchers("/api/users/current").authenticated()
                .requestMatchers("/api/users/statistics")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT", "ADMIN_PROVINSI", "OPERATOR_PROVINSI",
                        "ADMIN_SATKER", "OPERATOR_SATKER")
                .requestMatchers("/api/users/under-management")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI", "ADMIN_SATKER")
                .requestMatchers("/api/users/available-satkers")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI", "ADMIN_SATKER")
                .requestMatchers("/api/users/manageable-roles")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI", "ADMIN_SATKER")
                .requestMatchers("/api/users/bulk-status")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI", "ADMIN_SATKER")
                .requestMatchers("/api/users/*/transfer").hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI")
                .requestMatchers("/api/users/*/activate")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI", "ADMIN_SATKER")
                .requestMatchers("/api/users/*/deactivate")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI", "ADMIN_SATKER")
                .requestMatchers("/api/users/*/roles/*")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI", "ADMIN_SATKER")
                .requestMatchers("/api/users")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT", "ADMIN_PROVINSI", "OPERATOR_PROVINSI",
                        "ADMIN_SATKER", "OPERATOR_SATKER")

                // Kegiatan Management - scope-based access
                .requestMatchers("/api/kegiatans/*/assign-to-satkers")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT")
                .requestMatchers("/api/kegiatans/*/assign-to-provinces")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT")
                .requestMatchers("/api/kegiatans/*/assign-user")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "ADMIN_PROVINSI", "ADMIN_SATKER")
                .requestMatchers("/api/kegiatans/statistics")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT", "ADMIN_PROVINSI", "OPERATOR_PROVINSI",
                        "ADMIN_SATKER", "OPERATOR_SATKER")
                .requestMatchers("/api/kegiatans")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT", "ADMIN_PROVINSI", "OPERATOR_PROVINSI",
                        "ADMIN_SATKER", "OPERATOR_SATKER")

                // Tahap Management - scope-based access
                .requestMatchers("/api/tahap/*/update-status")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT", "ADMIN_PROVINSI", "OPERATOR_PROVINSI",
                        "ADMIN_SATKER", "OPERATOR_SATKER")
                .requestMatchers("/api/tahap")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT", "ADMIN_PROVINSI", "OPERATOR_PROVINSI",
                        "ADMIN_SATKER", "OPERATOR_SATKER")

                // Master Data Management - superadmin only
                .requestMatchers("/api/roles/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/provinces/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/satkers/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/programs/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/outputs/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/direktorats/**").hasAnyRole("ADMIN_PUSAT", "SUPERADMIN")
                .requestMatchers("/api/deputis/**").hasAnyRole("ADMIN_PUSAT", "SUPERADMIN")

                // Notifications and Logs
                .requestMatchers("/api/notifications/**")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT", "ADMIN_PROVINSI", "OPERATOR_PROVINSI",
                        "ADMIN_SATKER", "OPERATOR_SATKER")
                .requestMatchers("/api/activity-logs/**")
                .hasAnyRole("SUPERADMIN", "ADMIN_PUSAT", "OPERATOR_PUSAT", "ADMIN_PROVINSI", "OPERATOR_PROVINSI",
                        "ADMIN_SATKER", "OPERATOR_SATKER")

                .anyRequest().authenticated());
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}