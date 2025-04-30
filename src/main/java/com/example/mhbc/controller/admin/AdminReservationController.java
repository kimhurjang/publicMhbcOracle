package com.example.mhbc.controller.admin;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.service.admin.AdminReservationService;
import com.example.mhbc.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  // 예약 리스트 출력
  @GetMapping("/list")
  public String reservationList(@RequestParam(value = "page", defaultValue = "1") int page,
                                Model model) {

    int itemsPerPage = 10; // 페이지당 10개
    int groupSize = 5;     // 페이징 그룹 5개
    String link = "/admin/reservation/list"; // 현재 페이지 링크

    Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "idx");
    Page<ReservationDTO> paging = adminReservationService.findAll(pageable);

    int totalCount = (int) paging.getTotalElements();
    Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize, link);

    model.addAttribute("paging", paging);
    model.addAttribute("pagination", pagination);
    model.addAttribute("link", link);

    return "admin/reservation/list";
  }

  // 예약 상태 변경 저장 처리 (기존 방식)
  @PostMapping("/status")
  public String updateReservationStatus(
    @RequestParam(value = "statuses") List<String> statuses,
    @RequestParam(value = "statuses", defaultValue = "") List<String> updatedStatuses) {

    adminReservationService.updateStatuses(updatedStatuses);
    return "redirect:/admin/reservation/list";
  }

  // 예약 상태 변경 저장 처리 (Ajax 방식)
  @PostMapping("/status/ajax")
  @ResponseBody
  public String updateStatusAjax(@RequestBody List<Map<String, String>> updates) {
    adminReservationService.updateStatusesByAjax(updates);
    return "상태가 성공적으로 변경되었습니다.";
  }

  // 예약 상세보기
  @GetMapping("/view")
  public String reservationView(@RequestParam Long idx, Model model) {
    model.addAttribute("reservation", adminReservationService.findById(idx));
    return "/admin/reservation/view";
  }

  // 관리자 메모 수정
  @PostMapping("/memoUpdate")
  public String updateAdminNote(@RequestParam("idx") Long idx,
                                @RequestParam("adminNote") String adminNote,
                                @ModelAttribute("loginId") String loginId) {

    adminReservationService.updateAdminNote(idx, adminNote, loginId);
    return "redirect:/admin/reservation/view?idx=" + idx;
  }

}
