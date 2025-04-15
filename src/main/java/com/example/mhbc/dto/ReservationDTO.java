package com.example.mhbc.dto;

import com.example.mhbc.entity.HallEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.ReservationEntity;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private String name;
    private String eventType;
    private Date eventDate;
    private Integer guestCnt;
    private String mealType;
    private String flower;
    private String contactTime;
    private String note;
    private String userComment;
    private String status;
    private Integer totalAmount;
    private Date createdAt;
    private Long memberIdx;
    private Long hallIdx;

    public ReservationEntity toEntity(MemberEntity member, HallEntity hall) {
        return ReservationEntity.builder()
            .name(name)
            .eventType(eventType)
            .eventDate(eventDate)
            .guestCnt(guestCnt)
            .mealType(mealType)
            .flower(flower)
            .contactTime(contactTime)
            .note(note)
            .userComment(userComment)
            .status(status)
            .totalAmount(totalAmount)
            .member(member)
            .hall(hall)
            .build();
    }
}
