package com.example.mhbc.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * GlobalControllerAdvice
 * - 모든 컨트롤러에 로그인 사용자 ID(loginId) 자동 주입
 */
@ControllerAdvice
public class GlobalControllerAdvice {

  @ModelAttribute("loginId")
  public String loginId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    /* 사용할 때
    if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
      return authentication.getName(); // 로그인된 사용자 ID 반환
    }
    return null;
     */

    // 임시 로그인 강제 주입 (only 개발용)
    if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
      Authentication fakeAuth = new UsernamePasswordAuthenticationToken("testuser", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
      SecurityContextHolder.getContext().setAuthentication(fakeAuth);
      return "testuser"; // 임시 로그인 ID 반환
    }

    // 진짜 로그인 상태일 때
    return authentication.getName();

  }
}
