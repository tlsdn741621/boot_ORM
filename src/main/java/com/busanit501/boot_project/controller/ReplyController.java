package com.busanit501.boot_project.controller;

import com.busanit501.boot_project.dto.ReplyDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {
    // JSON 데이터 타입의 기본 모양, { "키" : 값, "키2":값2,... }
    // MIME 타입, -> MediaType.APPLICATION_JSON_VALUE
    //{
    //  "rno": 0,
    //  "bno": 0,
    //  "replyText": "string",
    //  "replyer": "string",
    //  "regDate": "2025-07-11T06:38:49.331Z",
    //  "modDate": "2025-07-11T06:38:49.331Z"
    //}

    // 네트워크 통해서, 실제로 전달시에는 문자열 형태로 전달.

    // @RequestBody ReplyDTO replyDTO
    // 의미 : 화면에서, JSON 데이터(문자열) 전달함
    // -> 서버에서, 객체 형태로 변경해줌. 역직렬화라고 부름.

    //ResponseEntity<Map<String,Long>>
    // 의미 : 서버 -> 화면에게 , 응답하는 타입,
    // 전달 내용.
    // 1) http status code , 200 ok, 2) 데이터 내용
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Long>> register( @Valid @RequestBody ReplyDTO replyDTO,
                                                      BindingResult bindingResult) throws BindException
    {
        log.info("ReplyController에서 작업중, 댓글 작성작업");

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        log.info("replyDTO : " + replyDTO);
        // 화면이 없어서, 하드코딩으로 더미 데이터 만들기.
        Map<String,Long> resultMap = Map.of("rno",100L);
        return ResponseEntity.ok(resultMap);
    }
}
