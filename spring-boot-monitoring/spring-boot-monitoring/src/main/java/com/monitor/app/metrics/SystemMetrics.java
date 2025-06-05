package com.monitor.app.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

@Component
public class SystemMetrics {

    private final MeterRegistry meterRegistry;
    private final OperatingSystemMXBean operatingSystemMXBean;
    private final MemoryMXBean memoryMXBean;
    private final ThreadMXBean threadMXBean;
    private final Runtime runtime;

    public SystemMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.runtime = Runtime.getRuntime();
    }

    @PostConstruct
    public void init() {
        registerCpuMetrics();
        registerMemoryMetrics();
        registerThreadMetrics();
        registerGcMetrics();
    }

    private void registerCpuMetrics() {
        // CPU负载
        Gauge.builder("system.cpu.load", operatingSystemMXBean, OperatingSystemMXBean::getSystemLoadAverage)
             .description("系统CPU负载")
             .register(meterRegistry);
        
        // 可用处理器数量
        Gauge.builder("system.cpu.count", operatingSystemMXBean, OperatingSystemMXBean::getAvailableProcessors)
             .description("可用处理器数量")
             .register(meterRegistry);
    }

    private void registerMemoryMetrics() {
        // 堆内存使用情况
        Gauge.builder("jvm.memory.heap.used", memoryMXBean, bean -> bean.getHeapMemoryUsage().getUsed())
             .description("JVM堆内存使用量")
             .baseUnit("bytes")
             .register(meterRegistry);
        
        Gauge.builder("jvm.memory.heap.max", memoryMXBean, bean -> bean.getHeapMemoryUsage().getMax())
             .description("JVM堆内存最大值")
             .baseUnit("bytes")
             .register(meterRegistry);
        
        // 非堆内存使用情况
        Gauge.builder("jvm.memory.nonheap.used", memoryMXBean, bean -> bean.getNonHeapMemoryUsage().getUsed())
             .description("JVM非堆内存使用量")
             .baseUnit("bytes")
             .register(meterRegistry);
        
        // 系统内存
        Gauge.builder("system.memory.total", runtime, Runtime::totalMemory)
             .description("JVM总内存")
             .baseUnit("bytes")
             .register(meterRegistry);
        
        Gauge.builder("system.memory.free", runtime, Runtime::freeMemory)
             .description("JVM空闲内存")
             .baseUnit("bytes")
             .register(meterRegistry);
    }

    private void registerThreadMetrics() {
        // 线程数量
        Gauge.builder("jvm.threads.count", threadMXBean, ThreadMXBean::getThreadCount)
             .description("JVM线程数量")
             .register(meterRegistry);
        
        // 守护线程数量
        Gauge.builder("jvm.threads.daemon", threadMXBean, ThreadMXBean::getDaemonThreadCount)
             .description("JVM守护线程数量")
             .register(meterRegistry);
        
        // 峰值线程数
        Gauge.builder("jvm.threads.peak", threadMXBean, ThreadMXBean::getPeakThreadCount)
             .description("JVM峰值线程数")
             .register(meterRegistry);
    }

    private void registerGcMetrics() {
        // GC计数器由Spring Boot Actuator自动注册
        Counter.builder("jvm.gc.manual")
               .description("手动GC触发次数")
               .register(meterRegistry);
    }

    // 手动触发GC的方法，可用于测试
    public void performGc() {
        System.gc();
        Counter.builder("jvm.gc.manual")
               .description("手动GC触发次数")
               .register(meterRegistry)
               .increment();
    }
}
