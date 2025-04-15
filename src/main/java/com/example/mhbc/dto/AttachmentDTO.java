package com.example.mhbc.dto;

import com.example.mhbc.entity.AttachmentEntity;
import com.example.mhbc.entity.BoardEntity;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentDTO {
    private String fileName;
    private String fileType;
    private String filePath;
    private Integer fileSize;
    private Date createdAt;
    private Long boardIdx;

    public AttachmentEntity toEntity(BoardEntity board) {
        return AttachmentEntity.builder()
            .fileName(fileName)
            .fileType(fileType)
            .filePath(filePath)
            .fileSize(fileSize)
            .board(board)
            .build();
    }
}
