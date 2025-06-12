package com.example.mhbc.dto;

import com.example.mhbc.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
  private Long idx;
  private String userid;
  private String pwd;
  private String name;
  private String telecom;
  private String mobile;
  private String email;
  private String nickname;
  private Integer grade;
  private String status;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date createdAt;  // 추가

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date updatedAt;  // 추가

  public MemberEntity toEntity() {
    return MemberEntity.builder()
            .idx(this.idx)
            .userid(userid)
            .pwd(pwd)
            .name(name)
            .telecom(telecom)
            .mobile(mobile)
            .email(email)
            .nickname(nickname)
            .grade(grade)
            .status(status)
            // createdAt, updatedAt은 엔티티에서 관리하므로 builder에 포함하지 않음
            .build();
  }

  // Entity → DTO 변환용 정적 메서드
  public static MemberDTO fromEntity(MemberEntity entity) {
    return MemberDTO.builder()
            .idx(entity.getIdx())
            .userid(entity.getUserid())
            .pwd(entity.getPwd())
            .name(entity.getName())
            .telecom(entity.getTelecom())
            .mobile(entity.getMobile())
            .email(entity.getEmail())
            .nickname(entity.getNickname())
            .grade(entity.getGrade())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())    // Date → LocalDateTime 변환
            .updatedAt(entity.getUpdatedAt())
            .build();
  }

  // Date → LocalDateTime 변환 헬퍼 메서드
  public static LocalDateTime convertToLocalDateTime(Date date) {
    if (date == null) return null;
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }
}
