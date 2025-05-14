package com.example.mhbc.controller.admin;


import com.example.mhbc.dto.BoardDTO;
import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.repository.BoardGroupRepository;
import com.example.mhbc.repository.BoardRepository;
import com.example.mhbc.service.BoardService;
import com.example.mhbc.service.admin.AdminBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/board")
public class AdminBoardController {



    @RequestMapping("/list")
    public String board_page(Model model){


        model.addAttribute("webtitle", "게시물관리 | 리스트");

        return "/admin/board/list";
    }

    @RequestMapping("/group_list")
    public String group_list(Model model){
        model.addAttribute("webtitle", "게시물관리 | 리스트");
        return "/admin/board/group_list";
    }






}
