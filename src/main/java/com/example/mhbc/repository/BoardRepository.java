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
    List<BoardEntity> findBoardsByGroupIdxSort(@Param("groupIdx") Long groupIdx , Sort sort);

    @Query("SELECT DISTINCT b FROM BoardEntity b JOIN b.attachment a WHERE b.group.groupIdx = :groupIdx")
    List<BoardEntity> findBoardsByGroupIdx(@Param("groupIdx") Long groupIdx);

    public BoardEntity findByIdx(Long idx);


    public List<BoardEntity> findByTitle(String title);

    public List<BoardEntity> findByGroupGroupIdx(Long groupIdx);

    @Transactional
    @Modifying
    @Query("DELETE FROM BoardEntity b WHERE b.idx = :idx")
    void deleteByIdx(@Param("idx") Long idx);


    @Query("SELECT b FROM BoardEntity b WHERE b.group.groupIdx = :groupIdx")
    Page<BoardEntity> findByGroupIdx(@Param("groupIdx") Long groupIdx, Pageable pageable);

    // 작성자 기준으로 전체 게시글 조회
    List<BoardEntity> findByMember(MemberEntity member);

    Page<BoardEntity> findByTitleContainingAndGroup_GroupIdxAndGroup_BoardType(String title, Long groupIdx, Long boardType , Pageable pageable);


    @Override
    Page<BoardEntity> findAll(Pageable pageable);

    // 2) 특정 그룹만 조회 (페이징)
    Page<BoardEntity> findByGroupGroupIdx(Long groupIdx, Pageable pageable);

    // 3) 그룹 없이 제목만 포함 검색 (페이징)
    Page<BoardEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 4) 그룹이 있을 때, 제목에 키워드 포함 검색 (페이징)
    Page<BoardEntity> findByGroupGroupIdxAndTitleContainingIgnoreCase(
            Long groupIdx, String keyword, Pageable pageable);

    // 5) 숫자만 입력된 경우: 제목에 포함 OR idx 일치 (그룹 없이) (페이징)
    Page<BoardEntity> findByTitleContainingIgnoreCaseOrIdx(
            String keyword, Long idx, Pageable pageable);

    // 6) 숫자만 입력된 경우: 그룹 필터 + (제목에 포함 OR idx 일치) (페이징)
    Page<BoardEntity> findByGroupGroupIdxAndTitleContainingIgnoreCaseOrGroupGroupIdxAndIdx(
            Long groupIdx1, String keyword,
            Long groupIdx2, Long idx,
            Pageable pageable);

    //사용자 idx로 게시물 조회
    List<BoardEntity> findByMemberIdx(Long memberIdx, Sort sort);

}

