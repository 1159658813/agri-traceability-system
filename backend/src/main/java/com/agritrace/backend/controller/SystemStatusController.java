package com.agritrace.backend.controller;

import com.agritrace.backend.service.SystemStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemStatusController {
    private final SystemStatusService systemStatusService;
    public SystemStatusController(SystemStatusService systemStatusService) {
        this.systemStatusService = systemStatusService;
    }
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return Map.of(
                "code", 200,
                "message", systemStatusService.getSystemStatus()
        );
    }
    @GetMapping("/database")
    public Map<String, Object> checkDatabase() {
        boolean connected = systemStatusService.isDatabaseConnected();
        return Map.of(
                "connected", connected,
                "message", connected ? "数据库连接正常" : "数据库检查未通过"
        );
    }
}