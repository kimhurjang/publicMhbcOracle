

package com.example.mhbc.service;

import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;/*final == 생성자 주입법. Required랑 세트member;*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("----UserDetailService----");
        System.out.println("----username : " + username);

        MemberEntity member = memberRepository.findByUserid(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        System.out.println(member.toString());

        return new UserDetailsImpl(Optional.of(member));
    }

}