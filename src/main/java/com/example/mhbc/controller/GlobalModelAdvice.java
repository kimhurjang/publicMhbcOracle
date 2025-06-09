package com.example.mhbc.controller;

import com.example.mhbc.dto.MemberDTO;
import com.example.mhbc.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * GlobalModelAdvice
 * - 모든 컨트롤러에 공통 모델 속성 자동 주입
 */
@Slf4j
@ControllerAdvice
public class GlobalModelAdvice {

  /**
   * 아코디언 메뉴 활성화용: 현재 요청 URI
   * 현재 요청 URI를 모든 모델에 currentPath로 추가
   * (ex: currentPath="/admin/hall/list")
   */
  @ModelAttribute("currentPath")
  public String currentPath(HttpServletRequest request) {
    return request.getRequestURI();
  }

  /**
   * 현재 로그인된 사용자 ID (없으면 "비회원")
  @ModelAttribute("loginId")
  public String loginId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    //실제사용용

    return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())
      ? auth.getName()
      : "비회원";
    // ✅ 임시 개발용: SecurityContext가 비었거나 비회원이면 "admin" 강제 로그인
    if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
      Authentication fakeAuth = new UsernamePasswordAuthenticationToken(
              "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
      );
      SecurityContextHolder.getContext().setAuthentication(fakeAuth);
      return "admin";

    }
    return auth.getName();
  }
  */

  @ModelAttribute("loginMember")
  public MemberDTO loginMember() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
      UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

      MemberDTO dto = new MemberDTO();
      dto.setIdx(userDetails.getIdx());         // 회원 IDX
      dto.setUserid(userDetails.getUsername()); // 아이디
      dto.setGrade(userDetails.getGrade());     // 등급
      return dto;
    }

    return null;
  }

  /**
   * 기본 웹 타이틀 설정 (개별 컨트롤러에서 덮어쓰기 가능)
   */
  @ModelAttribute
  public void defaultWebTitle(Model model) {
    if (!model.containsAttribute("webtitle")) {
      model.addAttribute("webtitle", "만화방초");
    }
  }

/**
 *    main 버튼 활성화용: 현재 요청 URI구하기
 * */
  @ModelAttribute("currentUri")
  public String currentUri(HttpServletRequest request) {
    return request.getRequestURI();
  }
}
