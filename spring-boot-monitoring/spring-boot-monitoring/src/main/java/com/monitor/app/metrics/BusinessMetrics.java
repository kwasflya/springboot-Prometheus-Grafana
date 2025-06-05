package com.monitor.app.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义业务指标监控
 * 可根据实际业务需求扩展
 */
@Component
public class BusinessMetrics {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeUsers = new AtomicInteger(0);
    private final AtomicInteger pendingTasks = new AtomicInteger(0);
    private final AtomicInteger systemStatus = new AtomicInteger(1); // 1=正常, 0=异常

    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        registerBusinessMetrics();
    }

    private void registerBusinessMetrics() {
        // 活跃用户数
        Gauge.builder("business.users.active", activeUsers, AtomicInteger::get)
             .description("当前活跃用户数")
             .register(meterRegistry);
        
        // 待处理任务数
        Gauge.builder("business.tasks.pending", pendingTasks, AtomicInteger::get)
             .description("待处理任务数")
             .register(meterRegistry);
        
        // 系统状态
        Gauge.builder("business.system.status", systemStatus, AtomicInteger::get)
             .description("系统状态: 1=正常, 0=异常")
             .register(meterRegistry);
        
        // 业务操作计数器
        Counter.builder("business.operations")
               .tag("type", "login")
               .description("业务操作计数")
               .register(meterRegistry);
        
        Counter.builder("business.operations")
               .tag("type", "logout")
               .description("业务操作计数")
               .register(meterRegistry);
        
        Counter.builder("business.operations")
               .tag("type", "transaction")
               .description("业务操作计数")
               .register(meterRegistry);
    }

    // 更新活跃用户数
    public void setActiveUsers(int count) {
        activeUsers.set(count);
    }

    // 增加活跃用户数
    public void incrementActiveUsers() {
        activeUsers.incrementAndGet();
    }

    // 减少活跃用户数
    public void decrementActiveUsers() {
        activeUsers.decrementAndGet();
    }

    // 更新待处理任务数
    public void setPendingTasks(int count) {
        pendingTasks.set(count);
    }

    // 增加待处理任务
    public void incrementPendingTasks() {
        pendingTasks.incrementAndGet();
    }

    // 减少待处理任务
    public void decrementPendingTasks() {
        pendingTasks.decrementAndGet();
    }

    // 设置系统状态
    public void setSystemStatus(boolean normal) {
        systemStatus.set(normal ? 1 : 0);
    }

    // 记录登录操作
    public void recordLogin() {
        Counter.builder("business.operations")
               .tag("type", "login")
               .register(meterRegistry)
               .increment();
    }

    // 记录登出操作
    public void recordLogout() {
        Counter.builder("business.operations")
               .tag("type", "logout")
               .register(meterRegistry)
               .increment();
    }

    // 记录交易操作
    public void recordTransaction() {
        Counter.builder("business.operations")
               .tag("type", "transaction")
               .register(meterRegistry)
               .increment();
    }
}
