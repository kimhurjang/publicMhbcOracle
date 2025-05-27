package com.example.mhbc.service.admin;

import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        boardRepository.deleteAllByIdInBatch(ids);
    }

    public void deleteById(Long idx) {
        boardRepository.deleteById(idx);
    }

    @Transactional
    public BoardEntity modifyBoard(Long boardIdx , BoardEntity board) {

        BoardEntity boardEn = boardRepository.findByIdx(boardIdx);
        if (boardEn == null) {
            throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다.");
        }
        boardEn.setTitle(board.getTitle());
        boardEn.setContent(board.getContent());
        boardEn.setRequest(1);

        // FAQ인 경우, 답변 필드도 수정
        if (board.getAnswerTitle() != null && board.getAnswerContent() != null) {
            boardEn.setAnswerTitle(board.getAnswerTitle());
            boardEn.setAnswerContent(board.getAnswerContent());
        }
        if (board.getRequestTitle() != null && board.getRequestContent() != null) {
            boardEn.setAnswerTitle(board.getAnswerTitle());
            boardEn.setAnswerContent(board.getAnswerContent());
        }
        return boardEn;
    }

    /*
    *       검색
    * */
    public Page<BoardEntity> getBoardsByGroupAndKeyword(Long groupIdx, String keyword, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (groupIdx == null && (keyword == null || keyword.isBlank())) {
            return boardRepository.findAll(pageable);
        } else if (groupIdx != null && (keyword == null || keyword.isBlank())) {
            return boardRepository.findByGroupIdx(groupIdx, pageable);
        } else if (groupIdx == null) {
            return boardRepository.findByTitleContaining(keyword, pageable);
        } else {
            return boardRepository.findByGroupGroupIdxAndTitleContaining(groupIdx, keyword, pageable);
        }
    }

}

