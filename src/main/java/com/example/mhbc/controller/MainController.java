package com.example.mhbc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping({"/", "/home"})
    public String index(Model model) {
        System.out.println(">>>>>>>>>>index page<<<<<<<<<<");
        model.addAttribute("title", "만화방초");
        return "index";
    }
    @RequestMapping("/content")
    public String contentLayout() {
        System.out.println(">>>>>>>>>>content page<<<<<<<<<<");
        return "content";
    }
    @RequestMapping("/admin")
    public String admin() {
        System.out.println(">>>>>>>>>>admin page<<<<<<<<<<");
        return "admin";
    }

}
