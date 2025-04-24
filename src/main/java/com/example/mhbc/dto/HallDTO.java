package com.example.mhbc.dto;

import com.example.mhbc.entity.HallEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallDTO {
    private String name;
    private Integer capacity;
    private Integer price;

    public HallEntity toEntity() {
        return HallEntity.builder()
            .name(name)
            .capacity(capacity)
            .price(price)
            .build();
    }
}
