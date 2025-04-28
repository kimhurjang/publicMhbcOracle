package com.example.mhbc.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Utility {

    /*
    페이징
    */
    @Getter
    public static class Pagination {

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

        private String link;

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

}
