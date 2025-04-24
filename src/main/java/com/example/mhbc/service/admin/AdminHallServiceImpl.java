package com.example.mhbc.service.admin;

import com.example.mhbc.dto.HallDTO;
import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 관리자 전용 Hall 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class AdminHallServiceImpl implements AdminHallService{

  private final HallRepository hallRepository;

  @Override
  public List<HallDTO> findAll() {
    return hallRepository.findAll()
      .stream()
      .map(HallEntity::toDTO)
      .collect(Collectors.toList());
  }

  @Override
  public HallDTO findById(Long idx) {
    return hallRepository.findById(idx)
      .map(HallEntity::toDTO)
      .orElse(null);
  }

  @Override
  public void save(HallDTO hallDTO) {
    HallEntity entity = hallDTO.toEntity();
    hallRepository.save(entity);
  }

  @Override
  public void delete(Long idx) {
    hallRepository.deleteById(idx);
  }
}
