package com.example.mhbc.repository;

import com.example.mhbc.entity.AttachmentEntity;
import com.example.mhbc.entity.BoardEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {
    public AttachmentEntity findByBoard (BoardEntity Board);

    public AttachmentEntity findByIdx(long idx);

    List<AttachmentEntity> findByBoard_Idx(Long boardIdx);

    @Transactional
    @Modifying
    @Query("DELETE FROM AttachmentEntity a WHERE a.board.idx = :idx")
    void deleteByIdx(@Param("idx") long idx);
}
