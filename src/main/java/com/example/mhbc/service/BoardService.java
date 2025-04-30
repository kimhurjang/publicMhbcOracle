package com.example.mhbc.service;

import com.example.mhbc.dto.AttachmentDTO;
import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.dto.CommentsDTO;
import com.example.mhbc.dto.MemberDTO;
import com.example.mhbc.entity.*;
import com.example.mhbc.repository.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardGroupRepository boardGroupRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentsRepository commentsRepository;
    private String uploadDir = "D:/SpringProject/data/";


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
    public List<BoardEntity> getBoardListByGroupIdx(long groupIdx) {

        return boardRepository.findBoardsByGroupIdx(groupIdx);
    }

    //같은 제목의 게시물 조회(자주 묻는 질문)
    public List<BoardEntity> getBoardListByTitle(String title){
        return boardRepository.findByTitle(title);
    }

    public void processBoardForm(long groupIdx, BoardDTO boardDTO, MemberDTO memberDTO) {
        // 1. 회원 정보 조회
        Optional<MemberEntity> optionalMember = memberRepository.findByNameAndEmail(memberDTO.getName().trim(), memberDTO.getEmail().trim());
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("회원 정보가 존재하지 않습니다.");
        }
        MemberEntity member = optionalMember.get();

        // 2. 게시판 그룹 조회 (필요 시)
        Optional<BoardGroupEntity> optionalGroup = boardGroupRepository.findById((long) groupIdx);
        BoardGroupEntity group = optionalGroup.orElse(null); // 그룹이 없으면 null로 처리하거나 기본 그룹 처리

        // 3. DTO → Entity 변환
        BoardEntity board = boardDTO.toEntity(member, group);
        board.setCreatedAt(boardDTO.getCreatedAt()); // createdAt 수동 설정 시 필요
        board.setViewCnt(0); // 기본 조회수 0

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
        board.setMember(memberRepository.findByIdx(1L));

        // 게시글 저장
        boardRepository.save(board);
    }


    /*게시물 삭제*/
    @Transactional
    public void deleteBoard(long boardIdx) {

//        List<AttachmentEntity> attachments = attachmentRepository.findByBoard_Idx(boardIdx);
//
//        for (AttachmentEntity attachment : attachments) {
//            String filepath = attachment.getFilePath();
//            System.out.println("파일 경로: " + filepath);
//            String dir = "D:/SpringProject/data/";
//
//            // 실제 파일 삭제
//            File file = new File(dir + filepath);
//            if (file.exists()) {
//                if (file.delete()) {
//                    System.out.println("파일 삭제 성공: " + filepath);
//                } else {
//                    System.out.println("파일 삭제 실패: " + filepath);
//                }
//            } else {
//                System.out.println("파일이 존재하지 않음: " + filepath);
//            }
//        }
//
//        // 1. 첨부파일 삭제
//        attachmentRepository.deleteByIdx(boardIdx);

        // 2. 게시글 삭제
        boardRepository.deleteByIdx(boardIdx);
    }


}
