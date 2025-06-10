package com.example.mhbc.service.admin;

import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.BoardGroupEntity;
import com.example.mhbc.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBoardService {

    private final BoardRepository boardRepository;

    /**
     * 게시판 그룹 별 리스트
     */

    /*
     *       카테고리 찾기
     * */
    public Page<BoardDTO> findByCategory(String category, int page, int size) {

        long groupIdx = mapCategoryToGroupIdx(category);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdAt");
        Page<BoardEntity> paging = boardRepository.findByGroupIdx(groupIdx, pageable);

        Page<BoardEntity> boards = boardRepository.findByGroupIdx(groupIdx, pageable);

        return boards.map(BoardDTO::fromEntity);
    }

    private long mapCategoryToGroupIdx(String category) {
        return switch (category) {
            case "notice" -> 1L;
            case "gallery" -> 4L;
            case "event" -> 3L;
            case "cmct" -> 2L;
            case "oftenquestion" -> 5L;
            case "personalquestion" -> 6L;
            default -> throw new IllegalArgumentException("Unknown category: " + category);
        };

    }

    public void deleteBoardsByIds(List<Long> ids) {
        Set<Long> protectedIds = Set.of(325L,326L, 327L, 328L, 329L, 330L, 331L);
        List<Long> filtered = ids.stream()
                .filter(Objects::nonNull)
                .filter(id -> !protectedIds.contains(id))
                .collect(Collectors.toList());

        boardRepository.deleteAllById(filtered);
    }

    public void deleteById(Long idx) {
        Set<Long> protectedIds = Set.of(325L,326L, 327L, 328L, 329L, 330L, 331L);

        // 실제 게시물 조회
        Optional<BoardEntity> optionalBoard = boardRepository.findById(idx);

        if (optionalBoard.isPresent()) {
            BoardEntity board = optionalBoard.get();
            Long groupIdx = board.getGroup().getGroupIdx();
            Long boardType = board.getGroup().getBoardType();

            // 삭제 방지 조건: 갤러리 groupIdx == 4 && boardType == 1 && 고정 ID
            if (groupIdx == 4 && boardType == 1 && protectedIds.contains(idx)) {
                throw new IllegalStateException("고정된 갤러리 게시물은 삭제할 수 없습니다.");
            }

            // 삭제 수행
            boardRepository.deleteById(idx);
        }
    }

    @Transactional
    public BoardEntity modifyBoard(Long boardIdx , BoardEntity board, Long groupIdx) {

        BoardEntity boardEn = boardRepository.findByIdx(boardIdx);
        if (boardEn == null) {
            throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다.");
        }
        boardEn.setTitle(board.getTitle());
        boardEn.setContent(board.getContent());
        boardEn.setRequest(1);


        if (groupIdx != null) {
            if (groupIdx == 5) { // FAQ 게시판
                boardEn.setAnswerTitle(board.getAnswerTitle());
                boardEn.setAnswerContent(board.getAnswerContent());
            } else if (groupIdx == 6) { // 1대1 게시판
                boardEn.setRequestTitle(board.getRequestTitle());
                boardEn.setRequestContent(board.getRequestContent());
            }
        }

        return boardRepository.save(boardEn);
    }

    /*
    *       검색
    * */
    public Page<BoardEntity> getBoardsByGroupAndKeyword(Long groupIdx, String keyword, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        if ((groupIdx == null) && (keyword == null || keyword.isBlank())) {
            return boardRepository.findAll(pageable);

        } else if (groupIdx != null && (keyword == null || keyword.isBlank())) {
            // 그룹만 있고 키워드는 없는 경우
            return boardRepository.findByGroupGroupIdx(groupIdx, pageable);

        } else if (groupIdx == null) {
            // 그룹이 없고, 키워드만 있는 경우
            if (keyword.matches("\\d+")) {
                Long idx = Long.parseLong(keyword);
                // 숫자만 입력: 제목에 포함 OR idx 일치
                return boardRepository.findByTitleContainingIgnoreCaseOrIdx(keyword, idx, pageable);
            } else {
                // 숫자 아닌 키워드: 제목 포함
                return boardRepository.findByTitleContainingIgnoreCase(keyword, pageable);
            }

        } else {
            // 그룹이 있고 키워드도 있는 경우
            if (keyword.matches("\\d+")) {
                Long idx = Long.parseLong(keyword);
                // 숫자만 입력: 그룹 필터 + (제목에 포함 OR idx 일치)
                return boardRepository.findByGroupGroupIdxAndTitleContainingIgnoreCaseOrGroupGroupIdxAndIdx(
                        groupIdx, keyword, groupIdx, idx, pageable);
            } else {
                // 숫자가 아닌 키워드: 그룹 필터 + 제목 포함
                return boardRepository.findByGroupGroupIdxAndTitleContainingIgnoreCase(groupIdx, keyword, pageable);
            }
        }
    }

}

