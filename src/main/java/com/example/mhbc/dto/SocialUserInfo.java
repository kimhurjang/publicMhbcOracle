package com.example.mhbc.dto;


public class SocialUserInfo {
    private String id;
    private String email;
    private String nickname;

    public SocialUserInfo(String id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

    public SocialUserInfo() {

    }

    // getter들 추가
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}
