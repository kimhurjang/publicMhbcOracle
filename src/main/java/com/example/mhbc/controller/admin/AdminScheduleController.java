// 관리자 일정차단 컨트롤러
package com.example.mhbc.controller.admin;

import com.example.mhbc.dto.ScheduleBlockDTO;
import com.example.mhbc.entity.ScheduleBlockEntity;
import com.example.mhbc.repository.ScheduleBlockRepository;
//import com.example.mhbc.service.UserDetailsImpl;
import com.example.mhbc.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/schedule")
@RequiredArgsConstructor
public class AdminScheduleController {

  private final ScheduleBlockRepository scheduleBlockRepository;

  @GetMapping({"", "/"})
  public String schedule() {
    System.out.println(">>>>>>>>>>admin schedule page<<<<<<<<<<");
    return "/admin/schedule/list";
  }

  // 등록폼 진입
  @GetMapping("/form")
  public String form(Model model) {
    model.addAttribute("schedule", new ScheduleBlockDTO()); // 폼 바인딩용 빈 객체
    return "admin/schedule/form";
  }

  // 저장 처리
  @Transactional //@Transactional이 없으면 JPA가 remove/delete를 안정적으로 수행하지 못해 이런 오류가 남
  @PostMapping("/save")
  public String save(@ModelAttribute ScheduleBlockDTO dto
                     // ✅임시사용자 쓸 동안 주석
                     ,@AuthenticationPrincipal UserDetailsImpl user
                     ) throws Exception {
    //String username = "admin"; // ✅ 임시사용자용

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date eventDate = sdf.parse(dto.getEventDate());
    String timeSlot = dto.getTimeSlot();

    // 중복 방지: 동일 날짜 + 동일 타임 이미 등록돼 있으면 저장 중단
    if (scheduleBlockRepository.existsByEventDateAndTimeSlot(eventDate, timeSlot)) {
      // TODO: 오류 메시지 처리 or redirect
      return "redirect:/admin/schedule/form?error=duplicate";
    }

    // 1) ALL 등록 → 기존 개별 타임들 삭제
    if ("ALL".equals(timeSlot)) {
      scheduleBlockRepository.deleteByEventDateAndTimeSlotIn(
        eventDate,
        List.of("10시", "12시", "14시", "16시")
      );
    }

    // 2) 개별 타임 등록 → 기존 ALL 삭제
    else {
      scheduleBlockRepository.deleteByEventDateAndTimeSlotIn(
        eventDate,
        List.of("ALL")
      );
    }

    ScheduleBlockEntity entity = new ScheduleBlockEntity();
    entity.setEventDate(eventDate);
    entity.setTimeSlot(timeSlot);
    entity.setReason(dto.getReason());
    entity.setCreatedAt(new Date());
    //entity.setModifiedBy(username); // ✅임시사용자용 아래 주석 해제
    entity.setModifiedBy(user.getUsername());

    scheduleBlockRepository.save(entity);

    return "redirect:/admin/schedule/list";
  }

  @PostMapping("/update")
  public String update(@ModelAttribute ScheduleBlockDTO dto
                       // ✅임시사용자 쓸 동안 주석
                       ,@AuthenticationPrincipal UserDetailsImpl user
                      ) throws Exception {
    //String username = "admin"; // ✅ 임시사용자용

    Optional<ScheduleBlockEntity> optional = scheduleBlockRepository.findById(dto.getIdx());
    if (optional.isPresent()) {
      ScheduleBlockEntity entity = optional.get();
      entity.setEventDate(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getEventDate()));
      entity.setTimeSlot(dto.getTimeSlot());
      entity.setReason(dto.getReason());
      //entity.setModifiedBy(username); // ✅임시사용자용 아래 주석 해제
      entity.setModifiedBy(user.getUsername()); // 수정자 ID 갱신
      scheduleBlockRepository.save(entity);
    }
    return "redirect:/admin/schedule/list";
  }

  @GetMapping("/list")
  public String list(Model model) {
    List<ScheduleBlockEntity> list = scheduleBlockRepository.findAll();
    model.addAttribute("list", list);
    return "admin/schedule/list";
  }

  // 상세보기
  @GetMapping("/view/{id}")
  public String view(@PathVariable Long id, Model model) {
    Optional<ScheduleBlockEntity> result = scheduleBlockRepository.findById(id);
    result.ifPresent(entity -> {
      ScheduleBlockDTO dto = new ScheduleBlockDTO();
      dto.setIdx(entity.getIdx());
      dto.setEventDate(new SimpleDateFormat("yyyy-MM-dd").format(entity.getEventDate()));
      dto.setTimeSlot(entity.getTimeSlot());
      dto.setReason(entity.getReason());

      dto.setModifiedBy(entity.getModifiedBy());
      model.addAttribute("schedule", dto);
    });
    return "admin/schedule/view";
  }

  // 삭제 처리
  @PostMapping("/delete")
  public String delete(@RequestParam Long id) {
    scheduleBlockRepository.deleteById(id);
    return "redirect:/admin/schedule/list";
  }

}
