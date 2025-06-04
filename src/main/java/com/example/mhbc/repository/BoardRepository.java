package com.example.mhbc.repository;

import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.MemberEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
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

    @Query("SELECT DISTINCT b FROM BoardEntity b JOIN b.attachment a WHERE b.group.groupIdx = :groupIdx")
    List<BoardEntity> findBoardsByGroupIdxSort(@Param("groupIdx") long groupIdx , Sort sort);

    @Query("SELECT DISTINCT b FROM BoardEntity b JOIN b.attachment a WHERE b.group.groupIdx = :groupIdx")
    List<BoardEntity> findBoardsByGroupIdx(@Param("groupIdx") long groupIdx);

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
    List<BoardEntity> findByMember(MemberEntity member);

    List<BoardEntity> findByTitleContainingAndGroup_GroupIdxAndGroup_BoardType(String title, long groupIdx, Long boardType);


    // 1️⃣ 전체 게시글 (페이지네이션)
    Page<BoardEntity> findAll(Pageable pageable);

    // 2️⃣ 특정 그룹 (카테고리) 게시글
    //상단에 findByGroupIdx

    // 3️⃣ 키워드 포함 (예: 제목에 키워드 포함)
    Page<BoardEntity> findByTitleContaining(String keyword, Pageable pageable);

    // 4️⃣ 특정 그룹 + 키워드 포함
    Page<BoardEntity> findByGroupGroupIdxAndTitleContaining(Long groupIdx, String keyword, Pageable pageable);

    //사용자 idx로 게시물 조회
    List<BoardEntity> findByMemberIdx(Long memberIdx, Sort sort);

}

