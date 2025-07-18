package com.busanit501.boot_project.repository;

import com.busanit501.boot_project.domain.Board;
import com.busanit501.boot_project.repository.search.BoardSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface BoardRepository extends JpaRepository<Board, Long> , BoardSearch {

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno = :bno")
    Optional<Board> findByIdWithImages(Long bno);

}
