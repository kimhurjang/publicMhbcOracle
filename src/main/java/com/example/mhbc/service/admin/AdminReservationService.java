package com.example.mhbc.service.admin;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.dto.ReservationSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AdminReservationService {

  // 예약 리스트 페이징 조회
  Page<ReservationDTO> findAll(Pageable pageable);

  ReservationDTO findById(Long idx);
  void updateStatuses(List<String> updatedStatuses); // 상태변경
  void updateStatusesByAjax(List<Map<String, String>> updates); // 상태변경 ajax
  void updateAdminNote(Long idx, String adminNote, String loginId);
  Page<ReservationDTO> findByCondition(ReservationSearchCondition condition, Pageable pageable);

  void updateReservation(ReservationDTO dto); //수정

}
