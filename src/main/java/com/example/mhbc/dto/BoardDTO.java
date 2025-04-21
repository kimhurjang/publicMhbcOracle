package com.example.mhbc.dto;

import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.BoardGroupEntity;
import com.example.mhbc.entity.MemberEntity;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
    private Integer re;
    private String title;
    private String content;
    private Integer viewCnt;
    private Date createdAt;
    private Long groupIdx;
    private Long memberIdx;

    public BoardEntity toEntity(MemberEntity member, BoardGroupEntity group) {
        return BoardEntity.builder()
            .re(re)
            .title(title)
            .content(content)
            .viewCnt(viewCnt)
            .member(member)
            .group(group)
            .build();
    }
}
