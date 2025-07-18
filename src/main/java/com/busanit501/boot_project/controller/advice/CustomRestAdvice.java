package com.busanit501.boot_project.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String,String>> handleException(BindException e){
        log.error("CustomRestAdvice 클래스에서, 레스트 처리시, 유효성 체크 작업중.",e);

        Map<String,String> errorMap = new HashMap<>();
        if(e.hasErrors()){
            BindingResult bindingResult = e.getBindingResult();
            bindingResult.getFieldErrors().forEach((fieldError)->{
                errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
            });
        }
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String,String>> handleFKException(DataIntegrityViolationException e){
        log.error(e);

        Map<String,String> errorMap = new HashMap<>();

        errorMap.put("time","" +System.currentTimeMillis());
        errorMap.put("msg",e.getMessage());

        return ResponseEntity.badRequest().body(errorMap);

    }

    @ExceptionHandler({NoSuchElementException.class,
            EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String,String>> handleNoSuchElementException(Exception e){
        log.error(e);
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("time","" +System.currentTimeMillis());
        errorMap.put("msg",e.getMessage());
        return ResponseEntity.badRequest().body(errorMap);
    }

}
