package com.example.mhbc.controller;

import com.example.mhbc.dto.ReservationDTO;
import com.example.mhbc.entity.ReservationEntity;
import com.example.mhbc.repository.HallRepository;
import com.example.mhbc.repository.ReservationRepository;
import com.example.mhbc.service.HallService;
import com.example.mhbc.service.ReservationService;
import com.example.mhbc.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  private final ReservationService reservationService;
  private final HallService hallService;

  private final ReservationRepository reservationRepository;

  // 예약 등록 폼 화면
  @GetMapping("/form")
  public String showForm(Model model) {
    ReservationDTO reservation = new ReservationDTO(); // or 조회한 DTO

    model.addAttribute("reservation", new ReservationDTO());
    model.addAttribute("halls", reservationService.getHallList());

    return "reservation/form";
  }

  // 예약 저장 처리
  @PostMapping("/save")
  public String saveReservation(@ModelAttribute ReservationDTO reservationDTO) {
    System.out.println(">>>>>>>>> memberIdx: " + reservationDTO.getMemberIdx()); // 전달된 값 확인

    reservationService.save(reservationDTO);
    return "redirect:/reservation/list";
  }

  // 예약 목록 화면
  @GetMapping("/list")
  public String showList(@RequestParam(value="page", defaultValue="1") int page,
                         Model model) {

    int itemsPerPage = 5; // 한 페이지당 5개씩 출력
    int groupSize = 3;    // 페이징 그룹 크기
    String link = "/reservation/list"; // 현재 페이지 링크

    Pageable pageable = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, "idx");
    Page<ReservationDTO> paging = reservationRepository.findAll(pageable)
      .map(ReservationEntity::toDTO);

    int totalCount = (int) paging.getTotalElements();

    Utility.Pagination pagination = new Utility.Pagination(page, itemsPerPage, totalCount, groupSize, "link");

    model.addAttribute("paging", paging);
    model.addAttribute("pagination", pagination);
    model.addAttribute("link", link);

    //System.out.println(">>>> 전체 예약 수: " + reservationRepository.findAll().size());
    //System.out.println(">>>> 변환된 DTO 수: " + reservationService.findAll().size());

    return "reservation/list";
  }

  // 예약 상세보기 화면
  @GetMapping("/view")
  public String viewReservation(@RequestParam Long idx, Model model) {
    model.addAttribute("reservation", reservationService.findById(idx));
    return "reservation/view";
  }

  // 예약 수정 폼 화면
  @GetMapping("/edit")
  public String editReservation(@RequestParam Long idx, Model model) {
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
}
