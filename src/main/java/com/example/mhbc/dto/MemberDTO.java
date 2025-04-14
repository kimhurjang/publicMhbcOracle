package com.example.mhbc.dto;

import com.example.mhbc.entity.MemberEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private String userid;
    private String pwd;
    private String name;
    private String telecom;
    private String mobile;
    private String email;
    private Integer grade;
    private String status;

    public MemberEntity toEntity() {
        return MemberEntity.builder()
            .userid(userid)
            .pwd(pwd)
            .name(name)
            .telecom(telecom)
            .mobile(mobile)
            .email(email)
            .grade(grade)
            .status(status)
            .build();
    }
}
