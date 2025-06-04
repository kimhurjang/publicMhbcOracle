package com.example.mhbc.repository;

import com.example.mhbc.entity.CommentsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {
    @Query("SELECT c FROM CommentsEntity c JOIN FETCH c.member WHERE c.board.idx = :boardIdx")
    List<CommentsEntity> findByBoard_idxWithMember(@Param("boardIdx") long boardIdx);

    CommentsEntity findByIdx(long idx);

    @Transactional
    @Modifying
    @Query("DELETE FROM CommentsEntity b WHERE b.idx = :idx")
    void deleteByIdx(@Param("idx") long idx);

    @Query("SELECT c.board.idx, COUNT(c) FROM CommentsEntity c GROUP BY c.board.idx")
    List<Object[]> countCommentsGroupedByBoardId();
}
