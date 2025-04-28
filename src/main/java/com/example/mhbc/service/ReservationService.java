package com.example.mhbc.service;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.entity.HallEntity;

import java.util.List;

public interface  ReservationService {

  List<HallEntity> getHallList();
  void save(ReservationDTO dto);             // 예약 저장
  List<ReservationDTO> findAll();            // 전체 예약 목록 조회
  ReservationDTO findById(Long idx);          // 단건 예약 조회
  void update(ReservationDTO dto, String loginId);           // 예약 수정
  void delete(Long idx);                      // 예약 삭제

}
