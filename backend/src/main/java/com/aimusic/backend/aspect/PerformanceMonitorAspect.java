package com.aimusic.backend.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.TimeUnit;

/**
 * 性能监控切面
 * 用于监控方法执行时间、内存使用等性能指标
 *
 * @author AI Music Team
 * @version 0.1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PerformanceMonitorAspect {

    private final MeterRegistry meterRegistry;
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    /**
     * 定义切点 - 所有控制器
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {}

    /**
     * 定义切点 - 所有服务
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {}

    /**
     * 环绕通知 - 监控方法执行性能
     */
    @Around("controllerPointcut() || servicePointcut()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        // 记录执行前的内存使用
        long heapMemoryBefore = memoryMXBean.getHeapMemoryUsage().getUsed();
        long nonHeapMemoryBefore = memoryMXBean.getNonHeapMemoryUsage().getUsed();

        // 创建计时器
        Timer timer = Timer.builder("method.execution.time")
                .tag("class", signature.getDeclaringType().getSimpleName())
                .tag("method", signature.getName())
                .description("Method execution time")
                .register(meterRegistry);

        // 记录方法执行时间
        long startTime = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            timer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            return result;
        } catch (Throwable e) {
            // 记录异常
            meterRegistry.counter("method.execution.error",
                    "class", signature.getDeclaringType().getSimpleName(),
                    "method", signature.getName(),
                    "exception", e.getClass().getSimpleName())
                    .increment();
            throw e;
        } finally {
            // 记录内存使用变化
            long heapMemoryAfter = memoryMXBean.getHeapMemoryUsage().getUsed();
            long nonHeapMemoryAfter = memoryMXBean.getNonHeapMemoryUsage().getUsed();

            // 记录堆内存使用
            meterRegistry.gauge("memory.heap.used",
                    meterRegistry.gauge("memory.heap.used",
                            heapMemoryAfter - heapMemoryBefore));

            // 记录非堆内存使用
            meterRegistry.gauge("memory.nonheap.used",
                    meterRegistry.gauge("memory.nonheap.used",
                            nonHeapMemoryAfter - nonHeapMemoryBefore));

            // 记录详细日志
            if (log.isDebugEnabled()) {
                log.debug("方法执行性能指标 - {}: 执行时间={}ms, 堆内存变化={}bytes, 非堆内存变化={}bytes",
                        methodName,
                        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime),
                        heapMemoryAfter - heapMemoryBefore,
                        nonHeapMemoryAfter - nonHeapMemoryBefore);
            }
        }
    }
} 