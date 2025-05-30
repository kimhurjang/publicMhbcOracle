package com.example.mhbc.service.admin;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.dto.ReservationSearchCondition;
import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.entity.ScheduleBlockEntity;
import com.example.mhbc.repository.ReservationRepository;
import com.example.mhbc.repository.ScheduleBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 예약 서비스 구현체
 * - 예약 조회, 페이징, 검색, 수정, 삭제, 상태변경 로직 처리
 */
@Service
@RequiredArgsConstructor
public class AdminReservationServiceImpl implements AdminReservationService {

  private final ReservationRepository reservationRepository;
  private final ScheduleBlockRepository scheduleBlockRepository;

  // 예약 리스트 페이징 조회
  @Override
  public Page<ReservationDTO> findAll(Pageable pageable) {
    return reservationRepository.findAll(pageable)
      .map(ReservationEntity::toDTO);
  }

  // 예약 상태 일괄 수정 (기존 방식)
  @Override
  public void updateStatuses(List<String> updatedStatuses) {
    for (String statusEntry : updatedStatuses) {
      // 포맷: "idx=1:상태값"
      String[] parts = statusEntry.split(":");
      if (parts.length == 2) {
        Long idx = Long.parseLong(parts[0].replace("idx=", ""));
        String status = parts[1];

        reservationRepository.findById(idx).ifPresent(entity -> {
          entity.setStatus(status);
          reservationRepository.save(entity);
        });
      }
    }
  }

  // 예약 상태 일괄 수정 (Ajax 방식)
  @Override
  public void updateStatusesByAjax(List<Map<String, String>> updates) {
    for (Map<String, String> update : updates) {
      Long idx = Long.parseLong(update.get("idx"));
      String status = update.get("status");

      reservationRepository.findById(idx).ifPresent(entity -> {
        // 1. 예약예정일이 없으면 변경 불가
        Date eventDate = entity.getEventDate();
        if (eventDate == null) {
          throw new IllegalArgumentException("예약 예정일이 없어 변경이 불가능합니다.");
        }

        // 2. 행사 시간을 추출
        LocalDateTime eventDateTime = eventDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String timeSlot = String.format("%02d", eventDateTime.getHour()) + "시"; // "14" 형태로 변환

        // 3. 차단된 일정이 있는지 확인
        if (scheduleBlockRepository.existsByEventDateAndTimeSlot(eventDate, timeSlot)) {
          throw new IllegalArgumentException("등록된 확정 일정이 있어 변경이 불가능합니다.");
        }

        // 4. 상태 변경 및 저장
        entity.setStatus(status);
        reservationRepository.save(entity);
      });
    }
  }


  /*
  @Override
  public void updateStatusesByAjax(List<Map<String, String>> updates) {
    List<String> errors = new ArrayList<>();

    for (Map<String, String> update : updates) {
      Long idx = Long.parseLong(update.get("idx"));
      String status = update.get("status");

      reservationRepository.findById(idx).ifPresent(entity -> {
        entity.setStatus(status);

        if ("예약확정".equals(status)) {
          Date eventDate = entity.getEventDate();

          if (eventDate == null) {
            errors.add("예약번호 " + idx + "는 행사 예정일이 없어 확정할 수 없습니다.");
            //return;
          }

          String timeSlot = extractTime(eventDate);
          String key = formatKey(eventDate, timeSlot);

          boolean exists = scheduleBlockRepository.existsByEventDateAndTimeSlot(eventDate, timeSlot);
          if (exists) {
            errors.add("[" + key + "] 시간대는 이미 차단일정에 등록되어 있어 중복 확정이 불가능합니다.");
            //return;
          }
          // ✅ 하나라도 오류가 발생하면 예외를 던져 확정 중단
          if (!errors.isEmpty()) {
            throw new IllegalStateException(String.join("\n", errors));
          }

          // 문제 없으면 차단 테이블에 등록
          ScheduleBlockEntity block = new ScheduleBlockEntity();
          block.setEventDate(eventDate);
          block.setTimeSlot(timeSlot);
          block.setReason("예약확정");
          block.setCreatedAt(new Date());
          block.setModifiedBy(entity.getLastModifiedBy());
          scheduleBlockRepository.save(block);
        }

        reservationRepository.save(entity);
      });
    }

    if (!errors.isEmpty()) {
      throw new IllegalStateException(String.join("\n", errors));
    }
  }
*/

