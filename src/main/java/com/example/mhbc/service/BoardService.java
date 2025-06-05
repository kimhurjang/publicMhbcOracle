package com.example.mhbc.service;

import com.example.mhbc.dto.AttachmentDTO;
import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.dto.CommentsDTO;
import com.example.mhbc.dto.MemberDTO;
import com.example.mhbc.entity.*;
import com.example.mhbc.repository.*;
import com.example.mhbc.util.Utility;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardGroupRepository boardGroupRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentsRepository commentsRepository;
    private String uploadDir = "C:/Users/YJ/Documents/GitHub/public/data/";


    // 게시글 조회 및 조회수 증가
    @Transactional
    public BoardEntity getBoardByIdx(long idx) {
        BoardEntity board = boardRepository.findByIdx(idx);

        int viewCnt =  0;

        if(board.getViewCnt() != null){
            viewCnt = board.getViewCnt();
        }

        if (board != null) {
            board.setViewCnt(viewCnt + 1);
            boardRepository.save(board);  // 조회수 증가 반영
        }
        return board;
    }

    // 그룹별 게시글 목록 조회
    public List<BoardEntity> getBoardListByGroupIdx(long groupIdx, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return boardRepository.findBoardsByGroupIdxSort(groupIdx, sort);
    }


    //같은 제목의 게시물 조회(자주 묻는 질문)
    public List<BoardEntity> getBoardListByTitle(String title){
        return boardRepository.findByTitle(title);
    }

    public void processBoardForm(long groupIdx, BoardDTO boardDTO, MemberDTO memberDTO) {
        // 1. 회원 정보 조회
        Long loginUser = Utility.getLoginUserIdx();
        MemberEntity member = memberRepository.findByIdx(loginUser);

        System.out.println("---------------------MEMBER = " + member.getName());

        // 2. 게시판 그룹 조회 (필요 시)
        Optional<BoardGroupEntity> optionalGroup = boardGroupRepository.findById((long) groupIdx);
        BoardGroupEntity group = optionalGroup.orElse(null); // 그룹이 없으면 null로 처리하거나 기본 그룹 처리

        // 3. DTO → Entity 변환
        BoardEntity board = boardDTO.toEntity(member, group);
        board.setCreatedAt(boardDTO.getCreatedAt()); // createdAt 수동 설정 시 필요
        board.setViewCnt(0); // 기본 조회수 0
        board.setRe(1);//1===답변가능한 게시물
        board.setRequest(0);//시작은 답변 전 상태

        // 4. 저장
        boardRepository.save(board);
    }


    /*DTO 변환*/
    public List<BoardDTO> getBoardDTOListByGroupIdx(Long groupIdx) {
        List<BoardEntity> boardEntities = boardRepository.findBoardsByGroupIdx(groupIdx);
        List<BoardDTO> dtoList = new ArrayList<>();
        AttachmentDTO ATdto = new AttachmentDTO();

        for (BoardEntity board : boardEntities) {
            BoardDTO dto = new BoardDTO();
            dto.setTitle(board.getTitle());
            dto.setContent(board.getContent());
            dto.setCreatedAt(board.getCreatedAt());

            AttachmentEntity attachment = attachmentRepository.findByBoard(board);
            if (attachment != null) {
                ATdto.setFileName(attachment.getFileName());
                ATdto.setFilePath(attachment.getFilePath());
            }

            dtoList.add(dto);
        }

        return dtoList;
    }




    /*게시물 업로드 서비스*/
    public void saveBoard(BoardEntity board, long groupIdx) throws IOException {
        board.setRe(1);
        BoardGroupEntity group = boardGroupRepository.findByGroupIdx(groupIdx);
        board.setGroup(group);

        // 임시: 로그인 사용자
        Long loginUser = Utility.getLoginUserIdx();

        board.setMember(memberRepository.findByIdx(loginUser));

        // 게시글 저장
        boardRepository.save(board);
    }


    /*게시물 삭제*/
    @Transactional
    public void deleteBoard(Long boardIdx) {

        // 2. 게시글 삭제
        boardRepository.deleteByIdx(boardIdx);
    }


    /**
     *
     *      수정
     *
     * */
    /*파일 재업로드*/
    @Transactional
    public void modifyBoard(Long boardIdx , String title, String content , String answerTitle , String answerContent,Date startAt,
                            Date closedAt){

     BoardEntity board = boardRepository.findByIdx(boardIdx);

        board.setUpdatedAt(new Date());
        board.setTitle(title);
        board.setContent(content);

        // FAQ인 경우, 답변 필드도 수정
        if (answerTitle != null && answerContent != null) {
            board.setAnswerTitle(answerTitle);
            board.setAnswerContent(answerContent);
        }

        if (startAt != null) {
            board.setStartAt(startAt);
        }
        if (closedAt != null) {
            board.setClosedAt(closedAt);
        }

    }
    @Transactional
    public void modifyAttachment(MultipartFile file, Long boardIdx) throws IOException {
        if (file == null || file.isEmpty()) {
            return;
        }

        // 1) 파일 저장
        String uuidFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File up = new File(uploadDir);
        if (!up.exists()) up.mkdirs();
        file.transferTo(new File(up, uuidFileName));

        // 2) BoardEntity 프록시 가져오기 (실제 조회 쿼리 지연)
        BoardEntity board = boardRepository.getReferenceById(boardIdx);

        // 3) 기존 첨부 있으면 가져오고, 없으면 새로 생성
        AttachmentEntity attach = attachmentRepository
                .findByBoard_idx(boardIdx)
                .orElseGet(() -> new AttachmentEntity());

        // 4) 연관관계 편의 메서드로 양쪽 세팅
        attach.setBoard(board);

        // 5) 파일 메타데이터 세팅
        attach.setFileName(file.getOriginalFilename());
        attach.setFilePath(uuidFileName);
        attach.setFileType(file.getContentType());
        attach.setFileSize((int) file.getSize());

        // 6) 저장
        attachmentRepository.save(attach);
    }


    /**
     *          myboard 사용자 idx 게시물 조회 정렬
     *
     * */
    public List<BoardEntity> getBoardListByMemberIdx(Long memberIdx, String sortField, String sortDir) {
        // Sort 객체 생성
        Sort sort = Sort.by(sortField);
        if ("DESC".equalsIgnoreCase(sortDir)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        // Repository 호출 (memberIdx 기준, Sort 적용)
        return boardRepository.findByMemberIdx(memberIdx, sort);
    }
    /**
     * 해당 boardIdx로 등록된 Attachment가 있으면 true, 없으면 false 반환
     */
    public boolean existsAttachment(long boardIdx) {
        return attachmentRepository.findByBoard_idx(boardIdx).isPresent();
    }

    /**
     * 1) 게시글(boardIdx)에 연결된 Attachment 한 건 조회
     * 2) 물리 파일 삭제 (uploadDir + filePath)
     * 3) BoardEntity와 AttachmentEntity 연관관계 제거(orphanRemoval=true라면 자동 삭제)
     */
    @Transactional
    public void deleteAttachments(long boardIdx) {
        // 1) AttachmentEntity 조회
        AttachmentEntity attachment = attachmentRepository
                .findByBoard_idx(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("첨부파일이 없습니다. boardIdx=" + boardIdx));

        // 2) 물리 파일 삭제
        String fullPath = uploadDir + attachment.getFilePath();
        File file = new File(fullPath);
        if (file.exists()) {
            if (!file.delete()) {
                System.out.println("파일 삭제 실패: " + file.getAbsolutePath());
            }
        } else {
            System.out.println("삭제하려는 파일이 존재하지 않음: " + file.getAbsolutePath());
        }

        // 3) 엔티티 연관관계 제거
        BoardEntity board = attachment.getBoard();
        board.setAttachment(null);
        boardRepository.save(board);

        // 만약 orphanRemoval 설정이 되어 있지 않다면 아래 주석을 해제하여 직접 삭제하세요.
        // attachmentRepository.delete(attachment);
    }

    /**
     * 물리 파일은 이미 다른 로직(deleteAttachments 등)에서 삭제된 상태이고,
     * DB 상에서만 BoardEntity의 attachment 필드를 null로 바꿔주고 싶을 때 사용합니다.
     */
    @Transactional
    public void clearAttachmentInfo(long boardIdx) {
        // 1) AttachmentEntity 조회
        AttachmentEntity attachment = attachmentRepository
                .findByBoard_idx(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 첨부파일이 없습니다. boardIdx=" + boardIdx));

        // 2) BoardEntity ↔ AttachmentEntity 연관관계 제거
        BoardEntity board = attachment.getBoard();
        board.setAttachment(null);
        boardRepository.save(board);

        // 3) AttachmentEntity 자체도 삭제 (orphanRemoval=true라면 board.setAttachment(null)만으로 충분)
        attachmentRepository.delete(attachment);
    }

}
