package com.example.mhbc.config;

import com.example.mhbc.service.UserPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/**").permitAll()
                .requestMatchers("/booking").hasAnyRole("ADMIN", "USER")
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

    /**
     * 암호화 없이 평문 비교를 위해 PasswordEncoder를 사용하지 않음
     * 실제로는 아래 메소드를 사용하지 않음
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 암호화하지 않고 평문 비밀번호 비교하도록 빈을 반환하지 않음
        return null;
    }
}
