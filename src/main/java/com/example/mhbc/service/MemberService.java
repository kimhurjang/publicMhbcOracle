package com.example.mhbc.service;

import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    // 이름과 전화번호로 아이디 찾기
    public MemberEntity findMemberByNameAndPhone(String name, String mobile) {
        return memberRepository.findByNameAndMobile(name, mobile);
    }

    // 이름과 아이디로 비밀번호 찾기
    public MemberEntity findMemberByNameAndUserid(String name, String userid) {
        return memberRepository.findByNameAndUserid(name, userid);
    }

    // 회원 여러 명 삭제 메서드
    @Transactional
    public void deleteMembersByIds(List<Long> memberIds) {
        memberRepository.deleteAllByIdInBatch(memberIds);
    }
}
