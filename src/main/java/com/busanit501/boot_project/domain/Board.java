package com.busanit501.boot_project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

// 엔티티 클래스 이용해서 -> 마리아 디비에 테이블 생성.
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board extends BaseEntity {

    @Id // DB pk와 같은 역할
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;
    private String content;
    private String writer;

    // 불변성으로, 데이터를 직접 수정하지않고,
    // 변경하려는 필드를 , 따로 메서드로 분리해서 작업함.
    public void changTitleContent(String title, String content) {
        this.title = title;
        this.content = content;
    }


}
