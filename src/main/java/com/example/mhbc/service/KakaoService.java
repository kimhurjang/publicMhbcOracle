package com.example.mhbc.service;


import com.example.mhbc.dto.SocialUserInfoDTO;
import com.example.mhbc.entity.SnsEntity;
import com.example.mhbc.repository.SnsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.mhbc.dto.SocialUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.LocalDateTime;

@Service
public class KakaoService {

    private final SnsRepository snsRepository;

    public KakaoService(SnsRepository snsRepository) {
        this.snsRepository = snsRepository;
    }


    public String getKakaoAccessToken(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://kauth.kakao.com")

                .defaultHeader("Content-Type", "application/x-www-form-urlencoded")

                //.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)

                .build();

        String response = webClient.post()
                .uri("/oauth/token")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")

                        .with("client_id", "c9bb56960e98eceddc4418dc3243c916")
                        .with("redirect_uri", "http://localhost:8090/member/sociallogin")
                        .with("code", code)
                )

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
                .uri("https://kapi.kakao.com/v2/user/me") // 프로필 이미지와 이메일 제외

                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("👤 사용자 정보 응답: " + response);


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            // 카카오 ID 가져오기 (SNS ID)
            String snsId = "k" + jsonNode.get("id").asText();
            String snsName = jsonNode.path("properties").path("nickname").asText("카카오사용자");  // 닉네임

            String snsEmail = jsonNode.path("kakao_account").path("email").asText();

            // SNS 정보를 담을 DTO 객체 반환
            SocialUserInfoDTO userInfo = new SocialUserInfoDTO();
            userInfo.setUserid(snsId);
            userInfo.setSnsName(snsName);
            userInfo.setSnsEmail(snsEmail);
            userInfo.setSnsType("KAKAO");
            userInfo.setConnectedAt(LocalDateTime.now()); // LocalDateTime으로 변환하여 저장

            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void saveUserInfoToSns(String accessToken) {
        SocialUserInfoDTO userInfo = getUserInfoFromKakao(accessToken);

        if (userInfo != null) {
            SnsEntity snsEntity = SnsEntity.builder()
                    .snsType(userInfo.getSnsType())
                    .snsId(userInfo.getUserid())
                    .snsEmail(userInfo.getSnsEmail())
                    .snsName(userInfo.getSnsName())
                    .connectedAt(userInfo.getConnectedAt()) // LocalDateTime 그대로 사용
                    .build();

            snsRepository.save(snsEntity);
            System.out.println("✅ SNS 테이블에 저장 완료: " + userInfo.getSnsName());
        } else {
            System.out.println("❌ SNS 테이블에 저장 실패");
        }
    }

}
