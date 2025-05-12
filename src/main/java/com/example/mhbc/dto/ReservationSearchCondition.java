package com.example.mhbc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 예약 검색 조건 DTO (HTML form 파라미터명과 일치)
 */
@Getter
@Setter
public class ReservationSearchCondition {

  private String searchType;     // 검색 유형 (userid, name, mobile)
  private String keyword;        // 검색어

  private String startDate;      // 시작일자 (String → LocalDate)
  private String endDate;        // 종료일자

  private List<String> status;   // 상태 필터 (WAIT, CONFIRM, ...)
  private List<String> hallIdx;  // 홀 필터 (1, 2, 3)

  @Override
  public String toString() {
    return "ReservationSearchCondition{" +
      "searchType='" + searchType + '\'' +
      ", keyword='" + keyword + '\'' +
      ", startDate='" + startDate + '\'' +
      ", endDate='" + endDate + '\'' +
      ", status=" + status +
      ", hallIdx=" + hallIdx +
      '}';
  }
}
