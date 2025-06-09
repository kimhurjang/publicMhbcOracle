// 일정차단 DTO (폼 입력/출력용)
package com.example.mhbc.dto;

import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.entity.ScheduleBlockEntity;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.List;

@Getter
@Setter
public class ScheduleBlockDTO {

  private Long idx;                   // PK
  private String eventDate;          // yyyy-MM-dd 포맷 문자열
  private String reason;             // 차단 사유
  private String modifiedBy;         // 수정자 ID

  private String timeSlot;           // 단일 시간대 (list에서 사용)
  private List<String> timeSlots;    // 복수 시간대 (form에서 사용)

  private String reservationName;  // 예약자명 (예약확정일 경우)
  private Long reservationIdx; // 예약 ID

  public static ScheduleBlockDTO fromEntity(ScheduleBlockEntity entity) {
    ScheduleBlockDTO dto = new ScheduleBlockDTO();
    dto.setIdx(entity.getIdx());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    dto.setEventDate(sdf.format(entity.getEventDate()));

    dto.setReason(entity.getReason());
    dto.setTimeSlot(entity.getTimeSlot());
    dto.setModifiedBy(entity.getModifiedBy());

    if (entity.getReservation() != null) {
      dto.setReservationName(entity.getReservation().getName());
      dto.setReservationIdx(entity.getReservation().getIdx());
    }
    return dto;
  }

}
