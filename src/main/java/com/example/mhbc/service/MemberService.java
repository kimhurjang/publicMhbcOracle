package com.example.mhbc.service;

import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.entity.SnsEntity;
import com.example.mhbc.repository.MemberRepository;
import com.example.mhbc.repository.SnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SnsRepository snsRepository;

    // 이름과 전화번호로 아이디 찾기
    public MemberEntity findMemberByNameAndPhone(String name, String mobile) {
        return memberRepository.findByNameAndMobile(name, mobile);
    }

    // 이름과 아이디로 비밀번호 찾기
    public MemberEntity findMemberByNameAndUserid(String name, String userid) {
        return memberRepository.findByNameAndUserid(name, userid);
    }

    // 휴면 계정 해지 (정지 -> 정상)
    @Transactional
    public boolean reactivate(String userid) {
        System.out.println("[reactivate] 요청 userid: " + userid);
        Optional<MemberEntity> memberOpt = memberRepository.findByUserid(userid);

        if (memberOpt.isPresent()) {
            MemberEntity member = memberOpt.get();
            String status = member.getStatus();
            System.out.println("[reactivate] 현재 상태: '" + status + "'");

            if (status != null && "정지".equals(status.trim())) {
                member.setStatus("정상");
                memberRepository.save(member);
                System.out.println("[reactivate] 상태 변경 완료 -> '" + member.getStatus() + "'");
                return true;
            } else {
                System.out.println("[reactivate] 상태가 '정지'가 아니므로 변경 불가");
            }
        } else {
            System.out.println("[reactivate] userid에 해당하는 회원 없음");
        }
        return false;
    }

    // 회원 탈퇴 처리
    @Transactional
    public boolean withdrawMember(String userid) {
        Optional<MemberEntity> memberOpt = memberRepository.findByUserid(userid);
        if (memberOpt.isEmpty()) {
            return false;  // 회원이 없으면 false 리턴
        }

        MemberEntity member = memberOpt.get();

        // SNS 로그인 회원인지 확인 (SNS 테이블에 존재하는지 확인)
        Optional<SnsEntity> snsOpt = Optional.ofNullable(snsRepository.findByMemberIdx(member.getIdx()));
        if (snsOpt.isPresent()) {
            // SNS 회원이면 SNS 정보 삭제
            snsRepository.delete(snsOpt.get());
        }

        // member status를 "탈퇴"로 변경
        member.setStatus("탈퇴");
        memberRepository.save(member);

        return true;
    }
}
