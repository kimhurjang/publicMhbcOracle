package com.example.mhbc.entity;

import com.example.mhbc.dto.ReservationDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 예약 테이블과 매핑되는 Entity 클래스
 * DB 테이블명: RESERVATION
 */
@Entity
@Table(name = "RESERVATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx; // 예약 번호 (기본키)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MEMBER_IDX")
  private MemberEntity member; // 회원 정보 (외래키)

  private String name; // 예약자 성함
  private String eventType; // 행사 종류 (예: 예식, 돌잔치)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "HALL_IDX")
  private HallEntity hall; // 홀 정보 (외래키)

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "EVENT_DATE")
  private Date eventDate; // 행사 예정일 (날짜 + 시간 포함)

  private Integer guestCnt; // 행사 인원수
  private String mealType; // 식사 종류 (뷔페, 도시락 등)
  private String flower; // 꽃장식 종류
  private String contactTime; // 연락 가능 시간
  private String mobile; // 연락처

  private String status; // 예약 상태 (예: 상담대기, 예약확정 등)
  private Integer totalAmount; // 총 금액

  @Column(name = "user_note")
  private String userNote; // 사용자 메모
  @Column(name = "admin_note")
  private String adminNote; // 관리자 메모
  @Column(name = "last_modified_by")
  private String lastModifiedBy; // 마지막 수정자 ID

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "CREATE_AT")
  private Date createdAt; // 생성일

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "UPDATE_AT")
  private Date updatedAt; // 수정일

  // INSERT 전에 호출되어 생성일과 수정일을 현재 시각으로 설정
  @PrePersist
  protected void onCreate() {
    Date now = new Date();
    createdAt = now;
    updatedAt = now;
  }

  // UPDATE 전에 호출되어 수정일을 현재 시각으로 갱신
  @PreUpdate
  protected void onUpdate() {
    updatedAt = new Date();
  }

  /**
   * Entity → DTO 변환 메서드
   */
  public ReservationDTO toDTO() {
    // eventDate → 시간 문자열로 포맷
    String formattedTimeSelect = "";
    if (eventDate != null) {
      LocalDateTime ldt = eventDate.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
      formattedTimeSelect = String.format("%02d", ldt.getHour()); // 예: "14"
    }

    // 연락 가능 시간 포맷 변경 (앞 두 자리를 잘라냄)
    String formattedContactTime = contactTime;
    if (contactTime != null && contactTime.length() >= 10) {
      String datePart = contactTime.substring(2, 10);  // "25-04-24"
      String ampmPart = contactTime.substring(10);     // " / 오후"
      formattedContactTime = datePart + ampmPart;
    }

    String userid = member != null && member.getUserid() != null ? member.getUserid() : "(아이디없음)";
    String hallName = hall != null && hall.getName() != null ? hall.getName() : "(홀없음)";

    return ReservationDTO.builder()
      .idx(idx)
      .name(name)
      .eventType(eventType)
      .eventDate(eventDate) // Date 그대로 넘김
      .eventTimeSelect(formattedTimeSelect) // 시간 따로
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
      .userid(userid)
      .hallIdx(hall != null ? hall.getIdx() : null)
      .hallName(hallName)
      .userNote(userNote)
      .adminNote(adminNote)
      .lastModifiedBy(lastModifiedBy)
      .build();
  }
}
