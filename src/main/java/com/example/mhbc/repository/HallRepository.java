package com.example.mhbc.repository;

import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallRepository extends JpaRepository<HallEntity, Long> {
}
