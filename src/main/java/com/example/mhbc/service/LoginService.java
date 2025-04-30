package com.example.mhbc.service;

import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;  // BCryptPasswordEncoder import
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class LoginService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberEntity login(String userid, String pwd) {
        Optional<MemberEntity> memberOpt = memberRepository.findByUserid(userid);

        if (memberOpt.isPresent()) {
            MemberEntity member = memberOpt.get();

            // 데이터베이스에 저장된 비밀번호와 입력된 비밀번호 비교
            if (bCryptPasswordEncoder.matches(pwd, member.getPwd())) {
                return member; // 로그인 성공
            }
        }
        return null; // 로그인 실패
    }
}
