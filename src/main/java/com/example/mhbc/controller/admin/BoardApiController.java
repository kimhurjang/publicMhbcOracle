package com.example.mhbc.controller.admin;

import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.service.admin.AdminBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/board")
public class BoardApiController {

    private final AdminBoardService adminboardService;


    @GetMapping(value = "/api/list", produces = "application/json")
    public Page<BoardDTO> getBoardList(
            @RequestParam("category") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<BoardDTO> boardPage = adminboardService.findByCategory(category, page, size);
        return ResponseEntity.ok(boardPage).getBody(); // 응답을 JSON 형태로 반환

    }
}
