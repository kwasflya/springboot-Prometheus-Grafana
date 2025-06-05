package com.monitor.app.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class ApiMetricsAspect {

    private final MeterRegistry meterRegistry;
    
    public ApiMetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerPointcut() {
    }
    
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMappingPointcut() {
    }
    
    @Around("restControllerPointcut() && requestMappingPointcut()")
    public Object measureApiPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String apiName = className + "." + methodName;
        
        // 计数器：API调用次数
        Counter.builder("api.calls")
               .tag("api", apiName)
               .description("API调用次数")
               .register(meterRegistry)
               .increment();
        
        // 计时器：API响应时间
        Timer timer = Timer.builder("api.response.time")
                          .tag("api", apiName)
                          .description("API响应时间")
                          .register(meterRegistry);
        
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            // 计数器：API错误次数
            Counter.builder("api.errors")
                   .tag("api", apiName)
                   .tag("exception", e.getClass().getSimpleName())
                   .description("API错误次数")
                   .register(meterRegistry)
                   .increment();
            throw e;
        } finally {
            long duration = System.nanoTime() - start;
            timer.record(duration, TimeUnit.NANOSECONDS);
        }
    }
}
