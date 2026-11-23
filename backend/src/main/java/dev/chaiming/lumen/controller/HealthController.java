package dev.chaiming.lumen.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "demo", true,
                "name", "lumen-desk-demo",
                "message", "这是公开作品集演示服务，数据均为虚构。"
        );
    }
}
