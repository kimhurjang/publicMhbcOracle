package com.example.mhbc.dto;

import lombok.Data;

import java.util.Date;
import java.time.LocalDateTime;

@Data
public class MemberSnsDto {
    private Long idx;
    private String userid;
    private String name;
    private String telecom;
    private String mobile;
    private String email;
    private String nickname;
    private Integer grade;
    private String status;
    private Date createdAt;
    private Date updatedAt;

    private String snsType;
    private String snsId;
    private String snsEmail;
    private String snsName;
    private String snsMobile;
    private LocalDateTime connectedAt;

    public MemberSnsDto(Long idx, String userid, String name, String telecom, String mobile, String email,
                        String nickname, Integer grade, String status, Date createdAt, Date updatedAt,
                        String snsType, String snsId, String snsEmail, String snsName, String snsMobile, LocalDateTime connectedAt) {
        this.idx = idx;
        this.userid = userid;
        this.name = name;
        this.telecom = telecom;
        this.mobile = mobile;
        this.email = email;
        this.nickname = nickname;
        this.grade = grade;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.snsType = snsType;
        this.snsId = snsId;
        this.snsEmail = snsEmail;
        this.snsName = snsName;
        this.snsMobile = snsMobile;
        this.connectedAt = connectedAt;
    }
}
