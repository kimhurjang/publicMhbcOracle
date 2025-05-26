package com.example.mhbc.repository;

import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
  List<ReservationEntity> findAllByOrderByIdxDesc(); // 내림차순

  // 로그인한 회원의 예약만 조회
  List<ReservationEntity> findByMemberOrderByIdxDesc(MemberEntity member);

  // 로그인한 회원의 예약을 페이징으로 조회
  Page<ReservationEntity> findByMember(MemberEntity member, Pageable pageable);

  @Query("SELECT r FROM ReservationEntity r " +
          "JOIN FETCH r.member " +
          "JOIN FETCH r.hall " +
          "ORDER BY r.idx DESC")
  List<ReservationEntity> findAllWithMemberAndHall();

  @Query("SELECT r FROM ReservationEntity r " +
          "LEFT JOIN FETCH r.member " +
          "LEFT JOIN FETCH r.hall " +
          "WHERE r.idx = :idx")
  ReservationEntity findByIdxWithMemberAndHall(@org.springframework.data.repository.query.Param("idx") Long idx);

}