package com.example.mhbc.repository;

import com.example.mhbc.entity.ScheduleBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ScheduleBlockRepository extends JpaRepository<ScheduleBlockEntity, Long> {

  // [중복 확인] 특정 날짜 + 시간대에 차단된 일정이 있는지 여부 확인
  boolean existsByEventDateAndTimeSlot(Date eventDate, String timeSlot);

  // [일정 조회] 특정 날짜에 등록된 모든 차단 일정 조회
  List<ScheduleBlockEntity> findByEventDate(Date eventDate);

  // [일괄 삭제] 특정 날짜 + 복수 타임슬롯을 가진 차단 일정 삭제
  @Modifying
  @Transactional
  void deleteByEventDateAndTimeSlotIn(Date eventDate, List<String> timeSlots);

  // [단일 삭제] 특정 날짜 + 단일 타임슬롯을 가진 차단 일정 삭제 (예약 확정 취소 시 사용)
  @Modifying
  @Transactional
  @Query("DELETE FROM ScheduleBlockEntity b WHERE b.eventDate = :eventDate AND b.timeSlot = :timeSlot")
  void deleteByEventDateAndTimeSlot(Date eventDate, String timeSlot);

  // [전체 조회] 차단 일정 전체 리스트 조회 (관리자용)
  List<ScheduleBlockEntity> findAll();
}
