package com.example.mhbc.service.admin;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.entity.HallEntity;

import java.util.List;
import java.util.Map;

public interface AdminReservationService {

  List<ReservationDTO> findAll();            // 전체 예약 목록 조회
  void updateStatuses(List<String> updatedStatuses);
  void updateStatusesByAjax(List<Map<String, String>> updates);
}
