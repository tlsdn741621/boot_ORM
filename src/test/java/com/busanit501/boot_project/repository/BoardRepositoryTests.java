package com.busanit501.boot_project.repository;

import com.busanit501.boot_project.domain.Board;
import com.busanit501.boot_project.dto.BoardListReplyCountDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {
    @Autowired
    private BoardRepository boardRepository;

    //JpaRepository 를 이용해서, 기본 crud 확인.
    // sql 를 따로 몰라도, 자바의 메서드만 호출해서, sql 전달하기.
    // 1 insert 확인,
    @Test
    public void testInsert() {
        // 더미 데이터, 병렬 처리로, 100개 정도, 하드 코딩하기.
        IntStream.rangeClosed(1, 100).forEach(i -> {
            // 엔티티 클래스 , 임시 객체를 생성을 반복문에 맞춰서 100개 생성.
            Board board = Board.builder()
                    .title("title____" + i)
                    .content("content____" + i)
                    .writer("user_____" + (i % 10))
                    .build();
            // 디비에 반영하기. save, insert sql 문장과 결과 동일.
            //====================== JpaRepository에서 확인 하는 부분은 여기==================================
            Board result = boardRepository.save(board);
            //====================== JpaRepository에서 확인 하는 부분은 여기==================================
            log.info("bno : " + result.getBno());
        });
    }// 1 insert 확인,

    // 2 , select
    @Test
    public void testSelect() {
        Long tno = 100L;
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        Optional<Board> result = boardRepository.findById(tno);
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        Board board = result.orElseThrow();
        log.info("bno : " + board);
    }
    // 2 , select

    //3. update
    @Test
    public void testUpdate() {
        Long bno = 100L;
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        //Db 로 부터 조회 된 데이터를 임시 객체에 담기
        Optional<Board> result = boardRepository.findById(bno);
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        // 패턴이니 잘 숙지, 있으면 엔티티 클래스 타입으로 받고, 없으면 예외 발생시키기
        Board board = result.orElseThrow();
        // 변경할 제목, 내용만 교체 하면 됨.
        board.changTitleContent("수정제목","수정, 오늘 점심 뭐 먹지?");
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        // 실제 디비에 반영.
        boardRepository.save(board);
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================

    } //3. update

    //4. delete
    @Test
    public void testDelete() {
        Long bno = 1L;
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        boardRepository.deleteById(bno);
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
    }

    //5. 페이징 테스트
    @Test
    public void testPage() {
        // 1 페이지에서, bno 기준으로 내림차순.
        // of(페이지번호(0: 1페이지), 사이즈, 정렬조건)
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
        // JpaRepository 이용해서, 페이징 처리가 된 데이터를 받기.
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        Page<Board> result = boardRepository.findAll(pageable);
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        // 페이징 관련 기본 정보 를 출력가능.
        // 1) 전체 갯수 2) 전체 페이지 3) 현재 페이지 번호
        // 4) 보여줄 사이즈 크기 10개,
        // 5) 디비에서 페이징된 조회될 데이터 10개 - voList 이런 형식으로 표현했음.
        log.info("전체 갯수 : total count : " + result.getTotalElements());
        log.info("전체 페이지 : total pages : " + result.getTotalPages());
        log.info("현재 페이지 번호 : page number  : " + result.getNumber());
        log.info("보여줄 사이즈 크기 : page size  : " + result.getSize());
        // 임시 리스트 생성해서, 디비에서 전달 받은 데이터를 담아두기.
        List<Board> todoList = result.getContent();
        log.info("디비에서 페이징된 조회될 데이터 10개 : todoList  : ");
        todoList.forEach(board -> log.info(board));

    } //5. 페이징 테스트

    // 6, QueryDSL 확인, 자바 문법 -> SQL 전달하기.
    @Test
    public void testSearch() {
        // 화면에서 전달 받을 페이징 정보, 더미 데이터 필요함.
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
        // 준비물 가지고, QueryDSL  , 백그라운드 동작 과정 확인 해보기.
        boardRepository.search(pageable);
    }
    // 6, QueryDSL 확인, 자바 문법 -> SQL 전달하기.

    // 7. QueryDSL 확인, 자바 문법 -> SQL 전달하기.2
    // 페이징 정보 + 검색 정보를 같이 전달해서, 조회하기.
    @Test
    public void testSearchAll() {
        // 더미 데이터 2개 필요,
        // 1) 페이징 정보, 2) 검색 정보
        // 검색 정보 더미 데이터 만들기 .
        // 화면의 체크박스에서, 작성자, 내용, 제목 다 체크 했다 가정.
        String[] types = {"t","c","w"};
        //검색어
        String keyword = "ㅇ";
        // 페이징 정보,
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
        // 실제 디비 가져오기 작업,
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);
        //====================== JpaRepository에서 확인 하는 부분은 여기==================================
        // 단위 테스트 실행하고, sql 전달 여부, 콘솔에서, sql 출력 확인하기, 목적.,
        // 자바 문법으로 -> sql 어떻게 전달을 하는지 여부를 확인, 관건. !!!!

        // 결과 값, 콘솔에서 확인.
        log.info("전체 갯수 : total count : " + result.getTotalElements());
        log.info("전체 페이지 : total pages : " + result.getTotalPages());
        log.info("현재 페이지 번호 : page number  : " + result.getNumber());
        log.info("보여줄 사이즈 크기 : page size  : " + result.getSize());
        log.info("이전 페이지 유무 : " + result.hasPrevious());
        log.info("다음 페이지 유무 : " + result.hasNext());
        // 임시 리스트 생성해서, 디비에서 전달 받은 데이터를 담아두기.
        List<Board> todoList = result.getContent();
        log.info("디비에서 페이징된 조회될 데이터 10개 : todoList  : ");
        todoList.forEach(board -> log.info(board));
    }

    // 기존 , 보드 정보 4가지에 이어서, 추가로 댓글 갯수 추가한 형태
    @Test
    public void testSearchReplyCount() {
        // 검색시 사용할, 더미 데이터 준비물
        // 1)
        String[] types = {"t","c","w"};
        //검색어
        String keyword = "ㅇ";
        // 페이징 정보,
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        // 댓글 갯수가 포함된 데이터를 조회
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);
        // 결과 값, 콘솔에서 확인.
        log.info("전체 갯수 : total count : " + result.getTotalElements());
        log.info("전체 페이지 : total pages : " + result.getTotalPages());
        log.info("현재 페이지 번호 : page number  : " + result.getNumber());
        log.info("보여줄 사이즈 크기 : page size  : " + result.getSize());
        log.info("이전 페이지 유무 : " + result.hasPrevious());
        log.info("다음 페이지 유무 : " + result.hasNext());
        // 임시 리스트 생성해서, 디비에서 전달 받은 데이터를 담아두기.
        List<BoardListReplyCountDTO> todoList = result.getContent();
        log.info("디비에서 페이징된 조회될 데이터 10개 : todoList  : ");
        todoList.forEach(board -> log.info(board));
    }


}
