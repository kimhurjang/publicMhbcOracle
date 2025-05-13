package com.example.mhbc.service;

import com.example.mhbc.entity.MemberEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Data /*어노테이션을 통해 기본적인 getter, setter, equals, hashCode, toString 자동 생성*/
public class UserDetailsImpl implements UserDetails {  // Spring Security의 UserDetails 인터페이스 구현

    private Optional<MemberEntity> member;

    public UserDetailsImpl(Optional<MemberEntity> member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한을 반환하는 메서드
        // 여기서는 "ROLE_USER" 권한을 가진 SimpleGrantedAuthority 객체를 리스트로 반환
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        //유저 권한
        member.ifPresent(m -> {
            if (m.getGrade() == 1) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            } else if (m.getGrade() == 10) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        // 사용자의 비밀번호를 반환하는 메서드
        System.out.println("----getPassword----");
        return member.map(MemberEntity::getPwd)
                .orElseThrow(() -> new IllegalStateException("비밀번호가 없습니다"));
    }

    @Override
    public String getUsername() {
        // 사용자의 아이디(Username)를 반환하는 메서드
        System.out.println("----getUsername----");
        return member.map(MemberEntity::getUserid)
                .orElseThrow(() -> new IllegalStateException("아이디가 없습니다"));
    }

    public Long getIdx() {
        // 사용자의 아이디(Username)를 반환하는 메서드
        System.out.println("----getUsername----");
        return member.map(MemberEntity::getIdx)
                .orElseThrow(() -> new IllegalStateException("유저(idx)가 없습니다"));
    }
    public Integer getGrade() {
        return member.map(MemberEntity::getGrade)
                .orElseThrow(() -> new IllegalStateException("등급(grade)이 없습니다"));
    }


}