package com.example.mhbc.controller;

import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.repository.BoardRepository;
import com.example.mhbc.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final BoardRepository boardRepository;

    @RequestMapping({"/" , "/home", "/index"})
    public String index(Model model){
        System.out.println(">>>>>>>>>>index page<<<<<<<<<<");

        List<BoardEntity> event = boardRepository.findByGroupGroupIdx(3L);
        List<BoardEntity> board = boardRepository.findByGroupGroupIdx(1L);

        try {
            model.addAttribute("event", event.subList(0, Math.min(2, event.size())));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("이벤트 리스트 오류: " + e.getMessage());
            model.addAttribute("event", new ArrayList<>()); // 빈 리스트 전달
        }

        try {
            model.addAttribute("board", board.subList(0, Math.min(2, board.size())));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("게시판 리스트 오류: " + e.getMessage());
            model.addAttribute("board", new ArrayList<>()); // 빈 리스트 전달
        }

        return "index";
    }
    @RequestMapping("/content")
    public String admin(){
        System.out.println(">>>>>>>>>>admin page<<<<<<<<<<");
        return "content";
    }
    @RequestMapping("/admin/admin_index")
    public String admin_index() {
        System.out.println(">>>>>>>>>>admin admin_index page<<<<<<<<<<");
        return "/admin/admin_index";
    }

    @RequestMapping("/page/about")
    public String about(){
        System.out.println(">>>>>>>>>>about page<<<<<<<<<<");
        return "/page/about";
    }
    @RequestMapping("/page/map")
    public String map(){
        return "/page/map";
    }
    @RequestMapping("/page/place")
    public String place(){
        return "/page/place";
    }
    @RequestMapping("/page/rental")
    public String rental(){
        return "/page/rental";
    }

}
