package com.example.redis_sampling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MonitoringController {

    private final HealthEndpoint healthEndpoint;

    @GetMapping("/")
    public String index(Model model) {
        // Redis 컴포넌트의 상태 추출 (Spring Boot 3.x 헬스 체크 구조 대응)
        Status redisStatus;
        try {
            redisStatus = (Status) healthEndpoint.healthForPath("redis").getStatus();
        } catch (Exception e) {
            // Redis 컴포넌트를 찾을 수 없거나 예외 발생 시 DOWN 처리
            redisStatus = Status.DOWN;
        }
        
        boolean isRedisUp = Status.UP.equals(redisStatus);
        
        model.addAttribute("redisStatus", redisStatus.getCode());
        model.addAttribute("isRedisUp", isRedisUp);
        
        return "index";
    }
}
