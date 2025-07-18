package com.busanit501.boot_project.controller;

import com.busanit501.boot_project.domain.Place;
import com.busanit501.boot_project.service.VisitBusanCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class VisitBusanController {

    @Autowired // VisitBusanCrawler 서비스를 자동으로 주입받음
    private VisitBusanCrawler visitBusanCrawler;

    @GetMapping("/view_places") // "/places" 경로로 GET 요청이 오면 이 메서드 실행
    public String places(Model model) {
        System.out.println("/places 요청 받음");
        List<Place> places = visitBusanCrawler.crawlPlaces(); // 크롤러 서비스 호출하여 데이터 가져오기
        System.out.println("컨트롤러에서 크롤링한 아이템 수 : " + places.size()); // 가져온 아이템 수 콘솔 출력
        model.addAttribute("places", places); // Model에 "places"라는 이름으로 데이터 추가
        return "your_thymeleaf_template_name";  // resources/templates/places.html 템플릿을 찾아 렌더링
    }
}