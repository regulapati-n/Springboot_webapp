package com.csye6225.cloud.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import com.csye6225.cloud.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    CustomUserDetailsService userService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authz) -> authz.requestMatchers(HttpMethod.POST, "/v1/user").permitAll()
                .requestMatchers(("/healthz")).permitAll()
                .requestMatchers("/v1/user").permitAll()
                .anyRequest().permitAll());
        http.csrf((csrf) -> csrf.disable());
        http.httpBasic();
        return http.build();
    }

}
