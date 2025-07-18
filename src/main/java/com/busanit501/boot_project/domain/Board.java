package com.busanit501.boot_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;
    private String content;
    private String writer;

    @OneToMany(mappedBy = "board", cascade = {CascadeType.ALL}
            ,fetch = FetchType.LAZY,
    orphanRemoval = true)
    @Builder.Default
    private Set<BoardImage>  imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName) {
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    public void clearImages() {
        imageSet.forEach(boardImage -> boardImage.chageBoard(null));
        this.imageSet.clear();
    }

    public void changTitleContent(String title, String content) {
        this.title = title;
        this.content = content;
    }


}
