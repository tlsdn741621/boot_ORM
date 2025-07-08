package com.busanit501.boot_project.service;

import com.busanit501.boot_project.dto.BoardDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister() {
        // 입력할 더미 데이터 , 준비물 준비하고,
        BoardDTO boardDTO = BoardDTO.builder()
                .title("서비스 작업 단위 테스트 중")
                .content("서비스 작업 단위 테스트 내용 작성중 ")
                .writer("이상용")
                .build();
        // 실제 서비스 이용해서, 외주 주기.
        boardService.register(boardDTO);
    }
}
