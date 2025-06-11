package com.example.mhbc.controller;

import com.example.mhbc.util.Utility;
import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.dto.CommentsDTO;
import com.example.mhbc.dto.CommonForm;
import com.example.mhbc.dto.MemberDTO;
import com.example.mhbc.entity.*;
import com.example.mhbc.repository.*;
import com.example.mhbc.service.BoardService;
import com.example.mhbc.service.CommentsService;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentsService commentsService;
    private final MemberRepository memberRepository;
    private final BoardGroupRepository boardGroupRepository;
    private final BoardRepository boardRepository;
    private final CommentsRepository commentsRepository;
    private final AttachmentRepository attachmentRepository;
    private final Utility utility;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("attachment");
    }


    /*갤러리*/
    @RequestMapping("/gallery")
    public String gallery() {
        System.out.println(">>>>>>>>>>gallery page<<<<<<<<<<");
        Long boardType = 1L;
        Long groupIdx = 4L;

        return "redirect:/board/gallery_page?board_type=" + boardType + "&group_idx=" + groupIdx;
    }
    @RequestMapping("/gallery_page")
    public String gallery_page(@RequestParam("board_type") Long boardType,
                               @RequestParam("group_idx") Long groupIdx,
                               Model model) {

        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser != null) {
            MemberEntity member = memberRepository.findByIdx(loginUser);
            model.addAttribute("member", member);
        } else {
            model.addAttribute("member", null);  // 로그인되지 않은 경우
        }

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx,"createdAt","DESC");

        for (BoardEntity board : boardList) {
            AttachmentEntity attachment = attachmentRepository.findByBoard(board);
            board.setAttachment(attachment); // BoardEntity에 Attachment 필드 추가
        }

        model.addAttribute("webtitle", "만화방초 | 갤러리");
        model.addAttribute("boardList", boardList);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardList", boardList);

        return "board/gallery_page";
    }
    @RequestMapping("/gallery_view")
    public String gallery_iew(@RequestParam("idx") Long idx,
                              @RequestParam("group_idx") Long groupIdx,
                              @RequestParam("board_type") Long boardType,
                              Model model) {

        BoardEntity board = boardService.getBoardByIdx(idx);
        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser != null) {
            MemberEntity member = memberRepository.findByIdx(loginUser);
            model.addAttribute("member", member);
        } else {
            model.addAttribute("member", null);  // 로그인되지 않은 경우
        }

        model.addAttribute("boardType", boardType);
        model.addAttribute("board", board);
        model.addAttribute("groupIdx", groupIdx);

        return "board/gallery_view";
    }
    @RequestMapping("/gallery_write")
    public String gallery_write(@RequestParam("group_idx") Long groupIdx,
                                @RequestParam("board_type") Long boardType,
                                Model model){

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx,"createdAt","DESC");

        model.addAttribute("commonForm", new CommonForm());
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardList", boardList);

        return "board/gallery_write";
    }
    @PostMapping("/gallery_proc")
    public String gallery_proc(@Valid CommonForm form,
                               BindingResult result,
                               Model model,
                               @ModelAttribute BoardEntity board,
                               @ModelAttribute AttachmentEntity attachmentEntity,
                               @RequestParam("attachment") MultipartFile attachment,
                               @RequestParam("group_idx") Long groupIdx,
                               @RequestParam("board_type") Long boardType,
                               RedirectAttributes redirectAttributes) {

        /*유효성 검사*/
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "redirect:/board/gallery_write";
        }

        attachmentEntity.setCreatedAt(new Date());
        board.setCreatedAt(new Date());
        attachmentEntity.setBoard(board); // 첨부파일이 어떤 게시글에 속하는지 연결

        if (attachment != null && !attachment.isEmpty()) {
            try {
                boardService.saveBoard(board,groupIdx);
                utility.saveAttachment(attachment,board);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "파일 업로드 실패");
                return "redirect:/board/gallery_page?board_type=" + boardType + "&group_idx=" + groupIdx;
            }
        }

        // 게시글 저장 및 리다이렉트 로직 추가되어야 함 (예: boardRepository.save(board), 등)
        return "redirect:/board/gallery_page?board_type=" + boardType + "&group_idx=" + groupIdx;
    }



    /*이벤트*/
    @RequestMapping("/event")
    public String event() {
        System.out.println(">>>>>>>>>>event page<<<<<<<<<<");
        Long boardType = 1L;
        Long groupIdx = 3L;
        return "redirect:/board/event_page?board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/event_page")
    public String event_Page(@RequestParam("board_type") Long boardType,
                             @RequestParam("group_idx") Long groupIdx,
                             Model model) {

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx,"createdAt","DESC");

        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser != null) {
            MemberEntity member = memberRepository.findByIdx(loginUser);
            model.addAttribute("member", member);
        } else {
            model.addAttribute("member", null);  // 로그인되지 않은 경우
        }
        Date now = new Date();
        // null 안전하게 처리
        List<BoardDTO> boardDtoList = boardList.stream().map(board -> {
            BoardDTO dto = new BoardDTO();
            dto.setStartAt(board.getStartAt());
            dto.setClosedAt(board.getClosedAt());
            dto.setIdx(board.getIdx());             // 링크 idx
            dto.setTitle(board.getTitle());         // 제목

            dto.setStartAt(board.getStartAt());    // 시작일
            dto.setClosedAt(board.getClosedAt());  // 종료일
            dto.setCreatedAt(board.getCreatedAt()); // 작성일

            // 첨부파일 관련 (BoardEntity 기준)
            if(board.getAttachment() != null) {
                dto.setAttachment(board.getAttachment());  // attachment 엔티티/DTO를 dto에 넣어야 함
            }
            // 상태 계산
            dto.setStarted(board.getStartAt() != null && now.after(board.getStartAt()));
            dto.setClosed(board.getClosedAt() != null && now.after(board.getClosedAt()));

            return dto;
        }).collect(Collectors.toList());
        model.addAttribute("webtitle", "만화방초 | 이벤트");
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardList", boardDtoList);

        return "board/event_page";
    }
    @RequestMapping("/event_view")
    public String event_view(@RequestParam("idx") Long idx,
                             @RequestParam("group_idx") Long groupIdx,
                             @RequestParam("board_type") Long boardType,
                             Model model) {


        BoardEntity board = boardService.getBoardByIdx(idx);

        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser != null) {
            MemberEntity member = memberRepository.findByIdx(loginUser);
            model.addAttribute("member", member);
        } else {
            model.addAttribute("member", null);  // 로그인되지 않은 경우
        }
        Date now = new Date();
        boolean isStarted = board.getStartAt() != null && now.after(board.getStartAt());
        boolean isClosed = board.getClosedAt() != null && now.after(board.getClosedAt());

        model.addAttribute("isStarted", isStarted);
        model.addAttribute("isClosed", isClosed);
        model.addAttribute("board", board);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);


        return "board/event_view";
    }
    @RequestMapping("/event_write")
    public String event_write(@RequestParam("group_idx") Long groupIdx,
                              @RequestParam("board_type") Long boardType,
                              Model model){

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx,"createdAt","DESC");

        model.addAttribute("commonForm", new CommonForm());
        model.addAttribute("boardList",boardList);
        model.addAttribute("groupIdx",groupIdx);
        model.addAttribute("boardType",boardType);

        return "board/event_write";
    }
    @PostMapping("/event_proc")
    public String event_proc(@Valid CommonForm form,
                             BindingResult result,
                             Model model,
                             @ModelAttribute BoardEntity board,
                             @ModelAttribute AttachmentEntity attachmentEntity,
                             @RequestParam("attachment") MultipartFile attachment,
                             @RequestParam("group_idx") Long groupIdx,
                             @RequestParam("board_type") Long boardType,
                             RedirectAttributes redirectAttributes){

        /*유효성 검사*/
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "redirect:/board/event_write";
        }
        attachmentEntity.setCreatedAt(new Date());
        board.setCreatedAt(new Date());
        attachmentEntity.setBoard(board); // 첨부파일이 어떤 게시글에 속하는지 연결

        if (attachment != null && !attachment.isEmpty()) {
            try {
                boardService.saveBoard(board,groupIdx);
                utility.saveAttachment(attachment,board);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "파일 업로드 실패");
                return "redirect:/board/event_page?board_type=" + boardType + "&group_idx=" + groupIdx;
            }
        }


        // 게시글 저장 및 리다이렉트 로직 추가되어야 함 (예: boardRepository.save(board), 등)
        return "redirect:/board/event_page?board_type=" + boardType + "&group_idx=" + groupIdx;
    }





    /*자주 질문*/
    @RequestMapping("/oftenquestion")
    public String oftenquestion() {
        System.out.println(">>>>>>>>>>oftenquestion page<<<<<<<<<<");
        Long boardType = 2L;
        Long groupIdx = 5L;
        int page = 1;
        return "redirect:/board/oftenquestion_page?page="+page+"&board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/oftenquestion_page")
    public String oftenquestion_page(@RequestParam("board_type") Long boardType,
                                     @RequestParam("group_idx") Long groupIdx,
                                     @RequestParam("page") int page,
                                     Model model) {

        int itemsPerPage = 4;
        int groupSize = 3;

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "createdAt");
        Page<BoardEntity> paging = boardRepository.findByGroupIdx(groupIdx,pageable);

        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser != null) {
            MemberEntity member = memberRepository.findByIdx(loginUser);
            model.addAttribute("member", member);
        } else {
            model.addAttribute("member", null);  // 로그인되지 않은 경우
        }

        int totalCount = (int) paging.getTotalElements();

        Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize,"link");

        model.addAttribute("paging", paging);
        model.addAttribute("link","/board/oftenquestion_page");
        model.addAttribute("pagination", pagination);
        model.addAttribute("webtitle", "만화방초 | 자주 묻는 질문");
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);

        return "board/oftenquestion_page";
    }
    @RequestMapping("/oftenquestion_view")
    public String oftenquestion_view(@RequestParam("idx") Long idx,
                                     @RequestParam("title") String title,
                                     @RequestParam("board_type") Long boardType,
                                     @RequestParam("group_idx") Long groupIdx,
                                     Model model) {

        BoardEntity board = boardRepository.findByIdx(idx);
        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser != null) {
            MemberEntity member = memberRepository.findByIdx(loginUser);
            model.addAttribute("member", member);
        } else {
            model.addAttribute("member", null);  // 로그인되지 않은 경우
        }
        model.addAttribute("board", board);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("idx", idx);
        model.addAttribute("title", title);

        return "board/oftenquestion_view";
    }
    @RequestMapping("/oftenquestion_write")
    public String oftenquestion_write( @RequestParam("board_type") Long boardType,
                                       @RequestParam("group_idx") Long groupIdx,
                                       Model model){

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        model.addAttribute("commonForm", new CommonForm());
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("today", today);

        return "/board/oftenquestion_write";
    }
    @GetMapping("oftenquestion_write_proc")
    public String oftenquestion_write_proc(@Valid CommonForm form,
                                           BindingResult result,
                                           @ModelAttribute BoardEntity board,
                                           @RequestParam("board_type") Long boardType,
                                           @RequestParam("group_idx") Long groupIdx,
                                           Model model) throws IOException {
        System.out.println(">>>>>>>>>>oftenquestion_write_proc page<<<<<<<<<<");

        /*유효성 검사*/
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "redirect:/board/cmct_write";
        }
            board.setCreatedAt(new Date());
            board.setRequest(0);
            boardService.saveBoard(board,groupIdx);

        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);

        return "redirect:/board/oftenquestion_page?page=1&board_type=" + boardType + "&group_idx=" + groupIdx;

    }

    /*1대1문의*/
    @RequestMapping("/personalquestion")
    public String personalquestion_page(Model model) {
        System.out.println(">>>>>>>>>>personalquestionpage page<<<<<<<<<<");

        Long boardType = 2L;
        Long groupIdx = 6L;

        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser == null) {
            return  "redirect:/api/member/login";
        }

        return "redirect:/board/personalquestion_page?board_type="+boardType+"&group_idx="+groupIdx+"&idx="+loginUser;
    }
    @RequestMapping("/personalquestion_page")
    public String personalquestion_page(@RequestParam("board_type") Long boardType,
                                        @RequestParam("group_idx") Long groupIdx,
                                        @RequestParam("idx") Long loginUser,
                                        Model model) {

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx,"createdAt","DESC");
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        MemberEntity member = memberRepository.findByIdx(loginUser);

        model.addAttribute("member", member);
        model.addAttribute("webtitle", "만화방초 | 1대1 질문");
        model.addAttribute("commonForm", new CommonForm());
        model.addAttribute("today", today);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardList", boardList);

        return "board/personalquestion_page";
    }
    @RequestMapping("/myboard")
    public String myboard(Model model) {

        Long loginUser = Utility.getLoginUserIdx();

        // 로그인 안 되어 있으면 로그인 페이지로 리다이렉트
        if (loginUser == null) {
            return "redirect:/api/member/login";
        }

        // 해당 사용자의 게시물 목록을 가져오기
        MemberEntity member = memberRepository.findByIdx(loginUser);
        List<BoardEntity> boardList = boardService.getBoardListByMemberIdx(loginUser, "createdAt", "DESC");


        // boardList가 빈 리스트일 경우 처리
        if (boardList.isEmpty()) {
            boardList = new ArrayList<>();
        }

        // 개인 게시물(1:1 문의) 필터링
        List<BoardEntity> personalList = boardList.stream()
                .filter(b -> b.getGroup() != null && b.getGroup().getGroupIdx().equals(6L))
                .collect(Collectors.toList());

        List<BoardEntity> communityList = boardList.stream()
                .filter(b -> b.getGroup() != null && b.getGroup().getGroupIdx().equals(2L))
                .collect(Collectors.toList());

        // 로그인한 사용자 정보 조회
        MemberEntity memberEn = memberRepository.findByIdx(loginUser);
        model.addAttribute("member", memberEn);

        // 모델에 데이터 추가
        model.addAttribute("personalList", personalList);
        model.addAttribute("communityList", communityList);
        model.addAttribute("webtitle", "만화방초 | 내가 작성한 게시글");
        model.addAttribute("boardList", boardList);

        return "/board/myboard_page";
    }

    @PostMapping("/pq_proc")
    public String handleForm(@Valid CommonForm form,
                             BindingResult result,
                             Model model,
                             @RequestParam("board_type") Long boardType,
                             @RequestParam("group_idx") Long groupIdx,
                             @ModelAttribute BoardDTO boardDTO,
                             @ModelAttribute MemberDTO memberDTO,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            return "redirect:/board/personalquestion_page?board_type="+boardType+"&group_idx="+groupIdx;
        }

        try {
            boardService.processBoardForm(groupIdx, boardDTO, memberDTO);
            model.addAttribute("message", "질문을 성공적으로 보냈습니다!");
            return "redirect:/board/myboard";// 템플릿 렌더링
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/board/oftenquestion_page?page=1&board_type="+boardType+"&group_idx="+groupIdx;
        } catch (Exception e) {
            model.addAttribute("error", "서버에 문제가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/board/oftenquestion_page?page=1&board_type="+boardType+"&group_idx="+groupIdx;
        }
    }
    @RequestMapping("/personalquestion_view")
    public String personalquestion_view(@RequestParam("group_idx") Long groupIdx,
                                        @RequestParam("board_type") Long boardType,
                                        @RequestParam("idx") Long idx,
                                        Model model){

        BoardEntity board = boardRepository.findByIdx(idx);

        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);
        model.addAttribute("board", board);

        return "/board/personalquestion_view";

    }



    /*공지사항*/
    @RequestMapping("/notice")
    public String notice(){
        System.out.println(">>>>>>>>>>notice page<<<<<<<<<<");

        Long groupIdx = 1L;
        Long boardType = 0L;
        int page = 1;

        return "redirect:/board/notice_page?page="+page+"&board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/notice_page")
    public String notice_page(Model model,
                              @RequestParam("group_idx") Long groupIdx,
                              @RequestParam("board_type") Long boardType,
                              @RequestParam(value="page", defaultValue = "1") int page){
        System.out.println(">>>>>>>>>>noticepage page<<<<<<<<<<");

        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser != null) {
            MemberEntity member = memberRepository.findByIdx(loginUser);
            model.addAttribute("member", member);
        } else {
            model.addAttribute("member", null);  // 로그인되지 않은 경우
        }

        int itemsPerPage = 4;
        int groupSize = 3;

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "createdAt");
        Page<BoardEntity> paging = boardRepository.findByGroupIdx(groupIdx,pageable);

        int totalCount = (int) paging.getTotalElements();

        Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize,"link");

        int startNum = totalCount - ((page - 1) * itemsPerPage);
        model.addAttribute("startNum", startNum);

        model.addAttribute("pagination", pagination);
        model.addAttribute("link","/board/notice_page");
        model.addAttribute("webtitle", "만화방초 | 공지 사항");
        model.addAttribute("paging", paging);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);
        return "/board/notice_page";
    }
    @RequestMapping("/notice_view")
    public String notice_view(Model model,
                              @RequestParam("group_idx") Long groupIdx,
                              @RequestParam("board_type") Long boardType,
                              @RequestParam("idx") Long idx){
        System.out.println(">>>>>>>>>>noticeview page<<<<<<<<<<");

        BoardEntity board = boardRepository.findByIdx(idx);
        Long loginUser = Utility.getLoginUserIdx();

        if (loginUser != null) {
            MemberEntity member = memberRepository.findByIdx(loginUser);
            model.addAttribute("member", member);
        } else {
            model.addAttribute("member", null);  // 로그인되지 않은 경우
        }

        model.addAttribute("board", board);
        model.addAttribute("idx", idx);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);

        return"/board/notice_view";
    }
    @RequestMapping("/notice_write")
    public String notice_write(Model model,
                               @RequestParam("group_idx")Long groupIdx,
                               @RequestParam("board_type")Long boardType){
        //로그인 구현 후 수정
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        model.addAttribute("commonForm", new CommonForm());
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("today", today);
        return "board/notice_write";
    }
    @PostMapping("notice_proc")
    public String notice_proc(@Valid CommonForm form,
                              @ModelAttribute BoardEntity board,
                              BindingResult result,
                              @RequestParam("attachment") MultipartFile attachment,
                              @RequestParam("group_idx") Long groupIdx,
                              @RequestParam("board_type") Long boardType,
                              RedirectAttributes redirectAttributes,
                              Model model){
        /*유효성 검사*/
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "redirect:/board/notice_write";
        }

        try {
            boardService.saveBoard(board,groupIdx);
            utility.saveAttachment(attachment,board);
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "파일 업로드 실패");
            return "redirect:/board/notice_write";
        }

        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);

        return "redirect:/board/notice_page?page=1&board_type="+boardType+"&group_idx="+groupIdx;
    }



    /*커뮤니티*/
    @RequestMapping("/cmct")
    public String cmct(Model model){

        Long boardType = 0L;
        Long groupIdx = 2L;
        int page = 1;

        return "redirect:/board/cmct_page?page="+page+"&board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/cmct_page")
    public String cmct_page(@RequestParam("board_type") Long boardType,
                            @RequestParam("group_idx") Long groupIdx,
                            @RequestParam("page") int page,
                            Model model){

        int itemsPerPage = 4;
        int groupSize = 3;

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "createdAt");
        Page<BoardEntity> paging = boardRepository.findByGroupIdx(groupIdx,pageable);

        int totalCount = (int) paging.getTotalElements();

        Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize,"link");

        int startNum = totalCount - ( (page - 1) * itemsPerPage );
        model.addAttribute("startNum", startNum);

        List<Object[]> commentCounts = commentsRepository.countCommentsGroupedByBoardId();
        Map<Long, Integer> commentCountMap = new HashMap<>();

        for (Object[] row : commentCounts) {
            Long boardId = ((Number) row[0]).longValue();
            Integer count = ((Number) row[1]).intValue();
            commentCountMap.put(boardId, count);
        }

        model.addAttribute("pagination", pagination);
        model.addAttribute("commentCountMap", commentCountMap);
        model.addAttribute("link", "/board/cmct_page");
        model.addAttribute("webtitle", "만화방초 | 커뮤니티");
        model.addAttribute("paging", paging);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);

        return"/board/cmct_page";
    }
    @RequestMapping("/cmct_view")
    public String cmct_view(@RequestParam("group_idx") Long groupIdx,
                            @RequestParam("board_type") Long boardType,
                            @RequestParam("idx") Long idx,
                            @RequestParam(value = "editingCommentId", required = false) Long editingCommentId,
                            Model model){

        Long memberIdx = Utility.getLoginUserIdx();
        if (memberIdx == null) {
            return "redirect:/api/member/login";
        }


        MemberEntity member = memberRepository.findById(memberIdx).orElse(null);
        BoardEntity board = boardRepository.findByIdx(idx);
        List<CommentsEntity> commentsList = commentsRepository.findByBoard_idxWithMember(idx);

        /*정렬*/
        commentsList.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        model.addAttribute("editingCommentId", editingCommentId);
        model.addAttribute("commentsList", commentsList);
        model.addAttribute("board", board);
        model.addAttribute("member", member);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);

        return "/board/cmct_view";
    }
    @RequestMapping("/cmct_write")
    public String cmct_write(Model model,
                             @RequestParam("group_idx")Long groupIdx,
                             @RequestParam("board_type")Long boardType){

        Long memberIdx = Utility.getLoginUserIdx();
        if (memberIdx == null) {
            return "redirect:/api/member/login";
        }

        MemberEntity memberEn = memberRepository.findByIdx(memberIdx);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        model.addAttribute("member", memberEn);
        model.addAttribute("commonForm", new CommonForm());
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("today", today);
        return "board/cmct_write";
    }
    @PostMapping("/cmct_write_proc")
    public String cmct_write_proc(@Valid CommonForm form,
                                  @ModelAttribute BoardEntity board,
                                  BindingResult result,
                                  @RequestParam("attachment") MultipartFile attachment,
                                  @RequestParam("group_idx") Long groupIdx,
                                  @RequestParam("board_type") Long boardType,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        /*유효성 검사*/
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "redirect:/board/cmct_write";
        }
        try {

            boardService.saveBoard(board,groupIdx);
            utility.saveAttachment(attachment,board);

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "파일 업로드 실패");
            return "redirect:/board/cmct_write";
        }

        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);

        return "redirect:/board/cmct_page?page=1&board_type=" + boardType + "&group_idx=" + groupIdx;
    }
    @PostMapping("cmct_comment_proc")
    public String comment_proc(@ModelAttribute CommentsDTO commentsDTO,
                               RedirectAttributes redirectAttributes,
                               @RequestParam("groupIdx") Long groupIdx,
                               @RequestParam("boardIdx") Long idx,
                               @RequestParam("memberIdx") Long member,
                               Model model,
                               @RequestParam(value = "boardType", required = false) Long boardType){


        commentsService.saveComment(commentsDTO,member);

        Long memberIdx = commentsDTO.getMemberIdx();

        redirectAttributes.addAttribute("idx", commentsDTO.getBoardIdx());
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);
        return "redirect:/board/cmct_view?board_type=" + boardType + "&group_idx=" + groupIdx+"&idx="+idx+"&member="+memberIdx;
    }





    /*파일 다운로드*/
    @GetMapping("/file/download/{idx}")
    public ResponseEntity<Resource> filedownload(@PathVariable("idx") Long idx) throws FileNotFoundException {

        AttachmentEntity attachment = attachmentRepository.findByIdx(idx);

        if (attachment.getFilePath() == null || attachment.getFilePath().isEmpty()) {
            throw new RuntimeException("파일 경로가 존재하지 않습니다: " + attachment);
        }
        String uploadDir = "C:/Users/YJ/Documents/GitHub/public/data/";
        Resource resource = new FileSystemResource(uploadDir + attachment.getFilePath());


        if (!resource.exists()) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다: " + attachment.getFilePath());
        }

        String encodedFilename;
        try {
            encodedFilename = URLEncoder.encode(attachment.getFileName(), "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("파일명 인코딩 중 오류 발생", e);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFilename + "\"")
                .body(resource);
    }
    @GetMapping("/file/delete")
    public String filedelete(@RequestParam("board_idx") Long boardIdx,
                             @RequestHeader(value = "Referer", required = false) String referer) throws FileNotFoundException {
        utility.deleteAttachments(boardIdx);

        // 2) 이전 페이지가 있으면 거기로, 없으면 기본 경로로
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/board/cmct";  // 기본 복귀 URL
        }

    }

    /*삭제*/

    @RequestMapping("/delete")
    public String delete(BoardEntity board,
                         MemberEntity member,
                         @RequestParam("group_idx") Long groupIdx,
                         @RequestParam("board_type") Long boardType,
                         @RequestParam("memberIdx") Long memberIdx,
                         @RequestParam("idx") Long boardIdx,
                         @RequestParam("comments_idx") Long commentsIdx){

        String redirectUrl = "";

        if(groupIdx == 1 && boardType == 0){
            redirectUrl = "notice_page?page=1&board_type="+boardType+"&group_idx="+groupIdx;
        }
        else if(groupIdx == 2 && boardType == 0){
            redirectUrl = "cmct_page?page=1&board_type="+boardType+"&group_idx="+groupIdx;
        }
        else if(groupIdx == 3 && boardType == 1){
            redirectUrl = "event_page?board_type="+boardType+"&group_idx="+groupIdx;
        }
        else if(groupIdx == 4 && boardType == 1){
            redirectUrl = "gallery_page?board_type="+boardType+"&group_idx="+groupIdx;
        }
        else if(groupIdx == 5 && boardType == 2){
            redirectUrl = "oftenquestion_page?page=1&board_type="+boardType+"&group_idx="+groupIdx;
        }
        // 댓글이 아닌 게시글 삭제
        if (boardIdx >= 1 && commentsIdx == 0) {
            List<AttachmentEntity> attachments = attachmentRepository.findBoardsByBoard_Idx(boardIdx);
            if (attachments == null || attachments.isEmpty()) {
                boardService.deleteBoard(boardIdx);
            } else {
                utility.deleteAttachments(boardIdx);
                boardService.deleteBoard(boardIdx);
            }
        }

        // 댓글 삭제
        if (commentsIdx >= 1 && boardIdx >= 1) {
            commentsService.deleteComment(commentsIdx);
            return "redirect:/board/cmct_view?board_type=" + boardType + "&group_idx=" + groupIdx + "&idx=" + boardIdx + "&member=" + memberIdx;
        }
        return "redirect:/board/"+redirectUrl;
    }

    @GetMapping("search")
    public String search(@RequestParam("group_idx") Long groupIdx,
                         @RequestParam("board_type") Long boardType,
                         @RequestParam("keyword") String keyword,
                         @RequestParam(value="page", defaultValue="1") int page,
                         Model model){


        int itemsPerPage = 4;
        // (페이징 처리 위해 PageRequest 객체 생성)
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "createdAt");

        // Page<BoardDTO> 타입으로 결과 받기 (utility.searchByTitle를 수정해야 함)
        Page<BoardDTO> resultPage = utility.searchByTitle(keyword, groupIdx, boardType, pageable);

        int totalCount = (int) resultPage.getTotalElements();
        // 기존 페이징 UI 로직 재사용
        Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, 3, "link");
        model.addAttribute("pagination", pagination);

        // 전체 검색 결과(=totalCount) 기준으로 역순 번호 매기려면
        int startNum = totalCount - ((page - 1) * itemsPerPage);
        model.addAttribute("startNum", startNum);


        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);
        model.addAttribute("isSearch", true);
        model.addAttribute("paging", resultPage);
        model.addAttribute("keyword", keyword); // 검색어 유지
        return "/board/cmct_page";
    }

    @RequestMapping("/modify")
    public String modify(@RequestParam("group_idx") Long groupIdx,
                         @RequestParam("board_type") Long boardType,
                         @RequestParam("idx") Long idx,
                         @RequestParam("commentsIdx") Long comments,
                         Model model) {

        Long memberIdx = Utility.getLoginUserIdx();
        if (memberIdx == null) {
            return "redirect:/api/member/login";
        }

        MemberEntity member = memberRepository.findById(memberIdx).orElse(null);
        BoardEntity board = boardRepository.findByIdx(idx);
        Optional<AttachmentEntity> attachment = attachmentRepository.findByBoard_idx(idx);
        List<CommentsEntity> commentsList = commentsRepository.findByBoard_idxWithMember(idx);

        commentsList.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        model.addAttribute("commentsList", commentsList);
        model.addAttribute("attachment", attachment);
        model.addAttribute("board", board);
        model.addAttribute("member", member);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);

        if(comments != null && comments > 0){
            commentsRepository.findById(comments).ifPresent(c -> model.addAttribute("editingComment", c));
            model.addAttribute("comments", comments);
            return "/board/cmct_view";
        }

        String viewName = switch (groupIdx.intValue()) {
            case 1 -> "/board/notice_modify";
            case 2 -> "/board/cmct_modify";
            case 3 -> "/board/event_modify";
            case 4 -> "/board/gallery_modify";
            case 5 -> "/board/oftenquestion_modify";
            default -> "redirect:/";
        };

        return viewName;
    }

    @PostMapping("/modify_proc")
    public String modify(
            @ModelAttribute BoardEntity board,
            BindingResult bindingResult,
            @RequestParam("group_idx") Long groupIdx,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            @RequestParam(value = "deleteAttachmentFlag", defaultValue = "N") String deleteFlag,
            @RequestParam("board_type") Long boardType,
            @RequestParam("memberIdx") Long memberIdx,
            @RequestParam("boardIdx") Long boardIdx,
            @RequestParam("comments_idx") Long commentsIdx,
            @RequestParam(value = "startAt", required = false) String startAtStr,
            @RequestParam(value = "closedAt", required = false) String closedAtStr,
            @RequestParam(value = "commentContent", required = false) String commentContent
    ) throws IOException {

        // 1) base 경로 결정 (기존 코드 그대로)
        String base = switch (groupIdx.intValue()) {
            case 1 -> "notice_view";
            case 2 -> "cmct_view";
            case 3 -> "event_view";
            case 4 -> "gallery_view";
            case 5 -> "oftenquestion_view";
            default -> "cmct_page";
        };

        // 2) 이벤트 페이지의 날짜 파싱 (기존 코드 그대로)
        Date startAt = null;
        Date closedAt = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (startAtStr != null && !startAtStr.isEmpty()) {
            LocalDate localStartAt = LocalDate.parse(startAtStr, formatter);
            startAt = Date.from(localStartAt.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        if (closedAtStr != null && !closedAtStr.isEmpty()) {
            LocalDate localClosedAt = LocalDate.parse(closedAtStr, formatter);
            closedAt = Date.from(localClosedAt.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        // 3) “이미지 필수” 게시판인지 확인 (예: gallery_page)
        boolean isImageRequired = (groupIdx == 4);  // groupIdx == 4 → gallery_page 로 가정

        // 4) 기존 첨부파일 존재 여부 확인
        boolean hadExistingAttachment = boardService.existsAttachment(boardIdx);

        // 5) 서버 단 유효성 검사: 이미지가 필수인 경우
        if (isImageRequired) {
            // (1) 기존 파일이 있었고, deleteFlag=“Y” 이면서 새 파일도 없는 경우 → 오류
            if (hadExistingAttachment
                    && "Y".equalsIgnoreCase(deleteFlag)
                    && (attachment == null || attachment.isEmpty())) {
                bindingResult.rejectValue("attachment", "NotNull.gallery", "이미지를 반드시 등록해야 합니다.");
            }
            // (2) 기존 파일이 없었고, deleteFlag=N 이지만 attachment도 비어 있으면 → 오류
            if (!hadExistingAttachment
                    && (attachment == null || attachment.isEmpty())) {
                bindingResult.rejectValue("attachment", "NotNull.gallery", "이미지를 반드시 등록해야 합니다.");
            }
        }

        // 6) 유효성 오류가 있으면 폼으로 되돌려서 오류 메시지 출력
        if (bindingResult.hasErrors()) {
            return base + "/modify_form";
        }

        // 7) “삭제 플래그”가 Y일 때: 기존 첨부파일(메타 + 물리 파일) 삭제
        if ("Y".equalsIgnoreCase(deleteFlag) && hadExistingAttachment) {
            boardService.deleteAttachments(boardIdx);
            // 이제 DB 측 BoardEntity와 연관관계가 끊기면서 첨부파일 레코드는 사라졌습니다.
        }

        // 8) “자주 묻는 질문” 페이지 답변 분리 처리 (기존 로직 유지)
        if (groupIdx == 5
                && boardType == 2
                && commentsIdx == 0
                && boardIdx >= 1
                && (attachment == null || attachment.isEmpty())
                && !"Y".equalsIgnoreCase(deleteFlag))
        {
            boardService.modifyBoard(
                    boardIdx,
                    board.getTitle(),
                    board.getContent(),
                    board.getAnswerTitle(),
                    board.getAnswerContent(),
                    startAt,
                    closedAt
            );
            return "redirect:/board/list";
        }

        // 9) 댓글이 아닌 게시글 수정 처리 (기존 로직 + 새 파일 처리 포함)
        if (commentsIdx == 0 && boardIdx >= 1) {

            // 9-1) 공통 게시글 수정 처리
            boardService.modifyBoard(
                    boardIdx,
                    board.getTitle(),
                    board.getContent(),
                    board.getAnswerTitle(),
                    board.getAnswerContent(),
                    startAt,
                    closedAt
            );

            // 9-2) 새로 업로드된 파일이 있으면 덮어쓰기
            if (attachment != null && !attachment.isEmpty()) {
                // 만약 deleteFlag가 “Y”라면 위에서 이미 deleteAttachments()로 삭제했으니,
                // 새로 받은 파일을 저장하는 로직만 수행한다.
                boardService.modifyAttachment(attachment, boardIdx);

                // 9-3) deleteFlag=Y 이고 새 파일도 없으면 → 이미 deleteAttachments()로 파일과 메타를 삭제했으므로,
                //       DB 컬럼 상에서 Attachment 정보만 null 처리해도 충분하다.
            } else if ("Y".equalsIgnoreCase(deleteFlag)) {
                // (선택) 추가로 삭제 후 DB 컬럼만 클리어해야 한다면 이 메서드 사용
                boardService.clearAttachmentInfo(boardIdx);
            }
            // 9-4) deleteFlag=N 이고 attachment도 없으면 → 기존 파일 그대로 유지
        }


        // 댓글 수정 처리
        if (commentsIdx >= 1 && boardIdx >= 1 &&  (attachment == null || attachment.isEmpty())) {
            commentsService.modifyComment(commentsIdx, commentContent);
            return "redirect:/board/cmct_view?board_type=" + boardType +
                    "&group_idx=" + groupIdx +
                    "&idx=" + boardIdx +
                    "&member=" + memberIdx;
        }

        return "redirect:/board/" +
                UriComponentsBuilder.fromPath(base)
                        .queryParam("page", 1)
                        .queryParam("group_idx", groupIdx)
                        .queryParam("board_type", boardType)
                        .queryParam("idx", boardIdx)
                        .build();
    }
}