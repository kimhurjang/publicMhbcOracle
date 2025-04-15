package com.example.mhbc.entity;

import com.example.mhbc.dto.SnsDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "SNS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx; // SNS 연동 고유 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_IDX")
    private MemberEntity member; // 회원

    private String snsType; // 플랫폼 유형
    private String snsId; // SNS 고유 사용자 ID
    private String snsEmail; // SNS 계정 이메일
    private String snsName; // SNS 닉네임 또는 이름
    private String snsProfileImg; // SNS 프로필 이미지 URL

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CONNECTED_AT")
    private Date connectedAt; // SNS 연동 시점

    public SnsDTO toDTO() {
        return SnsDTO.builder()
            .snsType(snsType)
            .snsId(snsId)
            .snsEmail(snsEmail)
            .snsName(snsName)
            .snsProfileImg(snsProfileImg)
            .connectedAt(connectedAt)
            .memberIdx(member != null ? member.getIdx() : null)
            .build();
    }
}
