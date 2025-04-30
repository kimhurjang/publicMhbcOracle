package com.example.mhbc.service.admin;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 예약 서비스 구현체
 * - 예약 조회, 페이징, 검색, 수정, 삭제, 상태변경 로직 처리
 */
@Service
@RequiredArgsConstructor
public class AdminReservationServiceImpl implements AdminReservationService {

  private final ReservationRepository reservationRepository;

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
        entity.setStatus(status);
        reservationRepository.save(entity);
      });
    }
  }

  // 개별 예약 조회
  @Override
  public ReservationDTO findById(Long idx) {
    if (idx == null) return null;
    return reservationRepository.findById(idx)
      .map(ReservationEntity::toDTO)
      .orElse(null);
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

}
