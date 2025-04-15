package com.example.mhbc.dto;

import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.CommentsEntity;
import com.example.mhbc.entity.MemberEntity;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsDTO {
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Long boardIdx;
    private Long memberIdx;

    public CommentsEntity toEntity(BoardEntity board, MemberEntity member) {
        return CommentsEntity.builder()
            .content(content)
            .board(board)
            .member(member)
            .build();
    }
}
