package com.example.mhbc.controller;

import com.example.mhbc.dto.BoardDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class ApiController {

  @RequestMapping("/member/idcheck")
  //아래는 예
  public BoardDTO idcheck(@RequestParam("id") String id) {

    BoardDTO board = new BoardDTO();
    return board;
  }
}
