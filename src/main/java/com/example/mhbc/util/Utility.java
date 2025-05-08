package com.example.mhbc.util;

import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.entity.AttachmentEntity;
import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.repository.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
    private String uploadDir = "D:/SpringProject/data/";
    /**
    * 파일
    * */
    /*파일 업로드*/
    public void saveAttachment(MultipartFile attachment, BoardEntity board) throws IOException {
        if (!attachment.isEmpty()) {
            String uuidFileName = UUID.randomUUID().toString() + "_" + attachment.getOriginalFilename();
            //uploadDir = "D:/SpringProject/data/";

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destination = new File(uploadDir, uuidFileName);
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
        try {

            List<AttachmentEntity> attachments = attachmentRepository.findByBoard_Idx(boardIdx);

            for (AttachmentEntity attachment : attachments) {
                String filepath = attachment.getFilePath();
                System.out.println("파일 경로: " + filepath);
                String dir = "D:/SpringProject/data/";

                // 실제 파일 삭제
                File file = new File(dir + filepath);

                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("파일 삭제 성공: " + filepath);
                    } else {
                        System.out.println("파일 삭제 실패: " + filepath);
                    }
                } else {
                    System.out.println("파일이 존재하지 않음: " + filepath);
                }
            }

            // 1. 첨부파일 삭제
            attachmentRepository.deleteByIdx(boardIdx);
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
    페이징
    */
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
    * 검색
    * */
    public List<BoardDTO> searchByTitle(String keyword, long groupIdx, Long boardType) {
        keyword = keyword.trim();
        System.out.println("Searching for: " + keyword + " with groupIdx: " + groupIdx + " and boardType: " + boardType);
        List<BoardEntity> results = boardRepository.findByTitleContainingAndGroup_GroupIdxAndGroup_BoardType(keyword, groupIdx, boardType);
        System.out.println("Search Results: " + results.size());
        return results.stream()
                .map(BoardDTO::fromEntity)
                .collect(Collectors.toList());
    }


}
