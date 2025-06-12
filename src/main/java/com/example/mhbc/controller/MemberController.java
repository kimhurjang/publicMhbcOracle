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
import com.example.mhbc.service.MemberService;
import com.example.mhbc.service.UserDetailServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.validation.Valid;
import java.lang.reflect.Member;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/member")
public class MemberController {

    private final MemberRepository memberRepository;
    private final KakaoService kakaoService;
    private final SnsRepository snsRepository;
    private final UserDetailServiceImpl userDetailsService;

    @Autowired
    private LoginService loginService;
    @Autowired
    private MemberService memberService;
    @PostMapping("/withdraw")
    @ResponseBody
    public Map<String, Object> withdrawMember(Principal principal, HttpSession session) {
        if (principal == null) {
            return Map.of("success", false, "message", "로그인이 필요합니다.");
        }

        String userid = principal.getName();
        boolean result = memberService.withdrawMember(userid);

        if (result) {
            session.invalidate();
            return Map.of("success", true, "message", "탈퇴되었습니다. 감사합니다.");
        } else {
            return Map.of("success", false, "message", "탈퇴에 실패했습니다.");
        }
    }





    @GetMapping("/mypage")
    public String mypage(Principal principal, Model model, @ModelAttribute("successMessage") String successMessage) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<MemberEntity> optionalMember = memberRepository.findByUserid(principal.getName());
        if (optionalMember.isEmpty()) {
            return "error/noMember";
        }

