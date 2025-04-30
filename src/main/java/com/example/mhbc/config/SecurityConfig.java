package com.example.mhbc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests( authorize -> authorize
                .requestMatchers("/", "/login","/join","/error","/index","/home","wedding").permitAll()
                .requestMatchers("/booking").hasAnyRole("ADMIN","USER")
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/loginProc")
                .usernameParameter("userid")
                .passwordParameter("pwd")
                .defaultSuccessUrl("/")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout") // 로그아웃 URL 지정
                .logoutSuccessUrl("/") // 로그아웃 성공 후 리다이렉트할 URL
                .invalidateHttpSession(true) // 세션 무효화
                .clearAuthentication(true) // 인증 정보 삭제
                .deleteCookies("JSESSIONID") // 쿠키 삭제
                .permitAll() // 모든 사용자에게 로그아웃 URL 접근 허용
        );
        return http.build();
    }


    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
