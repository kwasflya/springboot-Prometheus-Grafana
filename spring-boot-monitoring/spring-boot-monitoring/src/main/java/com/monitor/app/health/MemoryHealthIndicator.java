package com.monitor.app.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MemoryHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // 检查系统内存
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        if (memoryUsagePercent > 90) {
            return Health.down()
                    .withDetail("error", "内存使用率过高")
                    .withDetail("maxMemory", maxMemory)
                    .withDetail("totalMemory", totalMemory)
                    .withDetail("freeMemory", freeMemory)
                    .withDetail("usedMemory", usedMemory)
                    .withDetail("memoryUsagePercent", String.format("%.2f%%", memoryUsagePercent))
                    .build();
        }
        
        return Health.up()
                .withDetail("maxMemory", maxMemory)
                .withDetail("totalMemory", totalMemory)
                .withDetail("freeMemory", freeMemory)
                .withDetail("usedMemory", usedMemory)
                .withDetail("memoryUsagePercent", String.format("%.2f%%", memoryUsagePercent))
                .build();
    }
}
