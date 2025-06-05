package com.example.mhbc.dto;

import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.ReservationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 예약 데이터 전달용 DTO
 * Entity와 뷰 사이에서 데이터 전달에 사용됨
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

  private Long idx; // 예약번호
  private String name; // 예약자 성함
  private String eventType; // 행사 종류

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date eventDate; // 행사 예정일 (날짜만 입력받음)

  private String eventTimeSelect; // 시간 선택 (예: "14")

  private Integer guestCnt; // 인원수
  private String mealType; // 식사 종류
  private String flower; // 꽃 종류
  private String contactTime; // 연락 가능 시간
  private String mobile; // 연락처
  private String status; // 예약 상태
  private Integer totalAmount; // 총 금액
  private Date createdAt; // 생성일
  private Date updatedAt; // 수정일
  private Long memberIdx; // 회원 번호
  private String userid; // 회원 아이디
  private Long hallIdx; // 홀 번호
  private String hallName; // 홀 이름
  private String userNote; // 사용자 메모
  private String adminNote; // 관리자 메모
  private String lastModifiedBy; // 마지막 수정자

  /**
   * DTO → Entity 변환 메서드
   * member, hall 객체는 컨트롤러에서 주입받아 전달
   */
  public ReservationEntity toEntity(MemberEntity member, HallEntity hall) {
    Date parsedDate;

    try {
      if (eventDate == null || eventTimeSelect == null || eventTimeSelect.isEmpty()) {
        parsedDate = null; // 날짜 미정이면 null로 저장
      } else {
        // eventDate(Date) → LocalDateTime → Timestamp
        LocalDateTime dateTime = eventDate.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate()
          .atTime(Integer.parseInt(eventTimeSelect), 0); // 시만 설정
        parsedDate = Timestamp.valueOf(dateTime);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("날짜 형식이 잘못되었습니다: eventDate=" + eventDate + ", eventTimeSelect=" + eventTimeSelect);
    }

    // contactTime 값 변환
    String parsedContactTime = contactTime;
    if (contactTime != null && contactTime.length() >= 8) { //25-04-24로 들어오면
      parsedContactTime = "20" + contactTime; // 앞에 "20"을 추가하여 "2025-04-24" 형태로 변환
    }

    ReservationEntity.ReservationEntityBuilder builder = ReservationEntity.builder()
      .name(name)
      .eventType(eventType)
      .eventDate(parsedDate)
      .guestCnt(guestCnt)
      .mealType(mealType)
      .flower(flower)
      .contactTime(parsedContactTime)
      .mobile(mobile)
      .status(status)
      .totalAmount(totalAmount)
      .createdAt(createdAt)
      .updatedAt(updatedAt)
      .member(member)
      .hall(hall)
      .userNote(userNote)
      .adminNote(adminNote)
      .lastModifiedBy(lastModifiedBy);

    if (idx != null) {
      builder.idx(idx); // 수정 시에만 ID 포함
    }

    return builder.build();
  }
}
