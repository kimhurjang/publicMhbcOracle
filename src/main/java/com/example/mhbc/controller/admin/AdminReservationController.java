package com.example.mhbc.controller.admin;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.dto.ReservationSearchCondition;
import com.example.mhbc.repository.HallRepository;
import com.example.mhbc.repository.ReservationRepository;
import com.example.mhbc.service.admin.AdminReservationService;
import com.example.mhbc.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
  private final ReservationRepository reservationRepository;
  private final HallRepository hallRepository;

  // 예약 리스트 출력
  @GetMapping("/list")
  public String reservationList(
    @RequestParam(value = "page", defaultValue = "1") int page,
    @RequestParam(required = false) String searchType,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String startDate,
    @RequestParam(required = false) String endDate,
    @RequestParam(required = false) List<String> status,
    @RequestParam(required = false) List<String> hallIdx,
    Model model
  ) {
    model.addAttribute("webtitle", "예약관리 | 예약현황");
    model.addAttribute("hallList", hallRepository.findAll()); //홀 리스트

    // 조건 없이 진입했는지 여부
    boolean isEmptySearch = (searchType == null && keyword == null &&
      startDate == null && endDate == null &&
      status == null && hallIdx == null);

    int itemsPerPage = 10;
    int groupSize = 5;
    String link = "/admin/reservation/list";
    Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "idx");

    if (isEmptySearch) {
      // 전체 리스트 출력
      Page<ReservationDTO> paging = adminReservationService.findAll(pageable);
      int totalCount = (int) paging.getTotalElements();
      Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize, link);

      model.addAttribute("paging", paging);
      model.addAttribute("pagination", pagination);
      model.addAttribute("link", link);

      // 검색조건 비움
      model.addAttribute("searchType", null);
      model.addAttribute("keyword", null);
      model.addAttribute("startDate", null);
      model.addAttribute("endDate", null);
      model.addAttribute("status", Collections.emptyList());
      model.addAttribute("hallIdx", Collections.emptyList());

      return "admin/reservation/list";
    }

    // 검색 조건 있는 경우
    ReservationSearchCondition condition = new ReservationSearchCondition();
    condition.setSearchType(searchType);
    condition.setKeyword(keyword);
    condition.setStartDate(startDate);
    condition.setEndDate(endDate);
    condition.setStatus(status);
    condition.setHallIdx(hallIdx);

    Page<ReservationDTO> paging = adminReservationService.findByCondition(condition, pageable);
    int totalCount = (int) paging.getTotalElements();
    Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize, link);

    model.addAttribute("paging", paging);
    model.addAttribute("pagination", pagination);
    model.addAttribute("link", link);

    // 기존 조건 유지
    model.addAttribute("searchType", searchType);
    model.addAttribute("keyword", keyword);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("status", status != null ? status : Collections.emptyList());
    model.addAttribute("hallIdx", hallIdx != null ? hallIdx : Collections.emptyList());

    return "admin/reservation/list";
  }


  // 예약 상태 변경 저장 처리 (기존 방식)
  @PostMapping("/status")
  public String updateReservationStatus(@RequestParam(value = "statuses") List<String> statuses) {
    adminReservationService.updateStatuses(statuses);
    return "redirect:/admin/reservation/list";
  }

  // 예약 상태 변경 저장 처리 (Ajax 방식)
  @PostMapping("/status/ajax")
  @ResponseBody
  public ResponseEntity<String> updateStatusAjax(@RequestBody List<Map<String, String>> updates) {
    try {
      adminReservationService.updateStatusesByAjax(updates);
      return ResponseEntity.ok("상태가 성공적으로 변경되었습니다.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage()); // ← 오직 메시지만 전달
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
    }
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
