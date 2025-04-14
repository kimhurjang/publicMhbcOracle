package com.example.mhbc.service;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UserPasswordEncoder implements PasswordEncoder {


    @Override
    public String encode(CharSequence rawPassword) {
        System.out.println("----encode----");

        System.out.println("----rawPassword :" + rawPassword.toString());

        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        System.out.println("----matches----");
        System.out.println("----rawPassword : " + rawPassword);//내가 페이지에서 입력한 password 값
        System.out.println("----encodedPassword : " + encodedPassword);//userdetailsImpl 의 getPassword값(DB)

        return rawPassword.toString().equals(encodedPassword);

    }
}
