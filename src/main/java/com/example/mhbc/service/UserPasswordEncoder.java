package com.example.mhbc.service;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UserPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        System.out.println(">>>>>> encode <<<<<<<<<<");
        System.out.println("rawPassword1:" + rawPassword);
        System.out.println("rawPassword2:" + rawPassword.toString());
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        System.out.println(">>>>>> matches <<<<<<<<<<");
        System.out.println("rawPassword:" + rawPassword);
        System.out.println("encodedPassword:" + encodedPassword);

        return rawPassword.toString().equals(encodedPassword);
    }

}
