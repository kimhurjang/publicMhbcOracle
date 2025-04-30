package com.example.mhbc.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sns")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "sns_type")
    private String snsType;

    @Column(name = "sns_id")
    private String snsId;

    @Column(name = "sns_email")
    private String snsEmail;

    @Column(name = "sns_name")
    private String snsName;

    @Column(name = "connected_at")
    private LocalDateTime connectedAt;  // LocalDateTime을 사용하면 JPA가 자동으로 처리
}
