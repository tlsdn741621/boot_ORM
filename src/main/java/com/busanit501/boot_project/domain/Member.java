package com.busanit501.boot_project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity  // ✅ 여기 붙어야 합니다!!
@Getter
@Setter
public class Member {

    @Id  // ✅ 반드시 있어야 함 (기본 키 식별자)
    private Long id;

    private String username;

    private String email;
}
