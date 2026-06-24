package com.ivar.audit.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ivar.audit.security.jwt.JwtFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig{
	private final JwtFilter jwtFilter;
	
	public SecurityConfig(JwtFilter jwtFilter){
		this.jwtFilter=jwtFilter;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    return http
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                    .requestMatchers(
	                    		"/test-email",
	                            "/v3/api-docs/**",
	                            "/swagger-ui/**",
	                            "/swagger-ui.html",
	                            "/auth/**"   // ✅ allow token generation
	                    ).permitAll()
	                    .anyRequest().authenticated()
	            )
	            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	}
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	    return http
//	            .csrf(csrf -> csrf.disable())
//	            .authorizeHttpRequests(auth -> auth
//	                    .anyRequest().permitAll()
//	            )
//	            .build();
//	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
