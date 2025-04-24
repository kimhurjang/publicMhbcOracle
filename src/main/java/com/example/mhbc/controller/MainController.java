package com.example.mhbc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping({"/" , "/home"})
    public String index(Model model){
        System.out.println(">>>>>>>>>>index page<<<<<<<<<<");
        model.addAttribute("title", "만화방초");//페이지별 타이틀 설정.(디폴트값==기본 제목)
        return "index";
    }

    @RequestMapping("/admin")
    public String admin(){
        System.out.println(">>>>>>>>>>admin page<<<<<<<<<<");
        return "admin";
    }
    @RequestMapping("/gallery")
    public String gallery(){
        System.out.println(">>>>>>>>>>gallery page<<<<<<<<<<");
        return "board/gallery";
    }



}
