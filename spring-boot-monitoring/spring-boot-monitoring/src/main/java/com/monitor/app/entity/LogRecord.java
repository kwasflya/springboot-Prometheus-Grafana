package com.monitor.app.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 示例实体类，用于测试数据库监控功能
 */
@Entity
@Data
public class LogRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String action;
    private String description;
    private LocalDateTime timestamp;
    
    // 默认构造函数
    public LogRecord() {
        this.timestamp = LocalDateTime.now();
    }
    
    // 带参数的构造函数
    public LogRecord(String action, String description) {
        this.action = action;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}
