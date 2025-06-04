// 관리자 일정차단 엔티티
package com.example.mhbc.entity;

import com.example.mhbc.dto.ScheduleBlockDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "schedule_block")
@Getter
@Setter
public class ScheduleBlockEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idx")
  private Long idx; // PK

  @Temporal(TemporalType.DATE)
  @Column(name = "event_date")
  private Date eventDate; // 차단일자

  @Column(name = "time_slot")
  private String timeSlot; // 차단 시간 (10시 등)

  @Column(name = "reason")
  private String reason; // 차단 사유

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at")
  private Date createdAt; // 생성일자

  @Column(name = "modified_by")
  private String modifiedBy; // 마지막 수정자 ID

  // 예약과의 연관관계
  @ManyToOne
  @JoinColumn(name = "RESERVATION_IDX") // DB 칼럼명
  private ReservationEntity reservation;


}
