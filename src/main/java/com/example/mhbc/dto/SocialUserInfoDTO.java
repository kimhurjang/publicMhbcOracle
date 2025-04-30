package com.example.mhbc.dto;

import java.time.LocalDateTime;

public class SocialUserInfoDTO {
    private String userid;
    private String snsName;  // sns_name (nickname 대체)
    private String snsEmail; // sns_email
    private String snsType;  // sns_type 추가
    private  String mobile;
    private LocalDateTime connectedAt; // 연결 시간 추가 (String으로 유지)

    // 새 생성자 (모든 필드를 초기화)
    public SocialUserInfoDTO(String userid, String snsEmail, String snsName, String snsType, LocalDateTime connectedAt) {
        this.userid = userid;
        this.snsEmail = snsEmail;
        this.snsName = snsName;
        this.snsType = snsType;
        this.connectedAt = connectedAt;
    }

    // 기본 생성자
    public SocialUserInfoDTO() {
    }

    // getter들 추가
    public String getUserid() {
        return userid;
    }

    public String getSnsEmail() {
        return snsEmail;
    }

    public String getSnsName() {
        return snsName;
    }

    public String getSnsType() {
        return snsType;
    }

    public String getMobile() {
        return mobile;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }

    // setter들 추가
    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setSnsEmail(String snsEmail) {
        this.snsEmail = snsEmail;
    }

    public void setSnsName(String snsName) {
        this.snsName = snsName;
    }

    public void setSnsType(String snsType) {
        this.snsType = snsType;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setConnectedAt(LocalDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }
}
