package com.example.mhbc.dto;

import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.BoardGroupEntity;
import com.example.mhbc.entity.MemberEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
    private String title;
    private String content;
    private Integer viewCnt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    private Long groupIdx;
    private Long memberIdx;

    public BoardEntity toEntity(MemberEntity member, BoardGroupEntity group) {
        return BoardEntity.builder()
                .title(title)
                .content(content)
                .viewCnt(viewCnt)
                .member(member)
                .group(group)
                .build();
    }
}