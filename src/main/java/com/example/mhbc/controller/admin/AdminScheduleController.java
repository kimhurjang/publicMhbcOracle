// 관리자 일정차단 컨트롤러
package com.example.mhbc.controller.admin;

import com.example.mhbc.dto.ScheduleBlockDTO;
import com.example.mhbc.entity.ScheduleBlockEntity;
import com.example.mhbc.repository.ScheduleBlockRepository;
//import com.example.mhbc.service.UserDetailsImpl;
import com.example.mhbc.service.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;

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
    List<String> selectedSlots = dto.getTimeSlots(); // ✅ 복수 선택용 getter 사용

    if (selectedSlots == null || selectedSlots.isEmpty()) {
      // 아무 것도 체크 안 한 경우 예외 처리
      return "redirect:/admin/schedule/form?error=noslot";
    }

    // 중복 방지: 동일 날짜 + 동일 타임 이미 등록돼 있으면 저장 중단
    for (String slot : selectedSlots) {
      if (scheduleBlockRepository.existsByEventDateAndTimeSlot(eventDate, slot)) {
        return "redirect:/admin/schedule/form?error=duplicate";
      }
    }

    // 1) ALL 등록 → 기존 개별 타임들 삭제
    if (selectedSlots.contains("ALL")) {
      scheduleBlockRepository.deleteByEventDateAndTimeSlotIn(
              eventDate,
              List.of("10시", "12시", "14시", "16시")
      );
    } else {
      // 개별 선택 시 기존 ALL 삭제
      scheduleBlockRepository.deleteByEventDateAndTimeSlotIn(
              eventDate,
              List.of("ALL")
      );
    }

    // 선택된 모든 슬롯 저장
    for (String slot : selectedSlots) {
      ScheduleBlockEntity entity = new ScheduleBlockEntity();
      entity.setEventDate(eventDate);
      entity.setTimeSlot(slot);
      entity.setReason(dto.getReason());
      entity.setCreatedAt(new Date());
      //entity.setModifiedBy(username); // ✅임시사용자용 아래 주석 해제
      entity.setModifiedBy(user.getUsername());

      scheduleBlockRepository.save(entity);
    }

    return "redirect:/admin/schedule/list";
  }

  @Transactional
  @PostMapping("/update")
  public String update(@ModelAttribute ScheduleBlockDTO dto
                       // ✅임시사용자 쓸 동안 주석
                       ,@AuthenticationPrincipal UserDetailsImpl user
                      ) throws Exception {
    //String username = "admin"; // ✅ 임시사용자용

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date eventDate = sdf.parse(dto.getEventDate());
    List<String> selectedSlots = dto.getTimeSlots();

    if (selectedSlots == null || selectedSlots.isEmpty()) {
      return "redirect:/admin/schedule/form?error=noslot";
    }

    // 기존 날짜 전체 삭제 후 재등록
    scheduleBlockRepository.deleteByEventDateAndTimeSlotIn(
            eventDate,
            List.of("10시", "12시", "14시", "16시", "ALL")
    );

    for (String slot : selectedSlots) {
      ScheduleBlockEntity entity = new ScheduleBlockEntity();
      entity.setEventDate(eventDate);
      entity.setTimeSlot(slot);
      entity.setReason(dto.getReason());
      entity.setCreatedAt(new Date());
      //entity.setModifiedBy(username); // ✅임시사용자용 아래 주석 해제
      entity.setModifiedBy(user.getUsername()); // 수정자 ID 갱신

      scheduleBlockRepository.save(entity);
    }
    return "redirect:/admin/schedule/list";
  }

  @GetMapping("/list")
  public String list(Model model) throws JsonProcessingException {
    List<ScheduleBlockEntity> entities = scheduleBlockRepository.findAll();

    // 일정 하나당 타임슬롯 1개로 나눠서 리스트 구성
    List<ScheduleBlockDTO> list = entities.stream()
            .map(ScheduleBlockDTO::fromEntity)
            .collect(Collectors.toList());

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonList = objectMapper.writeValueAsString(list);

    //System.out.println("▶ DTO 변환 후 일정 수: " + list.size());
    model.addAttribute("list", jsonList);

    return "admin/schedule/list";
  }

  // 상세보기
  @GetMapping("/view/{id}")
  public String view(@PathVariable Long id, Model model) {
    Optional<ScheduleBlockEntity> result = scheduleBlockRepository.findById(id);
    if (result.isPresent()) {
      ScheduleBlockEntity base = result.get();
      Date eventDate = base.getEventDate();
      List<ScheduleBlockEntity> slots = scheduleBlockRepository.findByEventDate(eventDate);

      ScheduleBlockDTO dto = ScheduleBlockDTO.fromEntity(base);
      dto.setTimeSlots(slots.stream().map(ScheduleBlockEntity::getTimeSlot).collect(Collectors.toList()));

      model.addAttribute("schedule", dto);
    }
    return "admin/schedule/view";
  }


  // 삭제 처리
  @Transactional
  @PostMapping("/delete")
  public String delete(@RequestParam Long id) {
    scheduleBlockRepository.deleteById(id);
    return "redirect:/admin/schedule/list";
  }

}
