package com.example.mhbc.dto;

import com.example.mhbc.entity.BoardGroupEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardGroupDTO {
    private Long groupIdx;
    private Integer boardType;
    private String name;
    private Integer orderNo;
    private String role;

    public BoardGroupEntity toEntity() {
        return BoardGroupEntity.builder()
            .groupIdx(groupIdx)
            .boardType(boardType)
            .name(name)
            .orderNo(orderNo)
            .role(role)
            .build();
    }
}
