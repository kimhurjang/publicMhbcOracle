package com.example.mhbc.controller.admin;


import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.BoardGroupEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.repository.BoardGroupRepository;
import com.example.mhbc.repository.BoardRepository;
import com.example.mhbc.service.BoardService;
import com.example.mhbc.service.admin.AdminBoardService;
import com.example.mhbc.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;
import java.util.Set;

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
    public String group_list_select(
            @RequestParam(value = "group_idx", required = false) Long groupIdx,
            @RequestParam(value="page", defaultValue = "1") int page,
            @RequestParam(required = false, name = "delCheck") List<Long> delCheck,
            @RequestParam(value = "group_idx_hidden", required = false) Long groupIdxHidden,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam("action") String action,
            RedirectAttributes redirectAttributes) {

        if (groupIdx == null && groupIdxHidden != null) {
            groupIdx = groupIdxHidden;
        }

        switch (action) {
            case "delete":
                if (delCheck != null && !delCheck.isEmpty()) {
                    adminBoardService.deleteBoardsByIds(delCheck);
                    redirectAttributes.addFlashAttribute("msg", "선택한 게시글이 삭제되었습니다.");
                } else {
                    redirectAttributes.addFlashAttribute("msg", "삭제할 게시글을 선택해주세요.");
                }
                break;
            case "search":
                // 아무 로직 없이 리다이렉트로 검색 실행
                break;
            default:
                // 기타 액션 처리 필요 시
        }

        // 공통: 쿼리스트링에 groupIdx, page, keyword 유지
        redirectAttributes.addAttribute("group_idx", groupIdx);
        redirectAttributes.addAttribute("groupIdx", groupIdx);
        redirectAttributes.addAttribute("protectedIds", List.of(325L,326L, 327L, 328L, 329L, 330L, 331L));
        redirectAttributes.addAttribute("page", page);
        if (keyword != null && !keyword.isBlank()) {
            redirectAttributes.addAttribute("keyword", keyword);
        }
        return "redirect:/admin/board/group_list_select";
    }

    @GetMapping("/group_list_select")
    public String group_list_select_get(
            @RequestParam(value = "group_idx", required = false) Long groupIdx,
            @RequestParam(value="page", defaultValue = "1") int page,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        // 3️⃣ 커버: category 네이밍
        String category = switch (groupIdx != null ? groupIdx.intValue() : 0) {
            case 1 -> "공지사항";
            case 2 -> "커뮤니티";
            case 3 -> "이벤트";
            case 4 -> "갤러리";
            case 5 -> "자주 묻는 질문";
            case 6 -> "1 대 1 질문";
            default -> "기타";
        };

        int itemsPerPage = 10;
        int groupSize = 5;
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "createdAt");

        Page<BoardEntity> paging =
                adminBoardService.getBoardsByGroupAndKeyword(groupIdx, keyword, page);

        BoardGroupEntity boardGroup = boardGroupRepository.findByGroupIdx(groupIdx);
        Long boardType = (boardGroup != null ? boardGroup.getBoardType() : null);

        int totalCount = (int) paging.getTotalElements();
        Utility.Pagination pagination =
                new Utility.Pagination(page, itemsPerPage, totalCount, groupSize, "link");

        model.addAttribute("group_idx", groupIdx);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);
        model.addAttribute("page", page);
        model.addAttribute("link", "/admin/board/group_list_select");
        model.addAttribute("pagination", pagination);
        model.addAttribute("paging", paging);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("protectedIds", List.of(325L,326L, 327L, 328L, 329L, 330L, 331L));

        return "/admin/board/group_list";
    }


    @RequestMapping("/delete")
    public String deleteBoard(@RequestParam("id") Long id,
                              @RequestParam("groupIdx") Long groupIdx,
                              @RequestParam("page") int page,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {

        BoardGroupEntity board = boardGroupRepository.findByGroupIdx(groupIdx);
        Long BoardType = board.getBoardType();

        // 갤러리 고정 게시물 삭제 방지
        Set<Long> protectedIds = Set.of(326L, 327L, 328L, 329L, 330L, 331L);
        if (groupIdx == 4 && protectedIds.contains(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "이 게시물은 삭제할 수 없습니다.");
            return "redirect:" + Optional.ofNullable(request.getHeader("Referer")).orElse("/admin/board/group_list_select");
        }

        // 삭제 로직 처리
        adminBoardService.deleteById(id);

        redirectAttributes.addAttribute("boardType" , BoardType);
        redirectAttributes.addAttribute("groupIdx" , groupIdx);
        redirectAttributes.addAttribute("group_idx" , groupIdx);
        redirectAttributes.addAttribute("page" , page);

        // 이전 페이지 리다이렉트 (Referer 사용)
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/admin/board/group_list_select");
    }



    @RequestMapping("/group_view")
    public String group_view(@RequestParam("groupIdx") Long groupIdx,
                             @RequestParam("idx") Long boardIdx,
                             @RequestParam("page") int page,
                             Model model){

        BoardEntity board = boardRepository.findByIdx(boardIdx);
        BoardGroupEntity group = boardGroupRepository.findByGroupIdx(groupIdx);
        Long BoardType = group.getBoardType();
        MemberEntity member = board.getMember();

        String category = switch (groupIdx.intValue()) {
            case 1 -> "공지사항";
            case 2 -> "커뮤니티";
            case 3 -> "이벤트";
            case 4 -> "갤러리";
            case 5 -> "자주 묻는 질문";
            case 6 -> "1 대 1";
            default -> "기타";
        };

        model.addAttribute("page" , page);
        model.addAttribute("member" , member);
        model.addAttribute("category" , category);
        model.addAttribute("boardType" , BoardType);
        model.addAttribute("board" , board);
        model.addAttribute("groupIdx" , groupIdx);
        model.addAttribute("idx" , boardIdx);
        return "/admin/board/group_view";
    }

    @RequestMapping("/modify")
    public String modify(@RequestParam("id") Long boardIdx,
                         @RequestParam("groupIdx") Long groupIdx,
                         Model model){

        BoardEntity board = boardRepository.findByIdx(boardIdx);
        BoardGroupEntity group = boardGroupRepository.findByGroupIdx(groupIdx);
        Long BoardType = group.getBoardType();
        MemberEntity member = board.getMember();

        String category = switch (groupIdx.intValue()) {
            case 1 -> "공지사항";
            case 2 -> "커뮤니티";
            case 3 -> "이벤트";
            case 4 -> "갤러리";
            case 5 -> "자주 묻는 질문";
            case 6 -> "1 대 1";
            default -> "기타";
        };

        model.addAttribute("member" , member);
        model.addAttribute("category" , category);
        model.addAttribute("boardType" , BoardType);
        model.addAttribute("board" , board);
        model.addAttribute("groupIdx" , groupIdx);
        model.addAttribute("idx" , boardIdx);
        return"/admin/board/modify";
    }

    @PostMapping("/modify_proc")
    public String modify_proc(@RequestParam("idx") Long boardIdx,
                              @RequestParam("groupIdx") Long groupIdx,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @ModelAttribute BoardEntity board,
                              Model model){

        BoardEntity ModifyBoard = adminBoardService.modifyBoard(boardIdx,board,groupIdx);
        BoardGroupEntity group = boardGroupRepository.findByGroupIdx(groupIdx);
        Long BoardType = group.getBoardType();
        MemberEntity member = board.getMember();

        String category = switch (groupIdx.intValue()) {
            case 1 -> "공지사항";
            case 2 -> "커뮤니티";
            case 3 -> "이벤트";
            case 4 -> "갤러리";
            case 5 -> "자주 질문";
            case 6 -> "1 대 1";
            default -> "기타";
        };

        model.addAttribute("page" , page);
        model.addAttribute("member",member);
        model.addAttribute("category" , category);
        model.addAttribute("boardType" , BoardType);
        model.addAttribute("board" , ModifyBoard);
        model.addAttribute("groupIdx" , groupIdx);
        model.addAttribute("idx" , boardIdx);
        return "redirect:/admin/board/group_view?idx=" + boardIdx + "&groupIdx=" + groupIdx + "&page=" + page;
    }




}
