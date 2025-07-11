package com.busanit501.boot_project.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {

    // 어떤 예외를 처리할 지 종류 지정.
    @ExceptionHandler(BindException.class)// 예외를 처리할 종류를 알려주기
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)//응답코드, 예외 실패. 메세지 코드 전달.
    public ResponseEntity<Map<String,String>> handleException(BindException e){
        log.error("CustomRestAdvice 클래스에서, 레스트 처리시, 유효성 체크 작업중.",e);

        // 예외 종류와, 메세지 담아 둘 임시 객체,
        Map<String,String> errorMap = new HashMap<>();
        if(e.hasErrors()){ // 댓글 작성중, 오류가 있다면, 맵에 담아서 전달 할게,
            // bindingResult, 오류 사전에서, 문제점 꺼내서, 맵에 담기.
            BindingResult bindingResult = e.getBindingResult();
            bindingResult.getFieldErrors().forEach((fieldError)->{
                errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
            });
        }
        // 서버 -> 화면으로 , 준비물 전달 : 1) 상태 코드 400 badrequest , 너가 잘못된 형식으로 보낸거야,
        // 2) body(errorMap); 데이터 전달, 구체적인 오류의 내용들.
        return ResponseEntity.badRequest().body(errorMap);
    }

}
