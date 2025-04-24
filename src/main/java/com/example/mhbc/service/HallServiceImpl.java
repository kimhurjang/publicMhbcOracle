package com.example.mhbc.service;

import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
