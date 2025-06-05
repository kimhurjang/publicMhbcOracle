package com.example.mhbc.controller.admin;

import com.example.mhbc.dto.HallDTO;
import com.example.mhbc.service.admin.AdminHallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자 전용 홀 관리 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/hall")
public class AdminHallController {

  private final AdminHallService adminHallService;

  @GetMapping("/list")
  public String list(Model model) {
    model.addAttribute("webtitle", "예약관리 | 홀 현황");

    List<HallDTO> hallList = adminHallService.findAll();
    //System.out.println(">>>>>>>>>" + hallList.toString());
    model.addAttribute("hallList", hallList);
    return "admin/hall/list";
  }

  @GetMapping("/form")
  public String form(@RequestParam(required = false) Long idx, Model model) {
    model.addAttribute("webtitle", "예약관리 | 홀 등록");

    HallDTO hall = (idx != null) ? adminHallService.findById(idx) : new HallDTO();
    model.addAttribute("hall", hall);
    return "admin/hall/form";
  }

  @PostMapping("/save")
  public String save(@ModelAttribute HallDTO hallDTO) {
    adminHallService.save(hallDTO);
    return "redirect:/admin/hall/list";
  }

  @GetMapping("/delete")
  public String delete(@RequestParam Long idx) {
    adminHallService.delete(idx);
    return "redirect:/admin/hall/list";
  }
}
