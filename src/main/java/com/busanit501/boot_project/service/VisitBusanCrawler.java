package com.busanit501.boot_project.service;

import com.busanit501.boot_project.domain.Place;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisitBusanCrawler {

    private final String BASE_URL = "https://www.visitbusan.net"; // 기본 도메인 URL

    public List<Place> crawlPlaces() {
        List<Place> placeList = new ArrayList<>();
        String targetUrl = BASE_URL + "/index.do?menuCd=DOM_000000201002000000"; // 음식/맛집 페이지 URL

        System.out.println("크롤링 시작: " + targetUrl);

        try {
            // Jsoup으로 웹페이지에 연결 (타임아웃 10초 설정)
            Document doc = Jsoup.connect(targetUrl)
                    .timeout(10 * 1000)
                    .get();

            // *** 이 부분이 중요합니다! ***
            // 'box_actionImg3'가 이미지, 제목, 링크를 모두 포함하는 개별 아이템의 컨테이너이므로
            // 이 요소를 직접 선택하여 반복합니다.
            // 만약 'box_actionImg3'가 특정 상위 부모(예: .hot-item2) 안에만 나타난다면
            // ".hot-item2 .box_actionImg3"와 같이 더 구체적인 셀렉터를 사용할 수도 있습니다.
            // 하지만 일단 가장 핵심적인 'box_actionImg3'만을 사용하여 개별 아이템을 선택합니다.
            Elements elements = doc.select(".box.actionImg3");

            System.out.println("크롤링 대상 아이템 개수: " + elements.size());

            if (elements.isEmpty()) {
                System.err.println("오류: '.box.actionImg3' 요소를 찾지 못했습니다. 셀렉터가 웹페이지 구조와 일치하는지 다시 확인하세요.");
                // 디버깅을 위해 전체 HTML을 출력해볼 수도 있습니다. (주석 해제 후 재시도)
                // System.out.println("페이지 HTML (일부): \n" + doc.html().substring(0, Math.min(doc.html().length(), 2000)));
                return placeList; // 데이터를 찾지 못했으니 빈 리스트 반환
            }

            for (Element el : elements) { // 각 'box_actionImg3' 요소를 순회
                String title = "";
                String image = "";
                String link = "";

                try {
                    // 제목 추출: 'box_actionImg3' 요소 안에서 '.txt_action .tit'를 찾습니다.
                    Element titleElement = el.selectFirst(".txt_action .tit");
                    if (titleElement != null) {
                        title = titleElement.text().trim();
                    } else {
                        System.out.println("경고: 현재 아이템에서 제목(.txt_action .tit) 요소를 찾을 수 없습니다.");
                    }

                    // 이미지 URL 추출: 'box_actionImg3' 요소 안에서 'img' 태그를 찾습니다.
                    // 만약 이미지가 '.thumb' 클래스 안에 있다면 '.thumb img'로 변경하세요.
                    Element imageElement = el.selectFirst("img");
                    if (imageElement != null) {
                        String rawImage = imageElement.attr("src");
                        if (rawImage.startsWith("//")) {
                            image = "https:" + rawImage;
                        } else if (rawImage.startsWith("/")) {
                            image = BASE_URL + rawImage;
                        } else {
                            image = rawImage;
                        }
                    } else {
                        System.out.println("경고: 현재 아이템에서 이미지(img) 요소를 찾을 수 없습니다.");
                    }

                    // 링크 추출: 'box_actionImg3' 요소 안에서 'a' 태그를 찾습니다.
                    // 'box_actionImg3'의 바로 자식인 'a' 태그를 선택하는 것이 일반적입니다.
                    Element linkElement = el.selectFirst("a");
                    if (linkElement != null) {
                        String rawLink = linkElement.attr("href");
                        if (rawLink.startsWith("/")) {
                            link = BASE_URL + rawLink;
                        } else {
                            link = rawLink;
                        }
                    } else {
                        System.out.println("경고: 현재 아이템에서 링크(a) 요소를 찾을 수 없습니다.");
                    }

                    // 추출된 정보가 모두 유효할 경우에만 Place 객체에 추가
                    if (!title.isEmpty() && !image.isEmpty() && !link.isEmpty()) {
                        Place place = new Place(title, image, link);
                        placeList.add(place);
                        System.out.println("--- 추출 성공 ---");
                        System.out.println("제목: " + title);
                        System.out.println("이미지 URL: " + image);
                        System.out.println("링크 URL: " + link);
                        System.out.println("-----------------");
                    } else {
                        System.out.println("경고: 불완전한 데이터가 추출되었습니다 (비어있는 필드 있음). 이 아이템은 추가되지 않습니다.");
                        // 어떤 HTML 블록에서 문제가 발생했는지 확인하기 위해 해당 아이템의 HTML 일부 출력 (옵션)
                        // System.out.println("문제 아이템 HTML 일부: " + el.html().substring(0, Math.min(el.html().length(), 200)) + "...");
                    }

                } catch (Exception e) {
                    System.err.println("개별 아이템 처리 중 예외 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("크롤링 전체 과정 중 치명적인 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("크롤링 종료. 최종 성공적으로 추출된 아이템 개수: " + placeList.size());
        return placeList;
    }
}