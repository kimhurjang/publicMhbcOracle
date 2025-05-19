package com.example.mhbc.controller.admin;


import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.BoardGroupEntity;
import com.example.mhbc.repository.BoardGroupRepository;
import com.example.mhbc.repository.BoardRepository;
import com.example.mhbc.service.BoardService;
import com.example.mhbc.service.admin.AdminBoardService;
import com.example.mhbc.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/board")
public class AdminBoardController {

    private final BoardRepository boardRepository;
    private final BoardGroupRepository boardGroupRepository;
    private final AdminBoardService adminBoardService;

    @RequestMapping("/list")
    public String board_page(Model model){


        model.addAttribute("webtitle", "게시물관리 | 리스트");

        return "/admin/board/list";
    }

    @RequestMapping("/group_list")
    public String group_list(Model model){

        model.addAttribute("page" , 1);
        model.addAttribute("webtitle", "게시물관리 | 리스트");
        return "/admin/board/group_list";
    }

    @PostMapping("/group_list_select")
    public String group_list_selelct(@RequestParam(value="group_idx" , required=false) Long groupIdx,
                                     @RequestParam("page") int page,
                                     @RequestParam(required=false , name="delCheck") List<Long> delCheck,
                                     @RequestParam(value = "group_idx_hidden", required = false) Long groupIdxHidden,
                                     @RequestParam("action") String action,
                                     RedirectAttributes redirectAttributes,
                                     Model model){

        String category = switch (groupIdx.intValue()) {
            case 1 -> "공지사항";
            case 2 -> "커뮤니티";
            case 3 -> "이벤트";
            case 4 -> "갤러리";
            case 5 -> "자주 질문";
            case 6 -> "1 대 1";
            default -> "기타";
        };
        if (groupIdx == null && groupIdxHidden != null) {
            groupIdx = groupIdxHidden;
        }
        if ("delete".equals(action)) {
            if (delCheck != null && !delCheck.isEmpty()) {
                adminBoardService.deleteBoardsByIds(delCheck);
                redirectAttributes.addFlashAttribute("msg", "선택한 게시글이 삭제되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("msg", "삭제할 게시글을 선택해주세요.");
            }
            redirectAttributes.addAttribute("group_idx", groupIdx);
            redirectAttributes.addAttribute("page", page);
            return "redirect:/admin/board/group_list_select"; // 삭제 후 리다이렉트
        }
        if("search".equals(action)) {
            int itemsPerPage = 10;
            int groupSize = 5;

            Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.ASC, "createdAt");

            Page<BoardEntity> paging = boardRepository.findByGroupIdx(groupIdx, pageable);
            BoardGroupEntity board = boardGroupRepository.findByGroupIdx(groupIdx);
            Long BoardType = board.getBoardType();

            int totalCount = (int) paging.getTotalElements();
            Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize, "link");


            // 기존 snake_case
            model.addAttribute("group_idx", groupIdx);
            model.addAttribute("board_type", BoardType);

            // fragment 가 기대하는 camelCase
            model.addAttribute("groupIdx", groupIdx);
            model.addAttribute("boardType", BoardType);
            model.addAttribute("page", page);         // 현재 페이지 번호

            model.addAttribute("link", "/admin/board/group_list_select");
            model.addAttribute("pagination", pagination);
            model.addAttribute("paging", paging);
            model.addAttribute("category", category);
        }
        return "/admin/board/group_list";

    }
    @RequestMapping("/delete")
    public String deleteBoard(@RequestParam("id") Long id,
                              @RequestParam("groupIdx") Long groupIdx,
                              @RequestParam("page") int page,
                              Model model) {
        // 삭제 로직 처리
        adminBoardService.deleteById(id);
        BoardGroupEntity board = boardGroupRepository.findByGroupIdx(groupIdx);
        Long BoardType = board.getBoardType();

        model.addAttribute("boardType" , BoardType);
        model.addAttribute("groupIdx" , groupIdx);
        model.addAttribute("group_idx" , groupIdx);
        model.addAttribute("page" , page);
        return "/admin/board/group_list";
    }



    @RequestMapping("/group_view")
    public String group_view(){

        return "";
    }





}
