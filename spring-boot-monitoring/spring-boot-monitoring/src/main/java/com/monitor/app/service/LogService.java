package com.monitor.app.service;

import com.monitor.app.entity.LogRecord;
import com.monitor.app.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 示例服务，用于测试数据库监控和生成模拟数据
 */
@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;
    
    private final Random random = new Random();
    private final List<String> actions = Arrays.asList("LOGIN", "LOGOUT", "CREATE", "UPDATE", "DELETE", "VIEW");
    
    @PostConstruct
    public void init() {
        // 初始化一些日志数据
        for (int i = 0; i < 10; i++) {
            createRandomLog();
        }
    }
    
    /**
     * 创建日志记录
     */
    public LogRecord createLog(String action, String description) {
        LogRecord logRecord = new LogRecord(action, description);
        return logRepository.save(logRecord);
    }
    
    /**
     * 创建随机日志记录
     */
    public LogRecord createRandomLog() {
        String action = actions.get(random.nextInt(actions.size()));
        String description = "系统自动生成的日志记录 #" + System.currentTimeMillis();
        return createLog(action, description);
    }
    
    /**
     * 获取所有日志记录
     */
    public List<LogRecord> getAllLogs() {
        return logRepository.findAll();
    }
    
    /**
     * 定时任务，每分钟生成一条随机日志
     */
    @Scheduled(fixedRate = 60000)
    public void generateRandomLog() {
        createRandomLog();
    }
}
