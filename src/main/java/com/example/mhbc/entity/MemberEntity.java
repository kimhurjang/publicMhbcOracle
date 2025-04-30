package com.example.mhbc.entity;

import com.example.mhbc.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "MEMBER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx; // 회원번호

    @Column(unique = true)
    private String userid; // 회원아이디

    private String pwd; // 패스워드
    private String name; // 회원이름
    private String telecom; // 통신사
    private String mobile; // 휴대폰번호
    private String email; // 이메일
    private String nickname; //닉네임

    @Builder.Default
    @Column(name = "GRADE")
    private Integer grade = 0; // 등급

    @Builder.Default
    @Column(name = "STATUS")
    private String status = "ACTIVE"; // 회원상태

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_LOGIN_AT")
    private Date lastLoginAt; // 마지막 로그인 시간

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_AT")
    private Date createdAt; // 가입일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_AT")
    private Date updatedAt; // 수정일

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public MemberDTO toDTO() {
        return MemberDTO.builder()
                .userid(userid)
                .name(name)
                .email(email)
                .mobile(mobile)
                .grade(grade)
                .status(status)
                .build();
    }


}

