package com.example.mhbc.repository;

import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.SnsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SnsRepository extends JpaRepository<SnsEntity, Long> {
    Optional<SnsEntity> findBySnsId(String snsId);

    List<SnsEntity> findBySnsMobile(String snsMobile);

    // 회원 idx로 SNS 단건 조회
    SnsEntity findByMemberIdx(Long memberIdx);

    // 회원 idx 목록으로 SNS 리스트 조회
    List<SnsEntity> findByMemberIdxIn(List<Long> memberIdxList);
    // snsId와 memberIdx로 SNS 존재 여부 확인
    boolean existsBySnsIdAndMemberIdx(String snsId, Long memberIdx);

    boolean existsBySnsIdAndMember(String snsId, MemberEntity member);

}
