package com.busanit501.boot_project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // @Controller 로 변경
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // API 응답을 위해 추가

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Controller // 여기를 @Controller 로 변경해야 합니다.
@RequestMapping("/weather")
public class WeatherController {

    // 1. weather.html 페이지를 보여주는 메서드
    // 이 메서드가 클라이언트의 /weather 요청을 받아서 templates/board/weather.html 파일을 반환합니다.
    @GetMapping
    public String showWeatherPage() {
        return "board/weather"; // <--- 이 부분이 weather.html 파일의 경로를 나타냅니다.
    }

    // 2. 대기질 API 정보를 JSON 형태로 반환하는 메서드
    // 이 메서드는 @ResponseBody 를 사용하여 HTML 템플릿 대신 순수한 데이터를 반환하도록 합니다.
    // 만약 weather.html 페이지 내부에서 JavaScript (Ajax)로 이 API를 호출하고 싶다면,
    // 이 메서드에 다른 경로를 부여하는 것이 좋습니다. 예를 들어 "/weather/api"
    // @GetMapping("/api") 또는 @PostMapping("/api") 등으로 변경할 수 있습니다.
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<String> getAirQualityInfo(@RequestParam(defaultValue = "json") String resultType) {
        String serviceKey = "%2BVz%2FW9sXamaWikO6gKGJaBFtdw0Zq%2FK8TH9P12TYaZFD2y3eKjyZWfcqP5Wvl9KDMUc5JHyQFwyxGZd6%2B8kfZg%3D%3D";
        String urlStr = "http://apis.data.go.kr/6260000/AirQualityInfoService/getAirQualityInfoClassifiedByStation" +
                "?serviceKey=" + serviceKey +
                "&resultType=" + resultType +
                "&numOfRows=10" +
                "&pageNo=1";

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            BufferedReader br;

            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            if (responseCode >= 200 && responseCode < 300) {
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .body(sb.toString());
            } else {
                return ResponseEntity.status(responseCode)
                        .header("Content-Type", "text/plain; charset=UTF-8")
                        .body("API 호출 실패: " + sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "text/plain; charset=UTF-8")
                    .body("대기질 정보를 불러오는 데 실패했습니다: " + e.getMessage());
        }
    }

}