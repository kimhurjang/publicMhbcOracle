package com.example.mhbc.service.admin;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.repository.ReservationRepository;
import com.example.mhbc.service.HallService;
import lombok.RequiredArgsConstructor;
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

  //예약 전체 목록 조회
  @Override
  public List<ReservationDTO> findAll() {
    List<ReservationEntity> entities = reservationRepository.findAllByOrderByIdxDesc();
    //System.out.println(">> JPA로 가져온 Entity 수: " + entities.size());

    return reservationRepository.findAllByOrderByIdxDesc()
      .stream()
      .map(ReservationEntity::toDTO)
      .collect(Collectors.toList());
  }

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


}
