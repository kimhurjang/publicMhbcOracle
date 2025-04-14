//
// 
// 엔티티 만들고 활성화
// 
// package com.example.mhbc.Service;
//
//import com.example.mhbc.Entity.MemberEntity;
//import com.example.mhbc.Repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor//자동 생성자
//@Service
//public class UserDetailServiceImpl implements UserDetailsService {
//
//    private final MemberRepository memberRepository;//final == 생성자 주입법. Required랑 세트member;
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("----UserDetailService----");
//        System.out.println("----username : " + username);
//
//        MemberEntity member = memberRepository.findByUserid(username);//DB에서 username과 일치하는 userid찾기
//
//        if(member == null){
//            System.out.println("없는 아이디");
//            return null;
//        }
//
//        System.out.println(member.toString());
//
//        return new UserDetailsImpl(member);
//    }
//}
