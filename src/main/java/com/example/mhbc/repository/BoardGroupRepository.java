package com.example.mhbc.repository;

import com.example.mhbc.entity.BoardGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardGroupRepository extends JpaRepository<BoardGroupEntity, Long> {

    BoardGroupEntity findByGroupIdx(long groupIdx);
}
