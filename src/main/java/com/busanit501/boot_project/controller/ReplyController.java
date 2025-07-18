package com.busanit501.boot_project.controller;

import com.busanit501.boot_project.dto.PageRequestDTO;
import com.busanit501.boot_project.dto.PageResponseDTO;
import com.busanit501.boot_project.dto.ReplyDTO;
import com.busanit501.boot_project.service.ReplyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> register( @Valid @RequestBody ReplyDTO replyDTO,
                                      BindingResult bindingResult) throws BindException
    {
        log.info("ReplyController에서 작업중, 댓글 작성작업");

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        log.info("ReplyController에서 작업중 replyDTO : " + replyDTO);

        Map<String,Long> resultMap = new HashMap<>();
        Long rno = replyService.register(replyDTO);
        resultMap.put("rno",rno);

        return resultMap;
    }

    @Tag(name = "댓글 목록 조회", description = "댓글 목록 조회 레스트 버전 get 방식")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable Long bno, PageRequestDTO pageRequestDTO) {
        log.info("ReplyController에서 작업중, 댓글 목록 조회, 전달받은 bno 확인 :  " + bno);

        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);
        return responseDTO;

    }

    @Tag(name = "댓글 하나 조회", description = "댓글 하나 조회 레스트 버전 get 방식")
    @GetMapping(value = "/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable Long rno) {
        log.info("ReplyController에서 작업중,하나 조회 전달받은 rno 확인 :  " + rno);

        ReplyDTO replyDTO = replyService.read(rno);
        return replyDTO;

    }

    @Tag(name = "댓글 삭제", description = "댓글 삭제 레스트 버전 delete 방식")
    @DeleteMapping(value = "/{rno}")
    public Map<String,Long> remove(@PathVariable Long rno) {
        log.info("ReplyController에서 작업중, 삭제, 전달받은 rno 확인 :  " + rno);

        replyService.remove(rno);
        Map<String,Long> resultMap = new HashMap<>();
        resultMap.put("rno",rno);

        return resultMap;

    }

    @Tag(name = "댓글 수정", description = "댓글 수정 레스트 버전 put 방식")
    @PutMapping(value = "/{rno}")
    public Map<String,Long> modify(@PathVariable Long rno,
                                   @RequestBody ReplyDTO replyDTO) {
        log.info("ReplyController에서 작업중, 수정, 전달받은 rno 확인 :  " + rno);
        log.info("ReplyController에서 작업중, 수정, 전달받은 replyDTO 확인 :  " + replyDTO);
        replyDTO.setRno(rno);
        replyService.modify(replyDTO);
        Map<String,Long> resultMap = new HashMap<>();
        resultMap.put("rno",rno);

        return resultMap;

    }


}
