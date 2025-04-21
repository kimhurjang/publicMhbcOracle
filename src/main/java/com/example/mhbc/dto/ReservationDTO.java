package com.example.mhbc.dto;

import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.ReservationEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private Long idx; // 예약번호
    private String name; // 예약자 성함
    private String eventType; // 행사 종류

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date eventDate; // 행사 예정일
    private Integer guestCnt; // 행사 인원수
    private String mealType; // 식사 종류
    private String flower; // 꽃장식
    private String contactTime; // 연락 가능한 시간
    private String note; // 기타 문의
    private String mobile; // 연락처
    private String status; // 예약 상태
    private Integer totalAmount; // 총금액
    private Date createdAt; // 작성일
    private Date updatedAt; // 수정일
    private Long memberIdx; // 회원 FK
    private Long hallIdx; // 홀 FK
    private String hallName; // 홀 이름

    // DTO → Entity (수정용까지 반영)
    public ReservationEntity toEntity(MemberEntity member, HallEntity hall) {
        return ReservationEntity.builder()
                .idx(idx) // ← 이 줄이 핵심 (등록 시 null, 수정 시 식별자로 사용)
                .name(name)
                .eventType(eventType)
                .eventDate(eventDate)
                .guestCnt(guestCnt)
                .mealType(mealType)
                .flower(flower)
                .contactTime(contactTime)
                .note(note)
                .mobile(mobile)
                .status(status)
                .totalAmount(totalAmount)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .member(member)
                .hall(hall)
                .build();
    }
}
