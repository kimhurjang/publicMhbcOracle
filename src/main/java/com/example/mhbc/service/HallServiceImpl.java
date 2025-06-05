package com.example.mhbc.service;

import com.example.mhbc.dto.HallDTO;
import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HallServiceImpl implements HallService {

  private final HallRepository hallRepository;

  @Override
  public HallEntity findById(Long idx) {
    return hallRepository.findById(idx)
            .orElseThrow(); // 없을 경우 예외 발생
  }

  @Override
  public List<HallEntity> getAllHalls() {
    return hallRepository.findAll();
  }

  // 특정 홀 정보 가져오기
  public HallDTO getHallById(Long hallIdx) {
    HallEntity hallEntity = hallRepository.findById(hallIdx)
            .orElseThrow(() -> new NoSuchElementException("해당 홀을 찾을 수 없습니다: " + hallIdx));
    return hallEntity.toDTO();
  }
}
