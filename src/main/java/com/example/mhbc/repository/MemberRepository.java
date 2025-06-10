package com.example.mhbc.repository;

import com.example.mhbc.dto.MemberSnsDto;
import com.example.mhbc.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {



    // 아이디로 회원 찾기
    Optional<MemberEntity> findByUserid(String userid);

    // 이메일로 회원 찾기
    Optional<MemberEntity> findByEmail(String email);

    // 아이디 존재 여부 확인
    boolean existsByUserid(String userid);

    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);

    // 이름과 전화번호로 회원 찾기
    MemberEntity findByNameAndMobile(String name, String mobile);

    // 이름과 아이디로 회원 찾기 (비밀번호 찾기 용도)
    MemberEntity findByNameAndUserid(String name, String userid);

    // 이름과 이메일로 회원 찾기
    MemberEntity findByNameAndEmail(String name, String email);

    boolean existsByMobile(String mobile);

    // IDX로 회원 찾기
    MemberEntity findByIdx(long idx);

    // 아이디와 이름으로 회원 찾기 (비밀번호 찾기 용도)
    MemberEntity findByUseridAndName(String userid, String name);

    // 상태 + 키워드 검색 (이름 또는 아이디 포함)
    @Query("SELECT m FROM MemberEntity m " +
            "WHERE (:status IS NULL OR m.status = :status) " +
            "AND (:keyword IS NULL OR m.name LIKE %:keyword% OR m.userid LIKE %:keyword%)")
    Page<MemberEntity> findByStatusAndKeyword(@Param("status") String status,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);

    // Member와 연관된 SNS 정보 포함해서 DTO로 조회
    @Query("SELECT new com.example.mhbc.dto.MemberSnsDto(" +
            "m.idx, m.userid, m.name, m.telecom, m.mobile, m.email, m.nickname, m.grade, m.status, m.createdAt, m.updatedAt, " +
            "s.snsType, s.snsId, s.snsEmail, s.snsName, s.snsMobile, s.connectedAt) " +
            "FROM MemberEntity m LEFT JOIN m.snsList s " +
            "WHERE (:status IS NULL OR m.status = :status) " +
            "AND (:keyword IS NULL OR m.name LIKE %:keyword% OR m.userid LIKE %:keyword%)")
    Page<MemberSnsDto> findAllWithSnsByStatusAndKeyword(@Param("status") String status,
                                                        @Param("keyword") String keyword,
                                                        Pageable pageable);
}
