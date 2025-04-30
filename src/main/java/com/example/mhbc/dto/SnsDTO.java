package com.example.mhbc.dto;

import com.example.mhbc.entity.SnsEntity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnsDTO {
    private String snsType;
    private String snsId;
    private String snsEmail;
    private String snsName;
    private LocalDateTime connectedAt;

    public SnsEntity toEntity() {
        return SnsEntity.builder()
                .snsType(snsType)
                .snsId(snsId)
                .snsEmail(snsEmail)
                .snsName(snsName)
                .connectedAt(connectedAt)
                .build();
    }
}
