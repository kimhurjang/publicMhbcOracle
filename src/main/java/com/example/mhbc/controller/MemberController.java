package com.example.mhbc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

  @RequestMapping("/login")
  public String login(){
    System.out.println(">>>>>>>>>>login page<<<<<<<<<<");
    return "member/login";
  }

}
