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

import java.util.List;
import java.util.Map;
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
}
