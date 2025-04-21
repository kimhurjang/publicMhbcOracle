package com.example.mhbc.service;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.repository.HallRepository;
import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 예약 서비스 구현체
 * - 예약 등록, 조회, 수정, 삭제 로직 처리
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final MemberRepository memberRepository;
  private final HallRepository hallRepository;

  /**
   * 예약 등록 처리
   * - ReservationDTO → ReservationEntity 변환
   * - 연관된 MemberEntity, HallEntity 조회 후 Entity에 주입
   */
  @Override
  public void save(ReservationDTO dto) {
    MemberEntity member = memberRepository.findById(dto.getMemberIdx())
            .orElseThrow(); // 회원 조회 실패 시 예외 발생
    HallEntity hall = hallRepository.findById(dto.getHallIdx())
            .orElseThrow(); // 홀 조회 실패 시 예외 발생

    ReservationEntity entity = dto.toEntity(member, hall); // DTO → Entity
    reservationRepository.save(entity); // JPA 저장 (INSERT)
  }

  /**
   * 예약 전체 목록 조회
   * - Entity 목록을 DTO 목록으로 변환
   */
  @Override
  public List<ReservationDTO> findAll() {
    return reservationRepository.findAll() // DB에서 모든 예약 목록 가져옴
            .stream() // 리스트를 스트림으로 변환
            .map(ReservationEntity::toDTO) // 각 Entity를 DTO로 변환
            .collect(Collectors.toList()); // 변환된 DTO를 리스트로 수집해서 반환
  }

  /**
   * 예약 단건 상세 조회
   */
  @Override
  public ReservationDTO findById(Long id) {
    return reservationRepository.findById(id)
            .map(ReservationEntity::toDTO) // Entity → DTO 변환
            .orElse(null); // 없으면 null 반환
  }

  /**
   * 예약 수정 처리
   * - save()는 idx가 있으면 UPDATE로 작동
   */
  @Override
  public void update(ReservationDTO dto) {
    MemberEntity member = memberRepository.findById(dto.getMemberIdx())
            .orElseThrow();
    HallEntity hall = hallRepository.findById(dto.getHallIdx())
            .orElseThrow();

    ReservationEntity entity = dto.toEntity(member, hall); // DTO → Entity
    reservationRepository.save(entity); // JPA save()는 update로 동작
  }

  /**
   * 예약 삭제 처리
   */
  @Override
  public void delete(Long id) {
    reservationRepository.deleteById(id); // 해당 ID 삭제
  }
}
