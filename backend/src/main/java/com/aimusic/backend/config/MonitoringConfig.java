package com.aimusic.backend.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 监控配置类
 * 配置Actuator和Micrometer，用于应用监控和性能指标收集
 *
 * @author AI Music Team
 * @version 0.1.0
 */
@Configuration
public class MonitoringConfig {

    /**
     * 配置MeterRegistry，添加通用标签
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment environment) {
        return registry -> registry.config()
                .commonTags("application", "ai-music-backend")
                .commonTags("env", environment.getActiveProfiles()[0]);
    }

    /**
     * 配置TimedAspect，用于方法执行时间监控
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
} 