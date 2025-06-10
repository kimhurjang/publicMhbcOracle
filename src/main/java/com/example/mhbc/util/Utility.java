package com.example.mhbc.util;

import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.dto.MemberDTO;
import com.example.mhbc.entity.AttachmentEntity;
import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.repository.*;
import com.example.mhbc.service.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Utility {
    private final BoardRepository boardRepository;
    private final BoardGroupRepository boardGroupRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentsRepository commentsRepository;
    private String baseDir = "C:/Users/YJ/Documents/GitHub/public/data/";
    /**
    * 파일
    * */
    /*파일 업로드*/
    public void saveAttachment(MultipartFile attachment, BoardEntity board) throws IOException {
        if (!attachment.isEmpty()) {
            String uuidFileName = UUID.randomUUID().toString() + "_" + attachment.getOriginalFilename();

            File directory = new File(baseDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destination = new File(baseDir, uuidFileName);
            attachment.transferTo(destination);

            // 상대 경로만 DB에 저장
            String relativePath = uuidFileName;


            // AttachmentEntity 생성 및 설정
            AttachmentEntity attachmentEntity = new AttachmentEntity();
            attachmentEntity.setFilePath(relativePath); // 상대 경로
            attachmentEntity.setFileName(attachment.getOriginalFilename());
            attachmentEntity.setFileType(attachment.getContentType());
            attachmentEntity.setFileSize((int) attachment.getSize());

            // 파일과 게시글 연결
            attachmentEntity.setBoard(board);
            attachmentRepository.save(attachmentEntity);

            // 게시글에 첨부파일 정보 반영
            board.setAttachment(attachmentEntity);
            boardRepository.save(board); // 게시글과 첨부파일 연결 반영
        }
    }
    /*파일 삭제*/
    @Transactional
    public void deleteAttachments(long boardIdx) {
        // 1) 게시글(boardIdx)에 연결된 Attachment 한 건 조회
        AttachmentEntity attachment = attachmentRepository
                .findByBoard_idx(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("첨부파일이 없습니다. boardIdx=" + boardIdx));

        // 2) 물리 파일 삭제
        File file = new File(baseDir, attachment.getFilePath());
        if (file.exists()) {
            if (!file.delete()) {
                System.out.println("파일 삭제 실패: {}" + file.getAbsolutePath());
            }
        } else {
            System.out.println("파일이 존재하지 않음: {}"+ file.getAbsolutePath());
        }

        // 3) 엔티티 연관관계 제거
        BoardEntity board = attachment.getBoard();
        board.setAttachment(null);   // orphanRemoval=true 이므로 이 한 줄로 DB에서도 삭제

        // 4) (선택) 영속성 컨텍스트 강제 플러시
        // entityManager.flush();
    }

    /*
    *   페이징
    * */
    @Getter
    public static class Pagination {

        private final String link;
        private int page;        // 현재 페이지
        private int size;        // 페이지당 데이터 수
        private int totalCount;  // 전체 데이터 수
        private int totalPage;   // 전체 페이지 수
        private int offset;      // 오프셋

        private int groupSize;   // 그룹 크기
        private int currentGroupStart; // 현재 그룹 시작 페이지
        private int currentGroupEnd;   // 현재 그룹 끝 페이지

        private List<Integer> pageList; // 페이지 리스트

        // 버튼 노출 여부
        private boolean hasPrev; // 이전 버튼 노출 여부
        private boolean hasNext; // 다음 버튼 노출 여부
        private boolean hasFirst; // 처음 버튼 노출 여부
        private boolean hasLast;  // 마지막 버튼 노출 여부

        public Pagination(int page, int size, int totalCount, int groupSize, String link) {
            this.page = page < 1 ? 1 : page;
            this.size = size;
            this.totalCount = totalCount;
            this.groupSize = groupSize;
            this.link = link;

            this.totalPage = (int) Math.ceil((double) totalCount / size);
            if (totalPage == 0) totalPage = 1;
            if (this.page > totalPage) this.page = totalPage;

            this.offset = (this.page - 1) * size;

            int currentGroup = (this.page - 1) / groupSize;
            this.currentGroupStart = currentGroup * groupSize;
            this.currentGroupEnd = Math.min(currentGroupStart + groupSize - 1, totalPage - 1);

            // 페이지 번호 리스트 생성
            this.pageList = new ArrayList<>();
            for (int i = currentGroupStart; i <= currentGroupEnd; i++) {
                pageList.add(i);
            }

            // 버튼 노출 조건 계산
            this.hasFirst = page > 1;
            this.hasPrev = currentGroupStart > 0;
            this.hasNext = currentGroupEnd < totalPage - 1;
            this.hasLast = page < totalPage;
        }
    }

    /*
    *   검색
    * */
    public Page<BoardDTO> searchByTitle(
            String keyword,
            long groupIdx,
            Long boardType,
            Pageable pageable
    ) {
        // 1) 검색어 공백 제거
        keyword = keyword.trim();

        // 2) Repository의 페이징 버전을 호출
        Page<BoardEntity> pageResult = boardRepository
                .findByTitleContainingAndGroup_GroupIdxAndGroup_BoardType(
                        keyword, groupIdx, boardType, pageable
                );

        // 3) Entity → DTO 매핑
        return pageResult.map(BoardDTO::fromEntity);
    }


    /**
     *  로그인 유저 정보 찾기
     * */
    /*
    *   로그인 유저 idx 가져오기(일반)
    * */
    public static Long getLoginUserIdx() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getIdx();
        }

        return null;
    }
    /*
     * 현재 로그인한 사용자 UserDetailsImpl 전체 객체 반환(객체 보완 필요)
     */
    public static UserDetailsImpl getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }

        return null;
    }

    /**
     * 현재 로그인한 사용자 정보를 MemberDTO 형태로 반환
     * 로그인하지 않은 경우 AccessDeniedException을 발생시킴
     */
    public static MemberDTO getLoginMemberDTO() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 로그인되지 않은 경우 예외 발생 (비회원 상태)
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        // 로그인한 사용자 정보가 UserDetailsImpl 타입일 경우 DTO로 변환
        if (auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            MemberDTO dto = new MemberDTO();
            dto.setIdx(userDetails.getIdx());           // 회원 IDX
            dto.setUserid(userDetails.getUsername());   // 사용자 ID
            dto.setGrade(userDetails.getGrade());       // 회원 등급
            return dto;
        }

        return null;
    }



}
