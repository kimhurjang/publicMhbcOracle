package com.example.mhbc.repository;

import com.example.mhbc.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
  List<ReservationEntity> findAllByOrderByIdxDesc(); // 내림차순

  @Query(value="select idx, name from reservation", nativeQuery = true)
  List<Object []> selectByAll();
}
