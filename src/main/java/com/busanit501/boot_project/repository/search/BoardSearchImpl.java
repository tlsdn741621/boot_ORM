package com.busanit501.boot_project.repository.search;

import com.busanit501.boot_project.domain.Board;
import com.busanit501.boot_project.domain.QBoard;
import com.busanit501.boot_project.domain.QReply;
import com.busanit501.boot_project.dto.BoardListReplyCountDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {

        super(Board.class);
    }

    @Override
    public Page<Board> search(Pageable pageable) {

        QBoard board = QBoard.board;

        JPQLQuery<Board> query = from(board);

        query.where(board.title.contains("1"));

        List<Board> list = query.fetch();
        // 순서5
        long count = query.fetchCount();

        this.getQuerydsl().applyPagination(pageable,query);

        List<Board> list2 = query.fetch();

        long count2 = query.fetchCount();

        return null;
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board; // (board)

        JPQLQuery<Board> query = from(board); // select .. from board

        if ((types != null && types.length > 0) && keyword != null) {
            BooleanBuilder builder = new BooleanBuilder();
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
                }
            }
            query.where(builder);
        }

        query.where(board.bno.gt(0L));

        this.getQuerydsl().applyPagination(pageable,query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list,pageable,count);
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);

        query.leftJoin(reply).on(reply.board.eq(board));
        query.groupBy(board);

        if ((types != null && types.length > 0) && keyword != null) {

            BooleanBuilder builder = new BooleanBuilder();

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
                }
            }
            query.where(builder);
        }
        query.where(board.bno.gt(0L));

        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(
                Projections.bean(BoardListReplyCountDTO.class,
                        board.bno,
                        board.title,
                        board.writer,
                        board.regDate,
                        reply.count().as("replyCount")
                )
        );

        this.getQuerydsl().applyPagination(pageable,dtoQuery);

        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();
        long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList,pageable,count);
    }
}
