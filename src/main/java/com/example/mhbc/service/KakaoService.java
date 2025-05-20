package com.example.mhbc.service;

import com.example.mhbc.dto.SocialUserInfoDTO;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.SnsEntity;
import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.repository.SnsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        if (userInfo != null) {
            // 기존 회원 조회 또는 신규 회원 생성
            Optional<MemberEntity> optionalMember = memberRepository.findByUserid(userInfo.getUserid());

            MemberEntity member = optionalMember.orElseGet(() -> {
                MemberEntity newMember = MemberEntity.builder()
                        .userid(userInfo.getUserid())
                        .name(userInfo.getSnsName())
                        .email(userInfo.getSnsEmail())
                        .status("ACTIVE")
                        .createdAt(new Date())
                        .updatedAt(new Date())
                        .build();
                MemberEntity saved = memberRepository.save(newMember);
                System.out.println("신규 회원 저장, idx: " + saved.getIdx());
                return saved;
            });

            boolean snsExists = snsRepository.existsBySnsIdAndMember(userInfo.getUserid(), member);

            if (!snsExists) {
                SnsEntity snsEntity = SnsEntity.builder()
                        .snsType(userInfo.getSnsType())
                        .snsId(userInfo.getUserid())
                        .snsEmail(userInfo.getSnsEmail())
                        .snsName(userInfo.getSnsName())
                        .connectedAt(userInfo.getConnectedAt())
                        .member(member)  // 여기 연관관계 설정
                        .build();

                snsRepository.save(snsEntity);
                System.out.println("✅ SNS 저장 완료: " + userInfo.getSnsName());
            } else {
                System.out.println("이미 SNS 정보가 존재합니다: " + userInfo.getSnsName());
            }
        } else {
            System.out.println("❌ SNS 테이블 저장 실패");
        }
    }
}
