package com.example.mhbc.service;

import com.example.mhbc.dto.SocialUserInfoDTO;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.SnsEntity;
import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.repository.SnsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class KakaoService {

    private final SnsRepository snsRepository;
    private final MemberRepository memberRepository;

    public KakaoService(SnsRepository snsRepository, MemberRepository memberRepository) {
        this.snsRepository = snsRepository;
        this.memberRepository = memberRepository;
    }

    public String getKakaoAccessToken(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://kauth.kakao.com")
                .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        String response = webClient.post()
                .uri("/oauth/token")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", "3a729b684852129622871e6b959a97e6")
                        .with("redirect_uri", "http://localhost:8090/api/member/kakao")
                        .with("code", code))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("📦 카카오 토큰 응답: " + response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SocialUserInfoDTO getUserInfoFromKakao(String accessToken) {
        String response = WebClient.create()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("👤 사용자 정보 응답: " + response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            String snsId = "k" + jsonNode.get("id").asText();
            String snsName = jsonNode.path("properties").path("nickname").asText("카카오사용자");
            String snsEmail = jsonNode.path("kakao_account").path("email").asText();

            SocialUserInfoDTO userInfo = new SocialUserInfoDTO();
            userInfo.setUserid(snsId);
            userInfo.setSnsName(snsName);
            userInfo.setSnsEmail(snsEmail);
            userInfo.setSnsType("KAKAO");
            userInfo.setConnectedAt(LocalDateTime.now());

            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveUserInfoToSns(String accessToken) {
        SocialUserInfoDTO userInfo = getUserInfoFromKakao(accessToken);
        if (userInfo == null) {
            System.out.println("❌ 사용자 정보 불러오기 실패");
            return;
        }

        Optional<MemberEntity> memberOpt = memberRepository.findByUserid(userInfo.getUserid());
        MemberEntity member;

        if (memberOpt.isPresent()) {
            member = memberOpt.get();

            if ("탈퇴".equals(member.getStatus())) {
                member.setStatus("정상");
                member.setUpdatedAt(new Date());
                memberRepository.save(member);
            }
        } else {
            member = MemberEntity.builder()
                    .userid(userInfo.getUserid())
                    .name(userInfo.getSnsName())
                    .email(userInfo.getSnsEmail())
                    .status("정상")
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();
            member = memberRepository.save(member);
        }

        Optional<SnsEntity> snsOpt = snsRepository.findBySnsId(userInfo.getUserid());
        if (!snsOpt.isPresent()) {
            SnsEntity snsEntity = SnsEntity.builder()
                    .snsType(userInfo.getSnsType())
                    .snsId(userInfo.getUserid())
                    .snsEmail(userInfo.getSnsEmail())
                    .snsName(userInfo.getSnsName())
                    .connectedAt(userInfo.getConnectedAt())
                    .member(member)
                    .build();
            snsRepository.save(snsEntity);
            System.out.println("✅ SNS 정보 저장 완료");
        }

        // 로그인 처리
        UserDetails userDetails = User.withUsername(member.getUserid())
                .password("")
                .authorities("ROLE_USER")
                .build();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        System.out.println("✅ Spring Security 로그인 처리 완료");
    }
}