  // 시간 추출 (ex: 2025-06-01 14:00:00 → "14시")
  private String extractTime(Date date) {
    LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    return String.format("%02d시", ldt.getHour());
  }

  // 키 포맷용 날짜 + 시간 문자열 (확인용 메시지에 사용)
  private String formatKey(Date date, String timeSlot) {
    return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) + " " + timeSlot;
  }



  // 개별 예약 조회
  @Override
  public ReservationDTO findById(Long idx) {
    if (idx == null) return null;

    ReservationEntity entity = reservationRepository.findByIdxWithMemberAndHall(idx);
    return entity != null ? entity.toDTO() : null;
  }

  // 관리자 메모 수정
  @Override
  public void updateAdminNote(Long idx, String adminNote, String loginId) {
    ReservationEntity reservation = reservationRepository.findById(idx)
      .orElseThrow(() -> new IllegalArgumentException("잘못된 예약 번호입니다."));

    // 마지막 수정자 세팅
    reservation.setLastModifiedBy(loginId);
    reservation.setAdminNote(adminNote);
    reservationRepository.save(reservation);
  }

  @Override
  public Page<ReservationDTO> findByCondition(ReservationSearchCondition condition, Pageable pageable) {
    // 전체 엔티티 조회 (Fetch Join 사용)
    List<ReservationDTO> dtoList = reservationRepository.findAllWithMemberAndHall()
      .stream()
      .map(entity -> entity.toDTO()) // ✅ toDTO()로 변환
      .collect(Collectors.toList());

    // 필터 적용
    Stream<ReservationDTO> stream = dtoList.stream();

    // 🔍 검색어 필터
    if (condition.getSearchType() != null && condition.getKeyword() != null) {
      String keyword = condition.getKeyword().toLowerCase();
      switch (condition.getSearchType()) {
        case "userid":
          stream = stream.filter(r -> r.getUserid() != null && r.getUserid().toLowerCase().contains(keyword));
          break;
        case "name":
          stream = stream.filter(r -> r.getName() != null && r.getName().toLowerCase().contains(keyword));
          break;
        case "mobile":
          stream = stream.filter(r -> r.getMobile() != null && r.getMobile().contains(keyword));
          break;
      }
    }

    // 🔍 상태 필터
    if (condition.getStatus() != null && !condition.getStatus().isEmpty()) {
      stream = stream.filter(r -> r.getStatus() != null && condition.getStatus().contains(r.getStatus()));
    }

    // 🔍 홀 필터
    if (condition.getHallIdx() != null && !condition.getHallIdx().isEmpty()) {
      stream = stream.filter(r -> condition.getHallIdx().contains(String.valueOf(r.getHallIdx())));
    }

    // 🔍 날짜 필터
    if (condition.getStartDate() != null && condition.getEndDate() != null) {
      try {
        LocalDate start = LocalDate.parse(condition.getStartDate());
        LocalDate end = LocalDate.parse(condition.getEndDate());
        stream = stream.filter(r -> {
          if (r.getEventDate() == null) return false;
          LocalDate event = r.getEventDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
          return (event.compareTo(start) >= 0 && event.compareTo(end) <= 0);
        });
      } catch (DateTimeParseException e) {
        System.out.println(">> 날짜 파싱 오류: " + e.getMessage());
      }
    }

    List<ReservationDTO> filteredList = stream.collect(Collectors.toList());

    // 페이징 적용
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), filteredList.size());

    List<ReservationDTO> paged = filteredList.subList(start, end);
    return new PageImpl<>(paged, pageable, filteredList.size());
  }


}
