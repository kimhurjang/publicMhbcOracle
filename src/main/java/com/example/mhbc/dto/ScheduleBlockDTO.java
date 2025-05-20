// 일정차단 DTO (폼 입력/출력용)
package com.example.mhbc.dto;

import lombok.Getter;
import lombok.Setter;

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
}
