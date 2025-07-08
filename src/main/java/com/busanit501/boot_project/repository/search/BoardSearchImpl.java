package com.busanit501.boot_project.repository.search;

import com.busanit501.boot_project.domain.Board;
import com.busanit501.boot_project.domain.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

// 인터페이스이름 + Impl, 이름 규칙, 동일하게 작성,
// QuerydslRepositorySupport : 부모클래스, Querydsl 사용하기 위한 도구함.
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {
        // 부모 클래스에서, 엔티티 클래스 전달. 사용할 클래스 지정.
        super(Board.class);
    }

    @Override
    public Page<Board> search(Pageable pageable) {
        // 자바 문법으로만, sql 명령어 전달 하는게 목적.

        // QueryDSL ,사용법,
        // 순서1
        // Q도메인 객체 : 엔티티 클래스 Board, 동적 쿼리 작업 하기 위한
        // 편하게 만든 클래스라고 생각하면됨.
        // 기능이 향상된 버전이다.
        QBoard board = QBoard.board;
        // 순서2
        JPQLQuery<Board> query = from(board); // select .. from board 형식과 동일함.
        // 순서3
        query.where(board.title.contains("1")); // where title like...
        // 순서4
        List<Board> list = query.fetch(); // db에서 데이터 가져오기.
        // 순서5
        long count = query.fetchCount(); // 가져온 디비의 갯수 확인.
        // 순서6
        // 페이징 조건 추가해보기. 검색 쿼리에 , 페이징 조건 탑재
        this.getQuerydsl().applyPagination(pageable,query);
        // 순서7
        // 페이징 조건을 적용하고, 조회하기.
        List<Board> list2 = query.fetch();
        // 순서8
        // 페이징 조건 적용 + 전체 갯수
        long count2 = query.fetchCount();

        return null;
    }

    // 페이징 정보 + 검색 정보를 이용해서,
    // 자바 메서드로 -> sql 전달하기.
    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        // 순서1
        QBoard board = QBoard.board; // (board)
        // 순서2
        JPQLQuery<Board> query = from(board); // select .. from board
        // 순서3,
        // where 조건절 , BooleanBuilder 를 이용해서 조건 추가.
        // select .. from board where ....
        if ((types != null && types.length > 0) && keyword != null) {
            // or , 조건, and 조건을 사용하기 싶다. 묶기도 쉽다.
            BooleanBuilder builder = new BooleanBuilder();
            // types = {"t","w","c"}
            for(String type : types){
                switch (type) {
                    case "t":
                        builder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        builder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        builder.or(board.writer.contains(keyword));
                        break;
                } // end switch
            } // end for
            query.where(builder); // select * from board where like %keyword%
        } //end if
        // bno >0 조건 추가히기.
        query.where(board.bno.gt(0L));

        // paging 조건 추가하기.
        // 순서4,
        this.getQuerydsl().applyPagination(pageable,query);
        // 순서5,
        // 위의 검색 조건, 페이징 조건으로 sql  전달하기.  데이터 가져오기
        List<Board> list = query.fetch();
        // 순서6,
        long count = query.fetchCount();

        // 순서7, 리턴 타입에 맞추기
        return new PageImpl<>(list,pageable,count);
    }
}
