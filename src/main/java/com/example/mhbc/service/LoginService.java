package com.example.mhbc.service;

import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private MemberRepository memberRepository;
    public MemberEntity login(String userid, String pwd) {
        Optional<MemberEntity> memberOpt = memberRepository.findByUserid(userid);

        if (memberOpt.isEmpty()) {
            System.out.println("login fail: 아이디 없음");
            return null;
        }

        MemberEntity member = memberOpt.get();
        System.out.println("DB status: [" + member.getStatus() + "]");

        if (!member.getPwd().equals(pwd)) {
            System.out.println("login fail: 비밀번호 틀림");
            return null;
        }

        String status = member.getStatus() == null ? "" : member.getStatus().trim();
        System.out.println("password match, status: [" + status + "]");

        if ("탈퇴".equalsIgnoreCase(status) || "정지".equalsIgnoreCase(status)) {
            System.out.println("login blocked due to status: " + status);
            return null;
        }

        System.out.println("login success");
        return member;
    }

}
