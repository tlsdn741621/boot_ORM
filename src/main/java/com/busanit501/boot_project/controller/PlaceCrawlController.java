package com.busanit501.boot_project.controller;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller; // @RestController 대신 @Controller 사용
import org.springframework.ui.Model; // Model 객체 import
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller // HTML 템플릿을 반환하기 위해 @Controller 사용
@Log4j2
public class PlaceCrawlController {

    @GetMapping("/places")
    public String crawlPlaces(Model model) { // 반환 타입을 String으로, Model 객체 추가
        String url = "https://www.visitbusan.net/index.do?menuCd=DOM_000000201002000000";
        List<Map<String, String>> crawledData = new ArrayList<>();

        log.info("/places 요청 받음 - 크롤링 시작: {}", url);

        try {
            Document document = Jsoup.connect(url).get();
            Elements itemContainers = document.select(".box.actionImg3");
            log.info("크롤링 대상 아이템 컨테이너 개수: {}", itemContainers.size());

            if (itemContainers.isEmpty()) {
                log.warn("오류: '.box.actionImg3' 요소를 찾았지만, 내부에서 추출할 내용이 없습니다.");
            } else {
                for (Element container : itemContainers) {
                    Map<String, String> item = new HashMap<>();

                    Element imgElement = container.selectFirst("img");
                    Element infoDiv = container.nextElementSibling();

                    String titleText = "제목 없음";
                    if (infoDiv != null && infoDiv.hasClass("info")) {
                        Element titleElement = infoDiv.selectFirst("p.tit a");
                        if (titleElement != null) {
                            titleText = titleElement.text();
                        }
                    }

                    String imgSrc = (imgElement != null) ? imgElement.attr("src") : "이미지 주소 없음";
                    String imgAlt = (imgElement != null) ? imgElement.attr("alt") : "이미지 대체 텍스트 없음";
                    String itemLink = (container.selectFirst("a") != null) ? container.selectFirst("a").attr("href") : "링크 없음";

                    item.put("title", titleText);
                    // 이미지 URL이 상대 경로이므로, 전체 경로로 만들어줍니다.
                    // 실제 웹사이트의 기본 URL을 앞에 붙여주세요.
                    if (!imgSrc.startsWith("http")) { // http로 시작하지 않으면 상대 경로로 간주
                        imgSrc = "https://www.visitbusan.net" + imgSrc;
                    }
                    item.put("imageSrc", imgSrc);
                    item.put("imageAlt", imgAlt);
                    item.put("itemLink", itemLink); // 상세 페이지 링크 추가 (필요하면 여기도 절대 경로로 변경)

                    crawledData.add(item);
                }
            }

            log.info("컨트롤러에서 크롤링한 아이템 수: {}", crawledData.size());
            model.addAttribute("crawledPlaces", crawledData); // Model에 데이터 추가

        } catch (IOException e) {
            log.error("크롤링 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "데이터를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
            return "errorPage"; // 오류 발생 시 보여줄 템플릿 (선택 사항)
        }
        return "places"; // 'places.html' 템플릿을 반환
    }
}