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

@Data
public class UserDetailsImpl implements UserDetails {

    private Optional<MemberEntity> member;

    public UserDetailsImpl(Optional<MemberEntity> member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
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
        System.out.println("----getPassword----");
        return member.map(MemberEntity::getPwd)
                .orElseThrow(() -> new IllegalStateException("비밀번호가 없습니다"));
    }

    @Override
    public String getUsername() {
        System.out.println("----getUsername----");
        return member.map(MemberEntity::getUserid)
                .orElseThrow(() -> new IllegalStateException("아이디가 없습니다"));
    }

    public Long getIdx() {
        System.out.println("----getIdx----");
        return member.map(MemberEntity::getIdx)
                .orElseThrow(() -> new IllegalStateException("유저(idx)가 없습니다"));
    }

    public Integer getGrade() {
        return member.map(MemberEntity::getGrade)
                .orElseThrow(() -> new IllegalStateException("등급(grade)이 없습니다"));
    }

    /**
     * 계정 만료 여부 반환
     * true: 만료 안됨 (계정 사용 가능)
     * false: 만료됨 (로그인 불가)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요에 따라 구현
    }

    /**
     * 계정 잠김 여부 반환
     * true: 잠기지 않음
     * false: 잠김 (로그인 불가)
     *
     * 회원 상태가 "정지"인 경우 잠긴 상태로 처리
     */
    @Override
    public boolean isAccountNonLocked() {
        return member.map(m -> {
            String status = m.getStatus();
            if (status == null) return true;
            return !status.equalsIgnoreCase("정지");
        }).orElse(true);
    }

    /**
     * 비밀번호 만료 여부 반환
     * true: 만료 안됨
     * false: 만료됨 (로그인 불가)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 필요에 따라 구현
    }

    /**
     * 계정 활성화 여부 반환
     * true: 활성화됨
     * false: 비활성화됨 (로그인 불가)
     *
     * 회원 상태가 "탈퇴"인 경우 비활성화 상태로 처리
     */
    @Override
    public boolean isEnabled() {
        return member.map(m -> {
            String status = m.getStatus();
            if (status == null) return true;
            return !status.equalsIgnoreCase("탈퇴");
        }).orElse(false);
    }
}
