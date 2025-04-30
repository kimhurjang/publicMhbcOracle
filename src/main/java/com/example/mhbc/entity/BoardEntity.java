
package com.example.mhbc.entity;

import com.example.mhbc.dto.BoardDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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

    @Builder.Default
    private Integer viewCnt = 0; // 조회수

    @OneToOne(mappedBy = "board")
    private AttachmentEntity attachment;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_AT")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt; // 작성일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_AT")
    private Date updatedAt; // 수정일

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
                .title(title)
                .content(content)
                .viewCnt(viewCnt)
                .createdAt(createdAt)
                .groupIdx(group != null ? group.getGroupIdx() : null)
                .memberIdx(member != null ? member.getIdx() : null)
                .build();
    }


}
