package com.busanit501.boot_project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno")
})
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String replyText;
    private String replyer;

    public void changeBoard(Board board) {
        this.board = board;
    }

    public void changeReplyText(String text) {
        this.replyText = text;
    }

    public void changeReplyer(String replyer) {
        this.replyer = replyer;
    }
}
