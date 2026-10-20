package com.newsblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * URL rules. Order matters: the first matching rule wins.
 * Users come from the database through UserService (a UserDetailsService bean).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/error", "/favicon.ico").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/author/**").hasAnyRole("ADMIN", "EDITOR", "AUTHOR")
                .requestMatchers("/moderation/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers(HttpMethod.POST, "/categories/**", "/tags/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers("/newsletter/subscribe", "/newsletter/verify", "/newsletter/unsubscribe").permitAll()
                .requestMatchers("/newsletter/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers("/comments/**", "/profile/**", "/dashboard").authenticated()
                .requestMatchers(HttpMethod.POST, "/article/*/save").authenticated()
                .anyRequest().permitAll())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/?logout")
                .permitAll());
        return http.build();
    }
}
