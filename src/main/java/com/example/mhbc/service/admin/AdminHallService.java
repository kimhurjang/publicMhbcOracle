package com.example.mhbc.service.admin;

import com.example.mhbc.dto.HallDTO;

import java.util.List;

/**
 * 관리자 전용 Hall 서비스 인터페이스
 */
public interface AdminHallService {
  List<HallDTO> findAll();
  HallDTO findById(Long idx);
  void save(HallDTO hallDTO);
  void delete(Long idx);
}
