package com.busanit501.boot_project.domain;

import lombok.AllArgsConstructor; // 모든 필드를 인자로 받는 생성자 자동 생성
import lombok.Data;              // Getter, Setter, toString, equals, hashCode 등 자동 생성
import lombok.NoArgsConstructor;   // 기본 생성자 자동 생성

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Place {
    private String title;  // 장소의 제목
    private String image;  // 장소 대표 이미지 URL
    private String link;   // 장소 상세 페이지 링크 URL
}