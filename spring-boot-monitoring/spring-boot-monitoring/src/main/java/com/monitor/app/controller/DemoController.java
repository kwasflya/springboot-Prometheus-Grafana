package com.monitor.app.controller;

import com.monitor.app.metrics.BusinessMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 示例控制器，用于测试API监控功能
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    @Autowired
    private BusinessMetrics businessMetrics;

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        // 记录业务操作
        businessMetrics.incrementActiveUsers();
        businessMetrics.recordLogin();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, Spring Boot Monitoring!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("version", "1.0.0");
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
    
    @GetMapping("/simulate-load")
    public Map<String, Object> simulateLoad() {
        // 模拟CPU负载
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 1000) {
            // 空循环，消耗CPU
            Math.sqrt(Math.random() * 10000);
        }
        
        // 记录业务指标
        businessMetrics.incrementPendingTasks();
        businessMetrics.recordTransaction();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "负载模拟完成");
        result.put("duration", System.currentTimeMillis() - startTime);
        return result;
    }
    
    @GetMapping("/error-test")
    public Map<String, Object> errorTest() {
        // 随机抛出异常，用于测试错误监控
        if (Math.random() > 0.5) {
            throw new RuntimeException("模拟的随机错误");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "没有错误发生");
        return result;
    }
}
