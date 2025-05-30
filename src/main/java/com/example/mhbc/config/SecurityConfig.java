package com.example.mhbc.config;

import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.service.UserDetailServiceImpl;
import com.example.mhbc.service.UserPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailServiceImpl userDetailsService;

    @Autowired
    private MemberRepository memberRepository;  // MemberRepository 주입
    
    //static 내용물은 시큐리티 무시
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/image/**", "/css/**", "/js/**", "/fonts/**");
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/**").permitAll()
                .requestMatchers("/api/member/login", "/api/member/mobile", "/home", "/board/**").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(login -> login
                .loginPage("/api/member/login")
                .loginProcessingUrl("/api/member/loginProc")
                .usernameParameter("userid")
                .passwordParameter("pwd")
                .defaultSuccessUrl("/")
                .failureHandler(customAuthenticationFailureHandler())  // 커스텀 실패 핸들러 적용
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        http.sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/api/member/login")
                        .maximumSessions(1)
                        .expiredUrl("/api/member/login")
                )
                .userDetailsService(userDetailsService)
                .authenticationManager(new ProviderManager(
                        new DaoAuthenticationProvider() {{
                            setUserDetailsService(userDetailsService);
                            setPasswordEncoder(passwordEncoder());
                        }}
                ));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance(); // 개발 단계, 배포 전 BCrypt로 변경 권장
        return new UserPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(memberRepository);  // MemberRepository 넘김
    }
}
