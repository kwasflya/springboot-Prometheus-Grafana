package com.monitor.app.controller;

import com.monitor.app.entity.LogRecord;
import com.monitor.app.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志控制器，用于测试数据库监控功能
 */
@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping
    public List<LogRecord> getAllLogs() {
        return logService.getAllLogs();
    }
    
    @GetMapping("/generate")
    public Map<String, Object> generateLog() {
        LogRecord log = logService.createRandomLog();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "日志生成成功");
        response.put("log", log);
        return response;
    }
}
