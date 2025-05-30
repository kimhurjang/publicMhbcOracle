// 일정차단 Repository
package com.example.mhbc.repository;

import com.example.mhbc.entity.ScheduleBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleBlockRepository extends JpaRepository<ScheduleBlockEntity, Long> {

  // 특정 날짜 + 타임 존재 여부 확인 (예약 차단 여부 판단용)
  boolean existsByEventDateAndTimeSlot(Date eventDate, String timeSlot);

  // 특정 날짜 차단 전체 조회
  List<ScheduleBlockEntity> findByEventDate(Date eventDate);

  //삭제
  void deleteByEventDateAndTimeSlotIn(Date eventDate, List<String> timeSlots);

  //boolean existsByEventDateAndTimeSlot(Date eventDate, String timeSlot);


  List<ScheduleBlockEntity> findAll(); // list.html용

}

