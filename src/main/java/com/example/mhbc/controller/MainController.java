package com.example.mhbc.controller;

import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final BoardRepository boardRepository;

    @RequestMapping({"/" , "/home"})
    public String index(Model model){
        System.out.println(">>>>>>>>>>index page<<<<<<<<<<");

        List<BoardEntity> event = boardRepository.findByGroupGroupIdx(3L);
        List<BoardEntity> board = boardRepository.findByGroupGroupIdx(1L);

        model.addAttribute("event", event.subList(0, Math.min(2, event.size())));
        model.addAttribute("board", board.subList(0, Math.min(2, event.size())));

        return "index";
    }
    @RequestMapping("/admin")
    public String admin(){
        System.out.println(">>>>>>>>>>admin page<<<<<<<<<<");
        return "admin";
    }

    @RequestMapping("/map")
    public String map(){

        return "map";
    }


}
