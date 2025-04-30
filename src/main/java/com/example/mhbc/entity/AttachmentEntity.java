package com.example.mhbc.entity;

import com.example.mhbc.dto.AttachmentDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "ATTACHMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx; // 첨부파일 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_IDX")
    private BoardEntity board; // 게시판

    private String fileName; // 파일 이름
    private String fileType; // 파일 형식
    private String filePath; // 파일 경로
    private Integer fileSize; // 파일 크기

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_AT")
    private Date createdAt; // 작성일

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public AttachmentDTO toDTO() {
        return AttachmentDTO.builder()
                .fileName(fileName)
                .fileType(fileType)
                .filePath(filePath)
                .fileSize(fileSize)
                .createdAt(createdAt)
                .boardIdx(board != null ? board.getIdx() : null)
                .build();
    }

}