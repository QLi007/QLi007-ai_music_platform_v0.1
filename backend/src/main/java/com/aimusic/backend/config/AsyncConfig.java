package com.aimusic.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务配置类
 * 配置异步任务执行器和异常处理
 *
 * @author AI Music Team
 * @version 0.1.0
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 配置音乐生成任务执行器
     */
    @Bean(name = "musicGenerationExecutor")
    public Executor musicGenerationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("music-generation-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.error("音乐生成任务队列已满，拒绝执行新任务");
            throw new RuntimeException("系统繁忙，请稍后重试");
        });
        executor.initialize();
        return executor;
    }

    /**
     * 配置默认异步任务执行器
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("async-task-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.error("异步任务队列已满，拒绝执行新任务");
            throw new RuntimeException("系统繁忙，请稍后重试");
        });
        executor.initialize();
        return executor;
    }

    /**
     * 配置异步任务异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("异步任务执行异常 - 方法: {}, 参数: {}, 异常: {}",
                    method.getName(),
                    params,
                    ex.getMessage(),
                    ex);
        };
    }
} 