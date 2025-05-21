package com.example.mhbc.config;

import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.entity.MemberEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.Optional;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final MemberRepository memberRepository;

    public CustomAuthenticationFailureHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {

        String userid = request.getParameter("userid");
        String errorCode = "UNKNOWN";

        if (exception instanceof DisabledException) {
            Optional<MemberEntity> memberOpt = memberRepository.findByUserid(userid);
            if (memberOpt.isPresent()) {
                String status = memberOpt.get().getStatus();
                if ("탈퇴".equalsIgnoreCase(status)) {
                    errorCode = "WITHDRAW";
                } else if ("정지".equalsIgnoreCase(status)) {
                    errorCode = "STOP";
                }
            }
        } else if (exception instanceof BadCredentialsException) {
            errorCode = "BAD_CREDENTIALS";
        }

        response.sendRedirect("/api/member/login?errorCode=" + errorCode);
    }
}
