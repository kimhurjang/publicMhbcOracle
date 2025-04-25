package com.example.mhbc.controller.admin;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.service.admin.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AdminReservationController
 * - 관리자 예약 현황 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reservation")
public class AdminReservationController {

  private final AdminReservationService adminReservationService;

  // 리스트 출력
  @GetMapping("/list")
  public String reservationList(Model model) {
    List<ReservationDTO> reservations = adminReservationService.findAll();
    model.addAttribute("reservations", reservations);
    return "admin/reservation/list";
  }

  // 상태 변경 저장 처리
  @PostMapping("/status")
  public String updateReservationStatus(
    @RequestParam(value = "statuses") List<String> statuses,
    @RequestParam(value = "statuses", defaultValue = "") List<String> updatedStatuses) {

    adminReservationService.updateStatuses(updatedStatuses);
    return "redirect:/admin/reservation/list";
  }

  @PostMapping("/status/ajax")
  @ResponseBody
  public String updateStatusAjax(@RequestBody List<Map<String, String>> updates) {
    adminReservationService.updateStatusesByAjax(updates);
    return "상태가 성공적으로 변경되었습니다.";
  }

}
