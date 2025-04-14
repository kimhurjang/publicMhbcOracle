package com.example.mhbc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping({"/" , "/home"})
    public String index(){
        System.out.println(">>>>>>>>>>index page<<<<<<<<<<");
        return "index";
    }
    @RequestMapping("/login")
    public String login(){
        System.out.println(">>>>>>>>>>login page<<<<<<<<<<");
        return "login";
    }
    @RequestMapping("/admin")
    public String admin(){
        System.out.println(">>>>>>>>>>admin page<<<<<<<<<<");
        return "admin";
    }
    @RequestMapping("/wedding")
    public String wedding(){
        System.out.println(">>>>>>>>>>wedding page<<<<<<<<<<");
        return "wedding";
    }
    @RequestMapping("/booking")
    public String booking(){
        System.out.println(">>>>>>>>>>booking page<<<<<<<<<<");
        return "booking";
    }


}
