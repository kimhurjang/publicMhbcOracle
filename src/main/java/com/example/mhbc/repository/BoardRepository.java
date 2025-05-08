package com.example.mhbc.repository;

import com.example.mhbc.entity.BoardEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    @Query("SELECT b FROM BoardEntity b WHERE b.group.groupIdx = :groupIdx")
    public List<BoardEntity> findBoardsByGroupIdx(@Param("groupIdx") long groupIdx);

    public BoardEntity findByIdx(long idx);

    public List<BoardEntity> findByTitle(String title);

    public List<BoardEntity> findByGroupGroupIdx(long groupIdx);

    @Transactional
    @Modifying
    @Query("DELETE FROM BoardEntity b WHERE b.idx = :idx")
    void deleteByIdx(@Param("idx") long idx);

    @Query("SELECT b FROM BoardEntity b WHERE b.group.groupIdx = :groupIdx")
    Page<BoardEntity> findByGroupIdx(@Param("groupIdx") long groupIdx, Pageable pageable);

    // 작성자 기준으로 전체 게시글 조회
    List<BoardEntity> findByMemberIdx(Long memberIdx);

    List<BoardEntity> findByTitleContainingAndGroup_GroupIdxAndGroup_BoardType(String title, long groupIdx, Long boardType);


}