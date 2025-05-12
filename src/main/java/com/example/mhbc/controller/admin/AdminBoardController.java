package com.example.mhbc.controller.admin;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/board")
public class AdminBoardController {

    @RequestMapping("/list")
    public String board_page(Model model){



        model.addAttribute("webtitle", "게시물관리 | 리스트");

        return "/admin/board/list";
    }

}
