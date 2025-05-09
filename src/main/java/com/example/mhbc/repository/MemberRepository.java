package com.example.mhbc.repository;

import com.example.mhbc.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    // 아이디로 회원 찾기
    Optional<MemberEntity> findByUserid(String userid);

    // 이메일로 회원 찾기
    Optional<MemberEntity> findByEmail(String email);

    // 아이디 존재 여부 확인
    boolean existsByUserid(String userid);  // 여기 추가

    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);

    // 이름과 전화번호로 회원 찾기
    MemberEntity findByNameAndMobile(String name, String mobile);

    // 이름과 아이디로 회원 찾기 (비밀번호 찾기 용도)
    MemberEntity findByNameAndUserid(String name, String userid);

    // 이름과 이메일로 회원 찾기
    MemberEntity findByNameAndEmail(String name, String email);

    // IDX로 회원 찾기
    MemberEntity findByIdx(long idx);

    // 아이디와 이름으로 회원 찾기 (비밀번호 찾기 용도)
    MemberEntity findByUseridAndName(String userid, String name);
}
