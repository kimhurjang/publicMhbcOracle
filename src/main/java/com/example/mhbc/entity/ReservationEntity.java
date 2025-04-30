package com.example.mhbc.entity;

import com.example.mhbc.dto.ReservationDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Table(name = "RESERVATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx; // 예약번호

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MEMBER_IDX")
  private MemberEntity member; // 회원

  private String name; // 예약자 성함
  private String eventType; // 행사 종류

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "HALL_IDX")
  private HallEntity hall; // 홀

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "EVENT_DATE")
  private Date eventDate; // 행사 예정일

  private Integer guestCnt; // 행사 인원수
  private String mealType; // 식사 종류
  private String flower; // 꽃장식
  private String contactTime; // 연락 가능한 시간
  private String mobile; // 연락처

  private String status; // 예약 상태
  private Integer totalAmount; // 총금액

  @Column(name = "user_note")
  private String userNote; // 사용자 메모
  @Column(name = "admin_note")
  private String adminNote; // 관리자 메모
  @Column(name = "last_modified_by")
  private String lastModifiedBy; // 마지막 수정자 ID

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

  public ReservationDTO toDTO() {

    // Date → LocalDateTime → String
    String formattedEventDate = "";
    String formattedTimeSelect = "";
    if (eventDate != null) {
      LocalDateTime ldt = eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

      formattedEventDate = ldt.format(DateTimeFormatter.ofPattern("yy-MM-dd")); // ← 25-04-24
      formattedTimeSelect = String.format("%02d시", ldt.getHour());             // ← 10 → "10시"
    }

    // "2025-04-24 / 오후" → "25-04-24 / 오후"
    String formattedContactTime = contactTime;
    if (contactTime != null && contactTime.length() >= 10) {
      // "2025-04-24 / 오후"
      // index 2~10 → "25-04-24"
      // index 10~ → " / 오후"
      String datePart = contactTime.substring(2, 10);   // "25-04-24"
      String ampmPart = contactTime.substring(10);      // " / 오후"
      formattedContactTime = datePart + ampmPart;
    }

    return ReservationDTO.builder()
      .idx(idx)
      .name(name)
      .eventType(eventType)
      .eventDate(formattedEventDate)       // ← String으로 변환해서 저장
      .eventTimeSelect(formattedTimeSelect) // ← 시간도 따로
      .guestCnt(guestCnt)
      .mealType(mealType)
      .flower(flower)
      .contactTime(formattedContactTime)
      .mobile(mobile)
      .status(status)
      .totalAmount(totalAmount)
      .createdAt(createdAt)
      .updatedAt(updatedAt)
      .memberIdx(member != null ? member.getIdx() : null)
      .hallIdx(hall != null ? hall.getIdx() : null)
      .hallName(hall != null ? hall.getName() : null)
      .userNote(userNote)
      .adminNote(adminNote)
      .lastModifiedBy(lastModifiedBy)

      .build();
  }
}
