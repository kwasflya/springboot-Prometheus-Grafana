package com.monitor.app.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DiskSpaceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // 检查系统磁盘空间
        long freeSpace = new File("/").getFreeSpace();
        long totalSpace = new File("/").getTotalSpace();
        double freeSpacePercent = (double) freeSpace / totalSpace * 100;
        
        if (freeSpacePercent < 10) {
            return Health.down()
                    .withDetail("error", "磁盘空间不足")
                    .withDetail("freeSpace", freeSpace)
                    .withDetail("totalSpace", totalSpace)
                    .withDetail("freeSpacePercent", String.format("%.2f%%", freeSpacePercent))
                    .build();
        }
        
        return Health.up()
                .withDetail("freeSpace", freeSpace)
                .withDetail("totalSpace", totalSpace)
                .withDetail("freeSpacePercent", String.format("%.2f%%", freeSpacePercent))
                .build();
    }
}
