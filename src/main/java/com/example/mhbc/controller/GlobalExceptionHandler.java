package com.example.project.controller;

import org.springframework.http.HttpStatus;
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
        return "error"; // 타임리프 error.html 로 이동
    }

    // 403 Forbidden (접근 금지) 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("errorTitle", "접근 금지");
        model.addAttribute("errorMessage", "이 페이지에 접근할 권한이 없습니다.");
        return "error"; // 타임리프 error.html 로 이동
    }
*/
    // 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "오류 발생");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error"; // 타임리프 error.html 로 이동
    }
}
