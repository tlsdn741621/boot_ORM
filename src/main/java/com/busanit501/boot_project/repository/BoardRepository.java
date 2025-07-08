package com.busanit501.boot_project.repository;

import com.busanit501.boot_project.domain.Board;
import com.busanit501.boot_project.repository.search.BoardSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 설정한 인터페이스(BoardSearch)를 여기에 적용하기.
public interface BoardRepository extends JpaRepository<Board, Long> , BoardSearch {
//    @Query(value = "select now()", nativeQuery = true)
//    String getTime();

}
