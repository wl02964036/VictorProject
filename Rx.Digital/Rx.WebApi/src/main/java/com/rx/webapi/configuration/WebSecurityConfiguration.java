package com.rx.webapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rx.webapi.interceptor.JwtFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Autowired
    private JwtFilter jwtFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(withDefaults -> withDefaults.disable())  // 關閉 login 表單
                .httpBasic(withDefaults -> withDefaults.disable())  // 關閉 basic auth
                .authorizeHttpRequests(auth -> auth
                    .antMatchers("/static/**",
                            "/files/**",
                            "/images/**",
                            "/upload",
                            "/angular/wav/**",
                            "/angular/**", // angular api
                            "/login", // angular登入畫面
                            "/logout", // angular登出畫面
                            "/manage", // angular內嵌畫面
                            "/manage/**",
                            "/index.html",
                            "/", // 根目錄
                            "/*.js", "/*.css", "/*.ico", // 頂層靜態資源
                            "/**/*.js", "/**/*.css", "/assets/**", // 子資料夾
                            "/favicon.ico" // angular內嵌畫面
                            ).permitAll()  // 開放不檢核
                    .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // jwt檢核的白名單要另外設
                .build();
    }

}