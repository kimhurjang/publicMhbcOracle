package com.example.mhbc.config;

import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final MemberRepository memberRepository;

    public CustomAuthenticationFailureHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        System.out.println("🔴 로그인 실패 - CustomAuthenticationFailureHandler 진입");

        String userid = request.getParameter("userid");
        System.out.println("🔴 전달된 userid: " + userid);
        System.out.println("🔴 exception: " + exception.getClass().getSimpleName());

        String errorCode = "UNKNOWN";

        if (exception instanceof DisabledException) {
            Optional<MemberEntity> memberOpt = memberRepository.findByUserid(userid);
            if (memberOpt.isPresent()) {
                String status = memberOpt.get().getStatus();
                System.out.println("🔴 계정 상태: " + status);

                if ("탈퇴".equalsIgnoreCase(status)) {
                    errorCode = "WITHDRAW";
                } else if ("정지".equalsIgnoreCase(status)) {
                    errorCode = "STOP";
                }
            }
        } else if (exception instanceof LockedException) {
            errorCode = "STOP";
        } else if (exception instanceof BadCredentialsException) {
            errorCode = "BAD_CREDENTIALS";
        }

        System.out.println("🔴 최종 errorCode: " + errorCode);

        String redirectUrl = "/api/member/login?errorCode=" + errorCode;
        if (userid != null) {
            redirectUrl += "&userid=" + URLEncoder.encode(userid, "UTF-8");
        }

        response.sendRedirect(redirectUrl);
    }
}