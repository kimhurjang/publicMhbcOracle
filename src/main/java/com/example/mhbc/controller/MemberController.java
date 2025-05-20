package com.example.mhbc.controller;

import com.example.mhbc.dto.MemberDTO;
import com.example.mhbc.dto.MemberSnsDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.List;
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
    if (session.getAttribute("loginMember") != null || session.getAttribute("loginSnsUser") != null) {
      return "redirect:/";
    }
    return "member/login";
  }

  @PostMapping("/loginProc")
  public String loginPost(@RequestParam("userid") String userid,
                          @RequestParam("pwd") String pwd,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {

    MemberEntity member = loginService.login(userid, pwd);

    if (member != null) {
      session.setAttribute("loginMember", member);
      session.removeAttribute("loginSnsUser");

      if (member.getMobile() == null || member.getMobile().trim().isEmpty()) {
        return "redirect:/api/member/mobile";
      }
      return "redirect:/";
    } else {
      redirectAttributes.addFlashAttribute("error", "아이디나 비밀번호가 틀립니다.");
      return "redirect:/api/member/login";
    }
  }

  @GetMapping("/")
  public String homePage(HttpSession session) {
    MemberEntity member = (MemberEntity) session.getAttribute("loginMember");
    SnsEntity snsUser = (SnsEntity) session.getAttribute("loginSnsUser");

    if (member == null && snsUser == null) {
      return "redirect:/api/member/login";
    }

    if ((member != null && (member.getMobile() == null || member.getMobile().isEmpty())) ||
            (snsUser != null && (snsUser.getSnsMobile() == null || snsUser.getSnsMobile().isEmpty()))) {
      return "redirect:/api/member/mobile";
    }

    return "index";
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

      // 회원이 이미 존재하면 연결하기 (회원 이메일 기준 조회)
      Optional<MemberEntity> memberOpt = memberRepository.findByEmail(userInfo.getSnsEmail());
      memberOpt.ifPresent(snsUser::setMember);

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

  @GetMapping("/idcheck")
  @ResponseBody
  public String checkUserId(@RequestParam("userid") String userid) {
    if (memberRepository.existsByUserid(userid)) {
      return "Y";
    }
    return "N";
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

    if (!memberRepository.existsByUserid(userid)) {
      memberRepository.save(memberDTO.toEntity());
    } else {
      return "redirect:/api/member/join";
    }

    return "redirect:/api/member/login";
  }

  @RequestMapping("/findidpw")
  public String findidpw() {
    return "member/findidpw";
  }

  @PostMapping("/find-id")
  public String findUserId(@RequestParam("name") String name,
                           @RequestParam("mobile1") String mobile1,
                           @RequestParam("mobile2") String mobile2,
                           @RequestParam("mobile3") String mobile3,
                           Model model) {

    String mobile = mobile1 + "-" + mobile2 + "-" + mobile3;

    MemberEntity member = memberRepository.findByNameAndMobile(name, mobile);

    if (member != null) {
      model.addAttribute("message", "당신의 아이디는: " + member.getUserid());
    } else {
      model.addAttribute("error", "아이디를 찾을 수 없습니다.");
      model.addAttribute("notFound", true); // 이 부분 추가
    }

    return "member/findidpw";
  }


  @GetMapping("/reset-pwd")
  public String resetPasswordPage(@RequestParam("userid") String userid, Model model) {
    model.addAttribute("userid", userid);
    return "member/pwdresetform";
  }

  @PostMapping("/find-password")
  public String findPassword(@RequestParam("id") String id,
                             @RequestParam("name") String name,
                             RedirectAttributes redirectAttributes) {

    MemberEntity member = memberRepository.findByUseridAndName(id, name);

    if (member != null) {
      return "redirect:/api/member/reset-pwd?userid=" + id;
    } else {
      redirectAttributes.addFlashAttribute("error", "아이디 또는 이름이 일치하지 않습니다.");
      return "redirect:/api/member/findidpw";
    }
  }

  @PostMapping("/reset-pwd")
  public String resetPassword(@RequestParam("userid") String userid,
                              @RequestParam("newPwd") String newPwd,
                              RedirectAttributes redirectAttributes) {

    Optional<MemberEntity> memberOptional = memberRepository.findByUserid(userid);

    if (memberOptional.isPresent()) {
      MemberEntity member = memberOptional.get();
      member.setPwd(newPwd); // 비밀번호 저장
      memberRepository.save(member);

      // 비밀번호 변경 후 메시지와 함께 리디렉션
      redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다. 다시 로그인 해주세요.");
      return "redirect:/api/member/reset-pwd-confirm"; // 비밀번호 변경 후 확인 페이지로 리디렉션
    } else {
      // 사용자가 없으면 아이디/비밀번호 찾기 페이지로 리디렉션
      redirectAttributes.addFlashAttribute("error", "해당 사용자가 존재하지 않습니다.");
      return "redirect:/api/member/findidpw"; // 존재하지 않으면 아이디/비밀번호 찾기 페이지로
    }
  }

  @GetMapping("/reset-pwd-confirm")
  public String resetPasswordConfirm(@ModelAttribute("message") String message, Model model) {
    model.addAttribute("message", message);
    return "member/popup"; // popup.html로 렌더링 (파일명에 맞게 수정)
  }
  @GetMapping("/adminuser")
  public String adminuser(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String keyword) {

    int pageSize = 10;
    Pageable pageable = PageRequest.of(page, pageSize);
    Page<MemberSnsDto> memberPage;

    // 조건 분기
    if ((status == null || status.equals("전체")) && (keyword == null || keyword.isEmpty())) {
      // 전체 조회 시에도 DTO 반환 메서드 사용
      memberPage = memberRepository.findAllWithSnsByStatusAndKeyword(null, null, pageable);
    } else {
      memberPage = memberRepository.findAllWithSnsByStatusAndKeyword(
              (status == null || status.equals("전체")) ? null : status,
              (keyword == null || keyword.isEmpty()) ? null : keyword,
              pageable
      );
    }

    model.addAttribute("memberPage", memberPage);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", memberPage.getTotalPages());
    model.addAttribute("status", status);
    model.addAttribute("keyword", keyword);

    return "member/adminuser";
  }



  @GetMapping("/adminuserinfo")
  public String getUserInfo(@RequestParam Long idx, Model model) {
    MemberEntity member = memberRepository.findByIdx(idx);
    model.addAttribute("member", member);
    return "member/adminuserinfo";
  }

  @PostMapping("/adminuserinfo")
  public String updateUserInfo(@ModelAttribute MemberEntity updatedMember) {
    MemberEntity member = memberRepository.findByIdx(updatedMember.getIdx());

    if (member != null) {
      member.setName(updatedMember.getName());
      member.setEmail(updatedMember.getEmail());
      member.setMobile(updatedMember.getMobile());
      member.setTelecom(updatedMember.getTelecom());
      member.setStatus(updatedMember.getStatus());
      member.setGrade(updatedMember.getGrade());
      memberRepository.save(member);
    }

    return "redirect:/api/member/adminuserinfo?idx=" + updatedMember.getIdx();
  }

}