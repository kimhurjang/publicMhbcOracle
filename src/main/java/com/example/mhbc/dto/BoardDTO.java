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
    private long idx;
    private Integer re;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;
    private Date closedAt;
    private Date startAt;

    private Long groupIdx;
    private MemberEntity member;

    public BoardEntity toEntity(MemberEntity member, BoardGroupEntity group) {
        return BoardEntity.builder()
                .title(title)
                .content(content)
                .viewCnt(viewCnt)
                .startAt(startAt)
                .closedAt(closedAt)
                .member(member)
                .group(group)
                .build();
    }
    public static BoardDTO fromEntity(BoardEntity entity) {
        BoardDTO dto = new BoardDTO();
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setClosedAt(entity.getClosedAt());
        dto.setStartAt(entity.getStartAt());
        dto.setMember(entity.getMember());  // entity의 member를 DTO의 member에 할당
        return dto;
    }
}