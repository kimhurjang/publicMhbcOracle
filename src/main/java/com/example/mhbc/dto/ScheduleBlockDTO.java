// 일정차단 DTO (폼 입력/출력용)
package com.example.mhbc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleBlockDTO {
  private Long idx;
  private String eventDate; // String으로 받고 변환 처리 예정
  private String timeSlot;
  private String reason;
  private String modifiedBy; // 마지막 수정자 ID
}
