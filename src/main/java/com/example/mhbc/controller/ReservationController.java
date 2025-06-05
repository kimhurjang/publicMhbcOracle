package com.example.mhbc.controller;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.entity.ScheduleBlockEntity;
import com.example.mhbc.repository.ReservationRepository;
import com.example.mhbc.repository.ScheduleBlockRepository;
import com.example.mhbc.service.HallService;
import com.example.mhbc.service.ReservationService;
import com.example.mhbc.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 예약 컨트롤러
 * - 사용자 예약 기능: 등록, 목록, 상세, 수정, 삭제
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

  private final ReservationService reservationService;
  private final HallService hallService;

  private final ReservationRepository reservationRepository;
  private final ScheduleBlockRepository scheduleBlockRepository;

  @GetMapping("/")
  public String mainPage(Model model){

    List<ScheduleBlockEntity> blocks = scheduleBlockRepository.findAll();
    model.addAttribute("blocks", blocks); // 차단 리스트 넘김

    return "reservation/index";
  }

  // 예약 등록 폼 화면
  @GetMapping("/form")
  public String showForm(
          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
          @RequestParam(required = false) String time,
          Model model) {
    ReservationDTO reservation = new ReservationDTO(); // or 조회한 DTO

    reservation.setEventDate(date);
    reservation.setEventTimeSelect(time.replace("시", "")); // "14시" → "14"

    model.addAttribute("webtitle", "만화방초 | 상담예약");
    model.addAttribute("reservation", reservation);
    model.addAttribute("halls", reservationService.getHallList());

    //System.out.println("eventDate = " + reservation.getEventDate());
    //System.out.println("eventTimeSelect = " + reservation.getEventTimeSelect());

    return "reservation/form";
  }

  // 예약 저장 처리
  @PostMapping("/save")
  public String saveReservation(@ModelAttribute ReservationDTO reservationDTO) {
    //System.out.println(">>>>>>>>> memberIdx: " + reservationDTO.getMemberIdx()); // 전달된 값 확인
    reservationService.save(reservationDTO);
    return "redirect:/reservation/list";
  }

  /**
   * 로그인한 사용자의 예약현황 페이지
   * 로그인하지 않으면 로그인 페이지로 리다이렉트
   */
  @GetMapping("/list")
  public String showList(@RequestParam(value="page", defaultValue="1") int page,
                         Model model) {

    int itemsPerPage = 5; // 한 페이지당 5개씩 출력
    int groupSize = 3;    // 페이징 그룹 크기
    String link = "/reservation/list"; // 현재 페이지 링크

    Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "idx");

    // 로그인하지 않은 경우 예외 → 전역 리다이렉트 처리
    Page<ReservationDTO> paging = reservationService.findByLoginUserPage(pageable);

    int totalCount = (int) paging.getTotalElements();
    Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize, "link");

    model.addAttribute("paging", paging);
    model.addAttribute("pagination", pagination);
    model.addAttribute("link", link);
    model.addAttribute("webtitle", "만화방초 | 예약 현황");

    return "reservation/list";
  }

  // 예약 상세보기 화면
  @GetMapping("/view")
  public String viewReservation(@RequestParam Long idx, Model model) {
    model.addAttribute("webtitle", "만화방초 | 예약 상세보기");

    model.addAttribute("reservation", reservationService.findById(idx));
    return "reservation/view";
  }

  // 예약 수정 폼 화면
  @GetMapping("/edit")
  public String editReservation(@RequestParam Long idx, Model model) {
    model.addAttribute("webtitle", "만화방초 | 예약 수정하기");

    ReservationDTO dto = reservationService.findById(idx);
    model.addAttribute("reservation", dto); // 기존 예약 정보
    model.addAttribute("halls", hallService.getAllHalls()); // 홀 목록
    return "reservation/form"; // form.html 재사용
  }

  // 예약 수정 처리
  @PostMapping("/update")
  public String updateReservation(@ModelAttribute ReservationDTO reservationDTO,
                                  @ModelAttribute("loginId") String loginId) {

    reservationService.update(reservationDTO, loginId);
    return "redirect:/reservation/view?idx=" + reservationDTO.getIdx();
  }

  // 예약 삭제 처리
  @PostMapping("/delete")
  public String deleteReservation(@RequestParam Long idx) {
    reservationService.delete(idx);
    return "redirect:/reservation/list";
  }

  //달력
  @GetMapping("/calendar")
  public String showReservationCalendar(Model model) {
    List<ScheduleBlockEntity> blocks = scheduleBlockRepository.findAll();
    model.addAttribute("blocks", blocks); // 차단 리스트 넘김

    return "reservation/calendar";
  }
}
