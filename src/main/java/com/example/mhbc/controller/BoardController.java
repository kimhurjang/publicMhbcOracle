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
import jakarta.validation.Valid;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        long boardType = 1;
        long groupIdx = 4;
        return "redirect:/board/gallery_page?board_type=" + boardType + "&group_idx=" + groupIdx;
    }
    @RequestMapping("/gallery_page")
    public String gallery_page(@RequestParam("board_type") long boardType,
                               @RequestParam("group_idx") long groupIdx,
                               Model model) {

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx);

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
    public String gallery_iew(@RequestParam("idx") long idx,
                              @RequestParam("group_idx") long groupIdx,
                              @RequestParam("board_type") long boardType,
                              Model model) {

        BoardEntity board = boardService.getBoardByIdx(idx);
        model.addAttribute("boardType", boardType);
        model.addAttribute("board", board);
        model.addAttribute("groupIdx", groupIdx);

        return "board/gallery_view";
    }
    @RequestMapping("/gallery_write")
    public String gallery_write(@RequestParam("group_idx") long groupIdx,
                                @RequestParam("board_type") long boardType,
                                Model model){

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx);

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
                               @RequestParam("group_idx") long groupIdx,
                               @RequestParam("board_type") long boardType,
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
        long boardType = 1;
        long groupIdx = 3;
        return "redirect:/board/event_page?board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/event_page")
    public String event_Page(@RequestParam("board_type") long boardType,
                             @RequestParam("group_idx") long groupIdx,
                             Model model) {

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx);

        model.addAttribute("webtitle", "만화방초 | 이벤트");
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardList", boardList);

        return "board/event_page";
    }
    @RequestMapping("/event_view")
    public String event_view(@RequestParam("idx") long idx,
                             @RequestParam("group_idx") long groupIdx,
                             @RequestParam("board_type") long boardType,
                             Model model) {

        BoardEntity board = boardService.getBoardByIdx(idx);
        model.addAttribute("board", board);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);

        return "board/event_view";
    }
    @RequestMapping("/event_write")
    public String event_write(@RequestParam("group_idx") long groupIdx,
                              @RequestParam("board_type") long boardType,
                              Model model){

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx);

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
                             @RequestParam("group_idx") long groupIdx,
                             @RequestParam("board_type") long boardType,
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
        long boardType = 2;
        long groupIdx = 5;
        int page = 1;
        return "redirect:/board/oftenquestion_page?page="+page+"&board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/oftenquestion_page")
    public String oftenquestion_page(@RequestParam("board_type") long boardType,
                                     @RequestParam("group_idx") long groupIdx,
                                     @RequestParam("page") int page,
                                     Model model) {

        int itemsPerPage = 4;
        int groupSize = 3;

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "idx");
        Page<BoardEntity> paging = boardRepository.findByGroupIdx(groupIdx,pageable);

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
    public String oftenquestion_view(@RequestParam("idx") long idx,
                                     @RequestParam("title") String title,
                                     @RequestParam("board_type") long boardType,
                                     @RequestParam("group_idx") long groupIdx,
                                     Model model) {

        List<BoardEntity> boardList = boardService.getBoardListByTitle(title);

        model.addAttribute("boardList", boardList);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("idx", idx);
        model.addAttribute("title", title);

        return "board/oftenquestion_view";
    }


    /*1대1문의*/
    @RequestMapping("/personalquestion")
    public String personalquestion_page(Model model) {
        System.out.println(">>>>>>>>>>personalquestionpage page<<<<<<<<<<");

        long boardType = 2;
        long groupIdx = 6;

        return "redirect:/board/personalquestion_page?board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/personalquestion_page")
    public String personalquestion_page(@RequestParam("board_type") long boardType,
                                        @RequestParam("group_idx") long groupIdx,
                                        Model model) {

        List<BoardEntity> boardList = boardService.getBoardListByGroupIdx(groupIdx);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        model.addAttribute("webtitle", "만화방초 | 1대1 질문");
        model.addAttribute("commonForm", new CommonForm());
        model.addAttribute("today", today);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardList", boardList);

        return "board/personalquestion_page";
    }
    @RequestMapping("myboard_page")
    public String myboard_page(@RequestParam("board_type") long boardType,
                                      @RequestParam("group_idx") long groupIdx,
                                      @RequestParam("member") long memberIdx,
                                      Model model){

        Optional<MemberEntity> member = memberRepository.findById(1L);
        List<BoardEntity> boardList = boardRepository.findByMemberIdx(1L);

        model.addAttribute("webtitle", "만화방초 | 내가 작성한 게시글");
        model.addAttribute("boardList", boardList);
        model.addAttribute("member", member.get());
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);

        return "board/myboard_page";
    }
    @PostMapping("/pq_proc")
    public String handleForm(@Valid CommonForm form,
                             BindingResult result,
                             Model model,
                             @RequestParam("board_type") long boardType,
                             @RequestParam("group_idx") long groupIdx,
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
            return "redirect:/board/oftenquestion_page?page=1&board_type="+boardType+"&group_idx="+groupIdx; // 템플릿 렌더링
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/board/oftenquestion_page?page=1&board_type="+boardType+"&group_idx="+groupIdx;
        } catch (Exception e) {
            model.addAttribute("error", "서버에 문제가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/board/oftenquestion_page?page=1&board_type="+boardType+"&group_idx="+groupIdx;
        }
    }



    /*공지사항*/
    @RequestMapping("/notice")
    public String notice(){
        System.out.println(">>>>>>>>>>notice page<<<<<<<<<<");

        long groupIdx = 1;
        long boardType = 0;
        int page = 1;

        return "redirect:/board/notice_page?page="+page+"&board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/notice_page")
    public String notice_page(Model model,
                              @RequestParam("group_idx") long groupIdx,
                              @RequestParam("board_type") long boardType,
                              @RequestParam(value="page", defaultValue = "1") int page){
        System.out.println(">>>>>>>>>>noticepage page<<<<<<<<<<");


        int itemsPerPage = 4;
        int groupSize = 3;

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "idx");
        Page<BoardEntity> paging = boardRepository.findByGroupIdx(groupIdx,pageable);

        int totalCount = (int) paging.getTotalElements();

        Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize,"link");


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
                              @RequestParam("group_idx") long groupIdx,
                              @RequestParam("board_type") long boardType,
                              @RequestParam("idx") long idx){
        System.out.println(">>>>>>>>>>noticeview page<<<<<<<<<<");

        BoardEntity board = boardRepository.findByIdx(idx);

        model.addAttribute("board", board);
        model.addAttribute("idx", idx);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);

        return"/board/notice_view";
    }
    @RequestMapping("/notice_write")
    public String notice_write(Model model,
                               @RequestParam("group_idx")long groupIdx,
                               @RequestParam("board_type")long boardType){
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
                              @RequestParam("group_idx") long groupIdx,
                              @RequestParam("board_type") long boardType,
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

        long boardType = 0;
        long groupIdx = 2;
        int page = 1;

        return "redirect:/board/cmct_page?page="+page+"&board_type="+boardType+"&group_idx="+groupIdx;
    }
    @RequestMapping("/cmct_page")
    public String cmct_page(@RequestParam("board_type") long boardType,
                            @RequestParam("group_idx") long groupIdx,
                            @RequestParam("page") int page,
                            Model model){

        int itemsPerPage = 4;
        int groupSize = 3;

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "idx");
        Page<BoardEntity> paging = boardRepository.findByGroupIdx(groupIdx,pageable);

        int totalCount = (int) paging.getTotalElements();

        Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize,"link");

        model.addAttribute("pagination", pagination);
        model.addAttribute("link", "/board/cmct_page");
        model.addAttribute("webtitle", "만화방초 | 커뮤니티");
        model.addAttribute("paging", paging);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);

        return"/board/cmct_page";
    }
    @RequestMapping("/cmct_view")
    public String cmct_view(@RequestParam("group_idx") long groupIdx,
                            @RequestParam("board_type") long boardType,
                            @RequestParam("idx") long idx,
                            @RequestParam("member") long memberIdx,
                            Model model){

        MemberEntity member = memberRepository.findById(memberIdx).orElse(null);
        BoardEntity board = boardRepository.findByIdx(idx);
        List<CommentsEntity> commentsList = commentsRepository.findByBoard_idxWithMember(idx);

        /*정렬*/
        commentsList.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        model.addAttribute("commentsList", commentsList);
        model.addAttribute("board", board);
        model.addAttribute("member", member);
        model.addAttribute("boardType", boardType);
        model.addAttribute("groupIdx", groupIdx);

        return "/board/cmct_view";
    }
    @RequestMapping("/cmct_write")
    public String cmct_write(Model model,
                             @RequestParam("group_idx")long groupIdx,
                             @RequestParam("board_type")long boardType){
        //로그인 구현 후 수정
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        BoardEntity board = new BoardEntity();

        model.addAttribute("commonForm", new CommonForm());
        model.addAttribute("boardType", boardType);
        model.addAttribute("board", board);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("today", today);
        return "board/cmct_write";
    }
    @PostMapping("/cmct_write_proc")
    public String cmct_write_proc(@Valid CommonForm form,
                                  @ModelAttribute BoardEntity board,
                                  BindingResult result,
                                  @RequestParam("attachment") MultipartFile attachment,
                                  @RequestParam("group_idx") long groupIdx,
                                  @RequestParam("board_type") long boardType,
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
                           @RequestParam("groupIdx") long groupIdx,
                           @RequestParam("boardIdx") long idx,
                           Model model,
                           @RequestParam(value = "boardType", required = false) long boardType){


    commentsService.saveComment(commentsDTO,1L);

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
        String uploadDir = "D:/SpringProject/data/";
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

    /*삭제*/

    @RequestMapping("/delete")
    public String delete(BoardEntity board,
                         MemberEntity member,
                         AttachmentEntity attachment,
                         @RequestParam("group_idx") long groupIdx,
                         @RequestParam("board_type") long boardType,
                         @RequestParam("memberIdx") long memberIdx,
                         @RequestParam("idx") long boardIdx,
                         @RequestParam("comments_idx") long commentsIdx){

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

        if(boardIdx >= 1 && commentsIdx == 0) {
            utility.deleteAttachments(boardIdx);
            boardService.deleteBoard(boardIdx);
        }
        if(commentsIdx >= 1 && boardIdx >= 1){
            commentsService.deleteComment(commentsIdx);
            return "redirect:/board/cmct_view?board_type=" + boardType + "&group_idx=" + groupIdx+"&idx="+boardIdx+"&member="+memberIdx;

        }
        return "redirect:/board/"+redirectUrl;
    }

    @GetMapping("search")
    public String search(@RequestParam("group_idx") long groupIdx,
                         @RequestParam("board_type") long boardType,
                         @RequestParam("keyword") String keyword,
                         Model model){

        List<BoardDTO> result;
        result = utility.searchByTitle(keyword, groupIdx, boardType);

        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("boardType", boardType);
        model.addAttribute("isSearch", true);
        model.addAttribute("paging", result);
        model.addAttribute("keyword", keyword); // 검색어 유지
        return "/board/cmct_page";
    }

}