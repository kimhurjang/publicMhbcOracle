package com.example.mhbc.controller;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 예약 컨트롤러
 * - 사용자 예약 기능: 등록, 목록, 상세, 수정, 삭제
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

  // 서비스 주입 (생성자 주입 방식 - @RequiredArgsConstructor 사용)
  private final ReservationService reservationService;

  /**
   * 예약 등록 폼 화면
   * - 비어있는 ReservationDTO 객체를 모델에 담아 뷰에 전달
   */
  @GetMapping("/form")
  public String showForm(Model model) {
    model.addAttribute("reservation", new ReservationDTO()); // 폼 바인딩용 객체
    return "reservation/form"; // templates/reservation/form.html
  }

  /**
   * 예약 등록 처리
   * - 사용자가 입력한 예약 정보를 저장
   */
  @PostMapping("/save")
  public String saveReservation(@ModelAttribute ReservationDTO reservationDTO) {
    reservationService.save(reservationDTO); // DTO → Entity → 저장
    return "redirect:/reservation/list"; // 저장 후 목록으로 이동
  }

  /**
   * 예약 목록 화면
   * - 전체 예약 목록을 조회하여 리스트 출력
   */
  @GetMapping("/list")
  public String showList(Model model) {
    model.addAttribute("reservations", reservationService.findAll()); // 전체 예약 조회
    return "reservation/list"; // templates/reservation/list.html
  }

  /**
   * 예약 상세보기
   * - 특정 예약 ID에 해당하는 정보 조회
   */
  @GetMapping("/view/{id}")
  public String viewReservation(@PathVariable Long id, Model model) {
    model.addAttribute("reservation", reservationService.findById(id)); // 단건 조회
    return "reservation/view"; // templates/reservation/view.html
  }

  /**
   * 예약 수정 폼
   * - 기존 예약 데이터를 조회하여 수정 폼에 바인딩
   */
  @GetMapping("/edit/{id}")
  public String editReservation(@PathVariable Long id, Model model) {
    model.addAttribute("reservation", reservationService.findById(id)); // 수정 대상 조회
    return "reservation/edit"; // templates/reservation/edit.html
  }

  /**
   * 예약 수정 처리
   * - 사용자가 수정한 예약 내용을 저장
   */
  @PostMapping("/update")
  public String updateReservation(@ModelAttribute ReservationDTO reservationDTO) {
    reservationService.update(reservationDTO); // 기존 데이터 덮어쓰기
    return "redirect:/reservation/view/" + reservationDTO.getIdx(); // 수정 후 상세보기 이동
  }

  /**
   * 예약 삭제 처리
   * - 예약 ID에 해당하는 예약 정보 삭제
   */
  @GetMapping("/delete/{id}")
  public String deleteReservation(@PathVariable Long id) {
    reservationService.delete(id); // 삭제 처리
    return "redirect:/reservation/list"; // 삭제 후 목록 이동
  }
}
