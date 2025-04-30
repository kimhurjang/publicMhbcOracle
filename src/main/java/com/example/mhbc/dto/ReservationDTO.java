package com.example.mhbc.dto;

import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.ReservationEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

  private Long idx; // 예약번호
  private String name; // 예약자 성함
  private String eventType; // 행사 종류

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  // 행사 예정일
  private String eventDate;       // 날짜 (예: "2025-05-31")
  private String eventTimeSelect; // 시간 (예: "14")

  private Integer guestCnt; // 행사 인원수
  private String mealType; // 식사 종류
  private String flower; // 꽃장식
  private String contactTime; // 연락 가능한 시간
  private String mobile; // 연락처
  private String status; // 예약 상태
  private Integer totalAmount; // 총금액
  private Date createdAt; // 작성일
  private Date updatedAt; // 수정일
  private Long memberIdx; // 회원 FK
  private Long hallIdx; // 홀 FK
  private String hallName; // 홀 이름

  private String userNote; // 사용자 비고
  private String adminNote; // 관리자 비고
  private String lastModifiedBy; // 마지막 수정자 ID

  // DTO → Entity (수정용까지 반영)
  public ReservationEntity toEntity(MemberEntity member, HallEntity hall) {

    Date parsedDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

    try {
      // 미정 체크로 값이 비었을 경우 null 반환
      if (eventDate == null || eventDate.isEmpty() || eventTimeSelect == null || eventTimeSelect.isEmpty()) {
        parsedDate = null; // 행사일이 미정 상태
      } else {
        LocalDateTime dateTime = LocalDateTime.parse(eventDate + " " + eventTimeSelect, formatter);
        parsedDate = Timestamp.valueOf(dateTime);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("날짜 형식이 잘못되었습니다: eventDate=" + eventDate + ", eventTimeSelect=" + eventTimeSelect);
    }

    ReservationEntity.ReservationEntityBuilder builder = ReservationEntity.builder()
      .name(name)
      .eventType(eventType)
      .eventDate(parsedDate)
      .guestCnt(guestCnt)
      .mealType(mealType)
      .flower(flower)
      .contactTime(contactTime)
      .mobile(mobile)
      .status(status)
      .totalAmount(totalAmount)
      .createdAt(createdAt)
      .updatedAt(updatedAt)
      //.member(member)
      //.hall(hall)
      .userNote(userNote)
      .adminNote(adminNote)
      .lastModifiedBy(lastModifiedBy);

    // 수정 시에만 idx 포함
    if (idx != null) {
      builder.idx(idx);
    }

    return builder.build();
  }
}
