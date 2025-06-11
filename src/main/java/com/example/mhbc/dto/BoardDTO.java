package com.example.mhbc.dto;

import com.example.mhbc.entity.AttachmentEntity;
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
    private Long idx;
    private Integer re;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;
    private Date closedAt;
    private Date startAt;
    private Integer request;
    private String requestTitle;
    private String requestContent;
    private String answerTitle;
    private String answerContent;
    private String category;

    private boolean Started;
    private boolean Closed;

    private Long groupIdx;
    private MemberEntity member;
    private AttachmentEntity attachment;

    public BoardEntity toEntity(MemberEntity member, BoardGroupEntity group) {
        return BoardEntity.builder()
                .title(title)
                .content(content)
                .viewCnt(viewCnt)
                .startAt(startAt)
                .closedAt(closedAt)
                .member(member)
                .group(group)
                .re(re)
                .request(request)
                .requestTitle(requestTitle)
                .requestContent(requestContent)
                .answerTitle(answerTitle)
                .answerContent(answerContent)
                .category(category)
                .attachment(attachment)
                .build();
    }
    public static BoardDTO fromEntity(BoardEntity entity) {
        BoardDTO dto = new BoardDTO();
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setClosedAt(entity.getClosedAt());
        dto.setRe(entity.getRe());
        dto.setStartAt(entity.getStartAt());
        dto.setMember(entity.getMember());  // entity의 member를 DTO의 member에 할당
        dto.setRequest(entity.getRequest());
        dto.setRequestTitle(entity.getRequestTitle());
        dto.setRequest(entity.getRequest());
        dto.setRequestContent(entity.getRequestContent());
        dto.setAnswerTitle(entity.getAnswerTitle());
        dto.setAnswerContent(entity.getAnswerContent());
        dto.setCategory(entity.getCategory());
        dto.setAttachment(entity.getAttachment());
        // 날짜 기반 상태 계산
        Date now = new Date();
        dto.setStarted(entity.getStartAt() != null && now.after(entity.getStartAt()));
        dto.setClosed(entity.getClosedAt() != null && now.after(entity.getClosedAt()));

        return dto;
    }
}