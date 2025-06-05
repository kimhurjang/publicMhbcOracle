package com.example.mhbc.service;

import com.example.mhbc.dto.MemberDTO;
import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.repository.ReservationRepository;
import com.example.mhbc.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


  /**
   * [공통] 예약 폼에서 사용될 전체 홀 리스트 조회
   */
  public List<HallEntity> getHallList() {
    return hallService.getAllHalls();
  }


  /**
   * [공통] 연락처 하이픈 자동 포맷터 (예: 01012345678 → 010-1234-5678)
   */
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
   * [등록] 로그인 사용자의 예약 등록 처리
   * - DTO → Entity 변환
   * - member, hall 연관 엔티티 주입
   * - 연락처 포맷 및 상태 기본값 설정
   */
  @Override
  public void save(ReservationDTO dto) {
    MemberDTO loginMember = Utility.getLoginMemberDTO(); // AccessDeniedException 발생 가능

    MemberEntity member = memberRepository.findById(loginMember.getIdx())
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

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

    ReservationEntity entity = dto.toEntity(member, hall); // DTO → Entity
    reservationRepository.save(entity); // JPA 저장 (INSERT)
  }


  /**
   * [조회] 전체 예약 목록 조회 (관리자용)
   * - Entity → DTO 변환
   */
  @Override
  public List<ReservationDTO> findAll() {
    List<ReservationEntity> entities = reservationRepository.findAllByOrderByIdxDesc();
    //System.out.println(">> JPA로 가져온 Entity 수: " + entities.size());
    //entities.forEach(e -> System.out.println(">> entity: " + e.getIdx() + " / " + e.getName() + " / " + e.getStatus()));

    return reservationRepository.findAllByOrderByIdxDesc() // DB에서 모든 예약 목록 가져옴
            .stream() // 리스트를 스트림으로 변환
            .map(ReservationEntity::toDTO) // 각 Entity를 DTO로 변환
            .collect(Collectors.toList()); // 변환된 DTO를 리스트로 수집해서 반환
  }

  /**
   * [조회] 예약 상세 조회 (단건)
   * - idx가 null인 경우 null 반환
   */
  @Override
  public ReservationDTO findById(Long idx) {
    if (idx == null) return null; // null 체크 추가

    ReservationEntity entity = reservationRepository.findByIdxWithMemberAndHall(idx);
    return entity != null ? entity.toDTO() : null;
  }

  /**
   * [조회] 로그인 사용자의 예약 목록 조회 (비페이징)
   */
  @Override
  public List<ReservationDTO> findByLoginUser() {
    MemberDTO loginMember = Utility.getLoginMemberDTO(); // AccessDeniedException 발생 가능
    MemberEntity member = memberRepository.findById(loginMember.getIdx())
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

    List<ReservationEntity> list = reservationRepository.findByMemberOrderByIdxDesc(member);

    return list.stream()
            .map(ReservationEntity::toDTO)
            .collect(Collectors.toList());
  }

  /**
   * [조회] 로그인 사용자의 예약 목록 페이징 조회
   */
  @Override
  public Page<ReservationDTO> findByLoginUserPage(Pageable pageable) {
    MemberDTO loginMember = Utility.getLoginMemberDTO(); // AccessDeniedException 발생 가능
    MemberEntity member = memberRepository.findById(loginMember.getIdx())
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

    return reservationRepository.findByMember(member, pageable)
            .map(ReservationEntity::toDTO);
  }


  /**
   * [수정] 예약 정보 수정 처리
   * - idx가 있는 상태에서 save() → UPDATE
   * - 마지막 수정자 ID 설정
   */
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

  /**
   * [삭제] 예약 정보 삭제
   * - 예약 ID 기준으로 삭제 처리
   */
  @Override
  public void delete(Long idx) {
    reservationRepository.deleteById(idx); // 해당 ID 삭제
  }
}