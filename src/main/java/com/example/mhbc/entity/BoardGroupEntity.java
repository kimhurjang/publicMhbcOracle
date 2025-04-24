package com.example.mhbc.entity;

import com.example.mhbc.dto.BoardGroupDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BOARD_GROUP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupIdx; // 그룹 번호
    private Integer boardType; // 게시판 타입
    private String name; // 게시판 이름
    private Integer orderNo; // 순서
    private String role; // 권한

    public BoardGroupDTO toDTO() {
        return BoardGroupDTO.builder()
            .boardType(boardType)
            .name(name)
            .orderNo(orderNo)
            .role(role)
            .build();
    }
}
