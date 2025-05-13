package com.example.mhbc.dto;

import com.example.mhbc.entity.BoardGroupEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardGroupDTO {
    private Long boardType;
    private String name;
    private Integer orderNo;
    private String role;
    private Long groupIdx;

    public BoardGroupEntity toEntity() {
        return BoardGroupEntity.builder()
                .boardType(boardType)
                .name(name)
                .groupIdx(groupIdx)
                .orderNo(orderNo)
                .role(role)
                .build();
    }
}