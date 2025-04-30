package com.example.mhbc.repository;

import com.example.mhbc.entity.SnsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnsRepository extends JpaRepository<SnsEntity, Long> {
    Optional<SnsEntity> findBySnsId(String snsId);
}