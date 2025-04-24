package com.example.mhbc.service;

import com.example.mhbc.entity.HallEntity;
import java.util.List;


/**
 * Hall 서비스 인터페이스
 * - 홀 목록, 단건 조회 기능 정의
 */
public interface HallService {
  HallEntity findById(Long idx);             // 홀 단건 조회
  List<HallEntity> getAllHalls();            // 전체 홀 목록 조회
}