        model.addAttribute("member", optionalMember.get());
        model.addAttribute("successMessage", successMessage);  // 플래시 속성 전달
        return "member/mypage";  // templates/member/mypage.html
    }


    // 내 정보 수정 처리 (POST)
    @PostMapping("/mypage")
    public String update(@ModelAttribute MemberEntity formMember, RedirectAttributes redirectAttributes) {
        MemberEntity existingMember = memberRepository.findById(formMember.getIdx())
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다"));

        existingMember.setName(formMember.getName());
        existingMember.setEmail(formMember.getEmail());
        existingMember.setMobile(formMember.getMobile());
        memberRepository.save(existingMember);

        // 플래시 속성에 메시지 담기
        redirectAttributes.addFlashAttribute("successMessage", "저장되었습니다");

        return "redirect:/api/member/mypage";  // 저장 후 다시 마이페이지로 이동
    }



    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "errorCode", required = false) String errorCode,
                            @RequestParam(value = "userid", required = false) String userid,
                            Model model,
                            HttpSession session) {
        if (session.getAttribute("loginMember") != null || session.getAttribute("loginSnsUser") != null) {
            return "redirect:/";
        }
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("userid", userid);  // 아이디 유지용
        return "member/login";
    }

    @PostMapping("/loginProc")
    public String loginPost(@RequestParam("userid") String userid,
                            @RequestParam("pwd") String pwd,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {

        MemberEntity member = loginService.login(userid, pwd);

        if (member == null) {
            redirectAttributes.addAttribute("errorCode", "BAD_CREDENTIALS");
            redirectAttributes.addAttribute("userid", userid);
            return "redirect:/api/member/login";
        }

        String status = (member.getStatus() != null) ? member.getStatus().trim() : "";

        switch (status) {
            case "정상":
                session.setAttribute("loginMember", member);
                return "redirect:/";
            case "탈퇴":
                redirectAttributes.addAttribute("errorCode", "WITHDRAW");
                redirectAttributes.addAttribute("userid", userid);
                return "redirect:/api/member/login";
            case "정지":
                redirectAttributes.addAttribute("errorCode", "STOP");
                redirectAttributes.addAttribute("userid", userid);
                return "redirect:/api/member/login";
            default:
                redirectAttributes.addAttribute("errorCode", "UNKNOWN");
                redirectAttributes.addAttribute("userid", userid);
                return "redirect:/api/member/login";
        }
    }

    @PostMapping("/reactivate")
    @ResponseBody
    public Map<String, Object> reactivate(@RequestBody Map<String, String> request) {
        String userid = request.get("userid");
        System.out.println("[reactivate] 요청 userid: " + userid);

        boolean success = memberService.reactivate(userid);

        System.out.println("[reactivate] 상태 변경 성공 여부: " + success);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("userid", userid);  // 이거 추가
        return response;
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

            // sns에 연동된 member 엔티티도 업데이트
            MemberEntity linkedMember = snsSession.getMember();
            if (linkedMember != null) {
                linkedMember.setMobile(mobile);
                memberRepository.save(linkedMember);
                // 세션에 member 정보도 같이 업데이트
                session.setAttribute("loginMember", linkedMember);
            }

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
    public String socialLogin(@RequestParam("code") String code, HttpSession session) {
        String accessToken = kakaoService.getKakaoAccessToken(code);
        session.setAttribute("kakaoAccessToken", accessToken);

        SocialUserInfoDTO userInfo = kakaoService.getUserInfoFromKakao(accessToken);
        if (userInfo == null || userInfo.getSnsEmail() == null) {
            return "redirect:/api/member/login";
        }

        Optional<SnsEntity> snsUserOpt = snsRepository.findBySnsId(userInfo.getUserid());
        SnsEntity snsUser;
        MemberEntity member;

        if (snsUserOpt.isPresent()) {
            snsUser = snsUserOpt.get();
            member = snsUser.getMember();

            if ("탈퇴".equals(member.getStatus())) {
                member.setStatus("정상");
                member.setUpdatedAt(new Date());
                memberRepository.save(member);
            }
        } else {
            Optional<MemberEntity> memberOpt = memberRepository.findByUserid(userInfo.getUserid());

            if (memberOpt.isPresent()) {
                member = memberOpt.get();

                if ("탈퇴".equals(member.getStatus())) {
                    member.setStatus("정상");
                    member.setUpdatedAt(new Date());
                    memberRepository.save(member);
                }
            } else {
                member = new MemberEntity();
                member.setUserid(userInfo.getUserid());
                member.setName(userInfo.getSnsName());
                member.setEmail(userInfo.getSnsEmail());
                member.setStatus("정상");
                member.setGrade(1);
                member.setCreatedAt(new Date());
                member.setUpdatedAt(new Date());
                member = memberRepository.save(member);
            }

            snsUser = new SnsEntity();
            snsUser.setSnsType("KAKAO");
            snsUser.setSnsId(userInfo.getUserid());
            snsUser.setSnsEmail(userInfo.getSnsEmail());
            snsUser.setSnsName(userInfo.getSnsName());
            snsUser.setConnectedAt(LocalDateTime.now());
            snsUser.setMember(member);

            snsUser = snsRepository.save(snsUser);
        }

        // 로그인 차단 조건 체크
        if (member == null || "정지".equals(member.getStatus())) {
            String errorCode = "STOP";
            String userid = (member != null) ? member.getUserid() : "";
            return "redirect:/api/member/login?errorCode=" + errorCode + "&userid=" + userid;
        }

        session.setAttribute("loginSnsUser", snsUser);
        session.setAttribute("loginMember", member);

        UserDetails userDetails = userDetailsService.loadUserByUsername(member.getUserid());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        if (snsUser.getSnsMobile() == null || snsUser.getSnsMobile().isBlank()) {
            return "redirect:/api/member/mobile";
        }

        return "redirect:/";
    }


    @GetMapping("/join")
    public String join() {
        return "member/join";  // 뷰 이름, 예: src/main/resources/templates/member/join.html
    }

    // 아이디 중복 체크
    @GetMapping("/idcheck")
    @ResponseBody
    public String checkUserId(@RequestParam("userid") String userid) {
        boolean exists = memberRepository.existsByUserid(userid);
        return exists ? "Y" : "N";
    }

    // 전화번호 중복 체크
    @GetMapping("/mobilecheck")
    @ResponseBody
    public String checkMobile(@RequestParam("mobile") String mobile) {
        boolean exists = memberRepository.existsByMobile(mobile);
        return exists ? "Y" : "N";
    }

    // 회원가입 처리
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

        String mobile = (mobile1 != null && !mobile1.isEmpty() &&
                mobile2 != null && !mobile2.isEmpty() &&
                mobile3 != null && !mobile3.isEmpty())
                ? mobile1 + "-" + mobile2 + "-" + mobile3 : null;

        // 중복 아이디가 있으면 회원가입 페이지로 다시 리다이렉트
        if (memberRepository.existsByUserid(userid)) {
            return "redirect:/api/member/join";
        }

        // MemberDTO 생성 및 저장
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

        memberRepository.save(memberDTO.toEntity());

        // 가입 후 로그인 페이지로 리다이렉트
        return "redirect:/api/member/login";
    }
    @GetMapping("/api/member/nicknamecheck")
    @ResponseBody
    public String checkNickname(@RequestParam String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        return exists ? "Y" : "N";
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
            redirectAttributes.addFlashAttribute("pwError", "아이디 또는 이름이 일치하지 않습니다. 다시 확인해 주세요.");
            return "redirect:/api/member/findidpw#tab2";
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

        int pageSize = 8;
        // 최신 가입순으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MemberSnsDto> memberPage;

        if ((status == null || status.equals("전체")) && (keyword == null || keyword.isEmpty())) {
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



    @PostMapping("/adminuser/delete-multiple")
    public String deleteMultipleMembers(@RequestParam("idxList") List<Long> idxList) {
        idxList.forEach(memberRepository::deleteById);
        return "redirect:/api/member/adminuser";
    }

    @GetMapping("/adminuserinfo")
    public String getUserInfo(@RequestParam("idx") Long idx, Model model, HttpServletRequest request) {
        MemberEntity member = memberRepository.findByIdx(idx);
        if (member == null) {
            return "redirect:/api/member/adminuser";
        }
        model.addAttribute("member", member.toDTO());

        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if (flashMap != null && flashMap.containsKey("message")) {
            model.addAttribute("message", flashMap.get("message"));
        }

        return "member/adminuserinfo";
    }

    @PostMapping("/adminuserinfo")
    public String updateUserInfo(@Valid @ModelAttribute("member") MemberDTO memberDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "member/adminuserinfo";
        }

        MemberEntity member = memberRepository.findByIdx(memberDTO.getIdx());
        if (member != null) {
            member.setName(memberDTO.getName());
            member.setEmail(memberDTO.getEmail());
            member.setMobile(memberDTO.getMobile());
            member.setTelecom(memberDTO.getTelecom());
            member.setStatus(memberDTO.getStatus());
            member.setGrade(memberDTO.getGrade());
            memberRepository.save(member);
        }

        redirectAttributes.addFlashAttribute("message", "저장되었습니다.");
        return "redirect:/api/member/adminuserinfo?idx=" + memberDTO.getIdx();
    }



}