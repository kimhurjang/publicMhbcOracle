package com.example.mhbc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /*
    // 404 (페이지를 찾을 수 없음) 예외 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model) {
        model.addAttribute("errorTitle", "페이지를 찾을 수 없습니다.");
        model.addAttribute("errorMessage", "요청한 페이지가 존재하지 않습니다.");
        model.addAttribute("errorCode", HttpStatus.NOT_FOUND.value()); // 404 코드 추가
        model.addAttribute("errorDetails", ex.toString()); // 예외의 상세 정보 추가
        return "error"; // 타임리프 error.html 로 이동
    }

    //로그인하지 않은 사용자가 접근했을 때 로그인 페이지로 리다이렉트 //(AccessDeniedException 처리)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied() {
        return "redirect:/api/member/login"; // 절대 이건 View 이름 아님!
    }

    // 일반적인 예외 처리 + Ajax
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleException(IllegalArgumentException ex, HttpServletRequest request, Model model) {
        // Ajax 요청에서 모든 예외를 텍스트로 반환
        if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            return ResponseEntity.badRequest().body(ex.getMessage()); // Ajax 요청: 텍스트만
        } else {
            // HTML 요청이면 error.html로 이동하면서 상세 정보 추가
            model.addAttribute("errorTitle", "오류 발생");
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500 오류 코드
            model.addAttribute("errorDetails", ex.toString()); // 예외 상세 정보
            return "error"; // 타임리프 error.html 렌더링
        }
    }
*/
}

    /*
    // 403 Forbidden (접근 금지) 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("errorTitle", "접근 금지");
        model.addAttribute("errorMessage", "이 페이지에 접근할 권한이 없습니다.");
        model.addAttribute("errorCode", HttpStatus.FORBIDDEN.value()); // 403 코드 추가
        model.addAttribute("errorDetails", ex.toString()); // 예외의 상세 정보 추가
        return "error"; // 타임리프 error.html 로 이동
    }*/
