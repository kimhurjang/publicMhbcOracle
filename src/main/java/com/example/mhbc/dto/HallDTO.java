package com.example.mhbc.dto;

import com.example.mhbc.entity.HallEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallDTO {
  private Long idx; // 홀 번호
  private String name;
  private Integer capacity;
  private Integer price;

  public HallEntity toEntity() {
    HallEntity.HallEntityBuilder builder = HallEntity.builder()
      .name(name)
      .capacity(capacity)
      .price(price);

    // 수정 시에만 idx 포함
    if (idx != null) {
      builder.idx(idx);
    }

    return builder.build();

  }
}
