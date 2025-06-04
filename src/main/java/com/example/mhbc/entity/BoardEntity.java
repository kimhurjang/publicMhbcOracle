
package com.example.mhbc.entity;

import com.example.mhbc.dto.BoardDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "BOARD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx; // 게시판 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_IDX")
    private BoardGroupEntity group; // 게시판 그룹

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_IDX")
    @JsonIgnore
    private MemberEntity member; // 작성자

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title; // 타이틀

    @NotBlank(message = "본문은 필수 입력 항목입니다.")
    private String content; // 내용


    private Integer re; // 질문 답변 구분용

    @Column(name = "request")
    private Integer request;//1ㄷ1 답변 유무 0==전 / 1==후

    @Column(name = "request_title")
    private String requestTitle;

    @Column(name = "request_content")
    private String requestContent;

    @Builder.Default
    private Integer viewCnt = 0; // 조회수

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AttachmentEntity attachment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_AT")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startAt; // 이벤트 시작일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CLOSED_AT")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date closedAt; // 이벤트 종료일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_AT")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt; // 작성일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_AT")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt; // 수정일

    @Column(name = "answer_title")
    private String answerTitle;//자주 묻는 질문 답변용

    @Column(name = "answer_content")
    private String answerContent;//자주 묻는 질문 답변용

    @Column(name = "category")
    private String category;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public BoardDTO toDTO() {
        return BoardDTO.builder()
                .idx(idx)
                .title(title)
                .content(content)
                .re(re)
                .viewCnt(viewCnt)
                .createdAt(createdAt)
                .closedAt(closedAt)
                .startAt(startAt)
                .answerTitle(answerTitle)
                .answerContent(answerContent)
                .groupIdx(group != null ? group.getGroupIdx() : null)
                .member(member)
                .category(category)
                .build();
    }



}
