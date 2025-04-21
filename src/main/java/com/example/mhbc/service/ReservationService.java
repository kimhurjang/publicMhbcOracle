package com.example.mhbc.service;

import com.example.mhbc.dto.ReservationDTO;
import java.util.List;

public interface  ReservationService {

  void save(ReservationDTO dto);             // 예약 저장
  List<ReservationDTO> findAll();            // 전체 예약 목록 조회
  ReservationDTO findById(Long id);          // 단건 예약 조회
  void update(ReservationDTO dto);           // 예약 수정
  void delete(Long id);                      // 예약 삭제

}
