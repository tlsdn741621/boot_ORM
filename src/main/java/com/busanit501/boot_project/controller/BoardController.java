package com.busanit501.boot_project.controller;

import com.busanit501.boot_project.dto.BoardDTO;
import com.busanit501.boot_project.dto.BoardListReplyCountDTO;
import com.busanit501.boot_project.dto.PageRequestDTO;
import com.busanit501.boot_project.dto.PageResponseDTO;
import com.busanit501.boot_project.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);
        log.info("BoardController에서, list, responseDTO : {}", responseDTO);
        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/register")
    public void register() {
    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, registerPost 작업중");
        if (bindingResult.hasErrors()) {
            log.info("registerPost, 입력 작업중, 유효성 체크에 해당 사항 있음");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        log.info("넘어온 데이터 확인 boardDTO : " + boardDTO);
        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);
        return  "redirect:/board/list";
    }

    @GetMapping({"/read","/update"})
    public void read(Long bno, PageRequestDTO pageRequestDTO,
                     Model model) {
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("BoardController 에서, read 작업중 boardDTO: "+ boardDTO);
        model.addAttribute("dto", boardDTO);

    }

    @PostMapping("/update")
    public String update(PageRequestDTO pageRequestDTO,
                         @Valid BoardDTO boardDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, update 작업중 boardDTO:" + boardDTO);
        if (bindingResult.hasErrors()) {
            log.info("update, 입력 작업중, 유효성 체크에 해당 사항 있음");
            String link = pageRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            return "redirect:/board/update?"+link;
        }
        boardService.modify(boardDTO);
        redirectAttributes.addFlashAttribute("result", "수정완료");
        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        return  "redirect:/board/read";
    }

    @PostMapping("/remove")
    public String remove(Long bno, RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, remove 작업중 , 넘어온 bno 확인: " + bno);
        boardService.remove(bno);
        redirectAttributes.addFlashAttribute("result", "삭제완료!!");
        return "redirect:/board/list";
    }

}
