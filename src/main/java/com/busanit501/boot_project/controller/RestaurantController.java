package com.busanit501.boot_project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets; // Charset.forName("EUC-KR") 사용 시 필요

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

    @GetMapping
    public String showRestaurantPage() {
        return "board/restaurant";
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<String> getFoodServiceInfo(@RequestParam(defaultValue = "json") String resultType) {
        String baseUrl = "https://apis.data.go.kr/6260000/FoodService/getFoodKr";
        String serviceKey = "%2BVz%2FW9sXamaWikO6gKGJaBFtdw0Zq%2FK8TH9P12TYaZFD2y3eKjyZWfcqP5Wvl9KDMUc5JHyQFwyxGZd6%2B8kfZg%3D%3D";
        int numOfRows = 10;
        int pageNo = 1;

        String urlStr = baseUrl +
                "?serviceKey=" + serviceKey +
                "&numOfRows=" + numOfRows +
                "&pageNo=" + pageNo +
                "&resultType=" + resultType;

        System.out.println("맛집 API 호출 URL: " + urlStr);

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            System.out.println("맛집 API 응답 코드: " + responseCode);

            String contentType = conn.getContentType();
            System.out.println("맛집 API Content-Type 헤더: " + contentType);

            BufferedReader br;
            // 응답 코드가 성공적일 때와 아닐 때 읽어올 스트림을 결정
            if (responseCode >= 200 && responseCode <= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                // 만약 여전히 한글이 깨진다면, 아래 EUC-KR 시도
                // br = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("EUC-KR")));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close(); // BufferedReader 닫기
            conn.disconnect(); // HttpURLConnection 닫기

            String apiResponse = sb.toString(); // 여기서 한 번만 선언 및 할당
            System.out.println("맛집 API 응답 본문: " + apiResponse); // 콘솔 출력로직은 유지

            // 최종적으로 ResponseEntity 반환
            if (responseCode >= 200 && responseCode <= 300) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
                return new ResponseEntity<>(apiResponse, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(responseCode).body("API 오류 응답: " + apiResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("백엔드에서 맛집 API 호출 중 오류 발생: " + e.getMessage());
        }
    }
}