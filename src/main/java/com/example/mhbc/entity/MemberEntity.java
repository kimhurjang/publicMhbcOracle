package com.example.mhbc.entity;

import com.example.mhbc.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "MEMBER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;

  @Column(unique = true)
  private String userid;

  private String pwd;

  @NotBlank(message = "이름은 필수입니다.")
  private String name;

  @NotBlank(message = "통신사를 선택해주세요.")
  private String telecom;

  @NotBlank(message = "휴대폰 번호는 필수입니다.")
  @Pattern(regexp = "\\d{3}-\\d{3,4}-\\d{4}", message = "휴대폰 번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
  private String mobile;

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  private String nickname;

  @Builder.Default
  private Integer grade = 0;

  @NotBlank(message = "회원 상태를 선택해주세요.")
  private String status;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "CREATE_AT")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "UPDATE_AT")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date updatedAt;

  @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<SnsEntity> snsList;

  @PrePersist
  protected void onCreate() {
    Date now = new Date();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = new Date();
  }

  // 이 메서드는 선택 사항, 없어도 됨. 사용할 경우 변환 처리 필수
  public MemberDTO toDTO() {
    return MemberDTO.builder()
            .idx(idx)
            .userid(userid)
            .pwd(pwd)
            .name(name)
            .telecom(telecom)
            .mobile(mobile)
            .email(email)
            .nickname(nickname)
            .grade(grade)
            .status(status)
            .createdAt(createdAt)  // 변환 처리
            .updatedAt(updatedAt)
            .build();
  }
}
