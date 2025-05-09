package com.example.mhbc.controller;

import com.example.mhbc.dto.MemberDTO;
import com.example.mhbc.dto.SocialUserInfoDTO;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.SnsEntity;
import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.repository.SnsRepository;
import com.example.mhbc.service.KakaoService;
import com.example.mhbc.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/member")
public class MemberController {

  private final MemberRepository memberRepository;
  private final KakaoService kakaoService;
  private final SnsRepository snsRepository;

  @Autowired
  private LoginService loginService;

  @GetMapping("/login")
  public String loginForm(HttpSession session) {
    // 세션에 로그인 정보가 있으면 로그인 페이지로 가는 대신 메인 페이지로 리디렉션
    if (session.getAttribute("loginMember") != null || session.getAttribute("loginSnsUser") != null) {
      return "redirect:/"; // 로그인 상태라면 메인 페이지로 리디렉션
    }
    return "member/login"; // 로그인 상태가 아니라면 로그인 페이지를 보여줌
  }


  @PostMapping("/loginProc")
  public String loginPost(@RequestParam("userid") String userid,
                          @RequestParam("pwd") String pwd,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {

    MemberEntity member = loginService.login(userid, pwd);

    if (member != null) {
      session.setAttribute("loginMember", member);
      System.out.println(">>>> 로그인 세션 저장됨: " + session.getAttribute("loginMember"));
      session.removeAttribute("loginSnsUser");

      // 전화번호 없으면 입력 페이지로 이동
      if (member.getMobile() == null || member.getMobile().trim().isEmpty()) {
        return "redirect:/api/member/mobile";
      }
      return "redirect:/";
    } else {
      redirectAttributes.addFlashAttribute("error", "아이디나 비밀번호가 틀립니다.");
      return "redirect:/api/member/login";
    }
  }

  // 메인 페이지 접근 시 로그인 상태 확인
  @GetMapping("/")
  public String homePage(HttpSession session) {
    MemberEntity member = (MemberEntity) session.getAttribute("loginMember");
    SnsEntity snsUser = (SnsEntity) session.getAttribute("loginSnsUser");

    if (member == null && snsUser == null) {
      return "redirect:/api/member/login";
    }

    // 전화번호 없으면 입력 페이지로 이동
    if ((member != null && (member.getMobile() == null || member.getMobile().isEmpty())) ||
            (snsUser != null && (snsUser.getSnsMobile() == null || snsUser.getSnsMobile().isEmpty()))) {
      return "redirect:/api/member/mobile";
    }

    return "index";
  }

  @GetMapping("/home")
  public String homePage(HttpSession session, Model model) {
    String loggedInUser = (String) session.getAttribute("loggedInUser");
    boolean isLoggedIn = (loggedInUser != null);

    model.addAttribute("isLoggedIn", isLoggedIn);
    return "index";  // index.html을 보여주기 위해
  }


  @GetMapping("/mobile")
  public String phoneNumberPage(HttpSession session) {
    MemberEntity member = (MemberEntity) session.getAttribute("loginMember");
    SnsEntity snsUser = (SnsEntity) session.getAttribute("loginSnsUser");

    if (member == null && snsUser == null) {
      return "redirect:/api/member/login";
    }

    return "member/mobile";
  }

  @PostMapping("/mobile")
  public String phoneNumberSubmit(@RequestParam("mobile") String mobile, HttpSession session) {
    MemberEntity memberSession = (MemberEntity) session.getAttribute("loginMember");
    SnsEntity snsSession = (SnsEntity) session.getAttribute("loginSnsUser");

    if (snsSession != null) {
      snsSession.setSnsMobile(mobile);
      snsRepository.save(snsSession);
      session.setAttribute("loginSnsUser", snsSession);
      return "redirect:/";
    }

    if (memberSession != null) {
      memberSession.setMobile(mobile);
      memberRepository.save(memberSession);
      session.setAttribute("loginMember", memberSession);
      return "redirect:/";
    }

    return "redirect:/api/member/login";
  }

  @GetMapping("/kakao")
  public String sociallogin(@RequestParam("code") String code, HttpSession session) {
    String accessToken = kakaoService.getKakaoAccessToken(code);
    SocialUserInfoDTO userInfo = kakaoService.getUserInfoFromKakao(accessToken);

    if (userInfo == null || userInfo.getSnsEmail() == null) {
      return "redirect:/api/member/login";
    }

    Optional<SnsEntity> existingSnsUserOpt = snsRepository.findBySnsId(userInfo.getUserid());
    SnsEntity snsUser;

    if (existingSnsUserOpt.isEmpty()) {
      snsUser = new SnsEntity();
      snsUser.setSnsType("KAKAO");
      snsUser.setSnsId(userInfo.getUserid());
      snsUser.setSnsEmail(userInfo.getSnsEmail());
      snsUser.setSnsName(userInfo.getSnsName());
      snsUser.setConnectedAt(LocalDateTime.now());
      snsRepository.save(snsUser);
    } else {
      snsUser = existingSnsUserOpt.get();
    }

    session.setAttribute("loginSnsUser", snsUser);

    if (snsUser.getSnsMobile() == null || snsUser.getSnsMobile().isEmpty()) {
      return "redirect:/api/member/mobile";
    }

    return "redirect:/";
  }

  @RequestMapping("/join")
  public String join() {
    return "member/join";
  }

  // 아이디 중복 확인
  @GetMapping("/idcheck")
  @ResponseBody
  public String checkUserId(@RequestParam("userid") String userid) {
    if (memberRepository.existsByUserid(userid)) {
      return "Y";  // 중복된 아이디
    }
    return "N";  // 사용 가능한 아이디
  }

  @PostMapping("/join")
  public String joinProc(@RequestParam("userid") String userid,
                         @RequestParam("pwd") String pwd,
                         @RequestParam("name") String name,
                         @RequestParam("telecom") String telecom,
                         @RequestParam("email") String email,
                         @RequestParam("nickname") String nickname,
                         @RequestParam("mobile1") String mobile1,
                         @RequestParam("mobile2") String mobile2,
                         @RequestParam("mobile3") String mobile3) {

    // 모바일 번호 합치기
    String mobile = (!mobile1.isEmpty() && !mobile2.isEmpty() && !mobile3.isEmpty())
            ? mobile1 + "-" + mobile2 + "-" + mobile3 : null;

    MemberDTO memberDTO = MemberDTO.builder()
            .userid(userid)
            .pwd(pwd)
            .name(name)
            .telecom(telecom)
            .nickname(nickname)
            .email(email)
            .mobile(mobile)
            .grade(1)
            .status("정상")
            .build();

    // 아이디 중복 체크 후 저장
    if (!memberRepository.existsByUserid(userid)) {
      memberRepository.save(memberDTO.toEntity());
    } else {
      System.out.println("이미 가입된 사용자입니다.");
      return "redirect:/api/member/join";  // 이미 존재하는 경우 회원가입 페이지로 리디렉션
    }

    return "redirect:/api/member/login";
  }


  @RequestMapping("/findidpw")
  public String findidpw() {
    return "member/findidpw";
  }

  @RequestMapping("/adminuser")
  public String adminuser() {
    return "member/adminuser";
  }

  @RequestMapping("/adminuserinfo")
  public String adminuserinfo() {
    return "member/adminuserinfo";
  }
}
