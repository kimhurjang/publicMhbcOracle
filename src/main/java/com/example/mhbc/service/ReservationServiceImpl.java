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
  private final HallService hallService;

  // ReservationForm에서 필요한 Hall 리스트
  public List<HallEntity> getHallList() {
    return hallService.getAllHalls();
  }

  // 연락처 포맷 함수 (01012345678 → 010-1234-5678)
  private String formatMobileNumber(String number) {
    if (number.length() == 11) {
      return number.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    } else if (number.length() == 10) {
      return number.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
    } else {
      return number;
    }
  }

  /**
   * 예약 등록 처리
   * - ReservationDTO → ReservationEntity 변환
   * - 연관된 MemberEntity, HallEntity 조회 후 Entity에 주입
   */
  @Override
  public void save(ReservationDTO dto) {
    MemberEntity member = memberRepository.findById(dto.getMemberIdx())
      .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 memberIdx입니다: " + dto.getMemberIdx()));
    HallEntity hall = hallService.findById(dto.getHallIdx()); // 홀 조회 실패 시 예외 발생

    // 연락처에 하이픈이 없을 경우 자동 추가
    String mobile = dto.getMobile();
    if (mobile != null && !mobile.contains("-")) {
      dto.setMobile(formatMobileNumber(mobile));
    }
    // status 기본값 설정
    if (dto.getStatus() == null || dto.getStatus().trim().isEmpty()) {
      dto.setStatus("상담대기");
    }

    // ReservationEntity 값 확인
    // System.out.println(">>>>>>>>>ReservationEntity: " + entity);
    // System.out.println(">>>>>>>>>memberEntity: " + member); // 디버깅용 출력
    // System.out.println(">>>>>>>>>hallEntity: " + hall); // 디버깅용 출력

    ReservationEntity entity = dto.toEntity(member, hall); // DTO → Entity
    reservationRepository.save(entity); // JPA 저장 (INSERT)
  }

  // 예약 전체 목록 조회 - Entity 목록을 DTO 목록으로 변환
  @Override
  public List<ReservationDTO> findAll() {
    List<ReservationEntity> entities = reservationRepository.findAllByOrderByIdxDesc();
    System.out.println(">> JPA로 가져온 Entity 수: " + entities.size());

    //entities.forEach(e -> System.out.println(">> entity: " + e.getIdx() + " / " + e.getName() + " / " + e.getStatus()));

    return reservationRepository.findAllByOrderByIdxDesc() // DB에서 모든 예약 목록 가져옴
            .stream() // 리스트를 스트림으로 변환
            .map(ReservationEntity::toDTO) // 각 Entity를 DTO로 변환
            .collect(Collectors.toList()); // 변환된 DTO를 리스트로 수집해서 반환
  }

  //예약 단건 상세 조회
  @Override
  public ReservationDTO findById(Long idx) {
    if (idx == null) return null; // null 체크 추가
    return reservationRepository.findById(idx)
            .map(ReservationEntity::toDTO) // Entity → DTO 변환
            .orElse(null); // 없으면 null 반환
  }

  // 예약 수정 처리  save()는 idx가 있으면 UPDATE로 작동
  @Override
  public void update(ReservationDTO dto, String loginId) {
    MemberEntity member = memberRepository.findById(dto.getMemberIdx())
            .orElseThrow();
    HallEntity hall = hallService.findById(dto.getHallIdx());

    // 연락처에 하이픈이 없을 경우 자동 추가
    String mobile = dto.getMobile();
    if (mobile != null && !mobile.contains("-")) {
      dto.setMobile(formatMobileNumber(mobile));
    }
    // status 기본값 설정
    if (dto.getStatus() == null || dto.getStatus().trim().isEmpty()) {
      dto.setStatus("상담대기");
    }

    // 마지막 수정자 세팅
    dto.setLastModifiedBy(loginId);

    ReservationEntity entity = dto.toEntity(member, hall); // DTO → Entity
    reservationRepository.save(entity); // JPA save()는 update로 동작
  }

  // 예약 삭제 처리
  @Override
  public void delete(Long idx) {
    reservationRepository.deleteById(idx); // 해당 ID 삭제
  }
}
