package com.example.mhbc.entity;

import com.example.mhbc.dto.CommentsDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "COMMENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx; // 댓글 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_IDX")
    private BoardEntity board; // 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_IDX")
    private MemberEntity member; // 작성자

    private String content; // 내용

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_AT")
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

    public CommentsDTO toDTO() {
        return CommentsDTO.builder()
            .content(content)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .boardIdx(board != null ? board.getIdx() : null)
            .memberIdx(member != null ? member.getIdx() : null)
            .build();
    }
}
