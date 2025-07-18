package com.busanit501.boot_project.service;

import com.busanit501.boot_project.dto.PlaceDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisitBusanService {

    public List<PlaceDTO> getPlaceList() {
        List<PlaceDTO> placeList = new ArrayList<>();

        try {
            String url = "https://www.visitbusan.net/index.do?menuCd=DOM_000000201000000000";  // 원하는 페이지로 수정 가능
            Document doc = Jsoup.connect(url).get();

            Elements items = doc.select(".boardList li");

            for (Element item : items) {
                String title = item.select(".tit").text();
                String link = "https://www.visitbusan.net" + item.select("a").attr("href");
                String image = item.select("img").attr("src");

                PlaceDTO dto = new PlaceDTO();
                dto.setTitle(title);
                dto.setLink(link);
                dto.setImage(image);

                placeList.add(dto);
            }

            System.out.println("✅ 크롤링 성공, 총 " + placeList.size() + "개 아이템");

        } catch (Exception e) {
            System.out.println("크롤링 실패: " + e.getMessage());
        }

        return placeList;
    }
}
