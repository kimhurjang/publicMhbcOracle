package com.example.mhbc.dto;

import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.SnsEntity;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnsDTO {
    private String snsType;
    private String snsId;
    private String snsEmail;
    private String snsName;
    private String snsProfileImg;
    private Date connectedAt;
    private Long memberIdx;

    public SnsEntity toEntity(MemberEntity member) {
        return SnsEntity.builder()
            .snsType(snsType)
            .snsId(snsId)
            .snsEmail(snsEmail)
            .snsName(snsName)
            .snsProfileImg(snsProfileImg)
            .connectedAt(connectedAt)
            .member(member)
            .build();
    }
}
