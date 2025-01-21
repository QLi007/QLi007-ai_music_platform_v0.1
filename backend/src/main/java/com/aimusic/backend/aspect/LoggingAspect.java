package com.aimusic.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

/**
 * 日志切面
 * 用于记录方法调用、参数、返回值和执行时间等信息
 *
 * @author AI Music Team
 * @version 0.1.0
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

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
     * 环绕通知 - 记录控制器方法的调用
     */
    @Around("controllerPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, true);
    }

    /**
     * 环绕通知 - 记录服务方法的调用
     */
    @Around("servicePointcut()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, false);
    }

    /**
     * 记录方法执行的详细信息
     *
     * @param joinPoint 连接点
     * @param isController 是否为控制器方法
     * @return 方法执行结果
     * @throws Throwable 如果方法执行出错
     */
    private Object logMethodExecution(ProceedingJoinPoint joinPoint, boolean isController) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        
        // 记录请求信息
        if (isController) {
            logRequest(methodName);
        }

        // 记录方法参数
        log.debug("开始执行: {} 参数: {}", methodName, Arrays.toString(joinPoint.getArgs()));

        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            // 执行方法
            Object result = joinPoint.proceed();
            stopWatch.stop();

            // 记录执行结果
            log.debug("执行完成: {} 耗时: {}ms 返回值: {}", 
                    methodName, 
                    stopWatch.getTotalTimeMillis(),
                    result);

            return result;
        } catch (Throwable e) {
            stopWatch.stop();
            // 记录异常信息
            log.error("执行异常: {} 耗时: {}ms 异常: {}", 
                    methodName, 
                    stopWatch.getTotalTimeMillis(), 
                    e.getMessage(), 
                    e);
            throw e;
        }
    }

    /**
     * 记录HTTP请求信息
     *
     * @param methodName 方法名
     */
    private void logRequest(String methodName) {
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(attributes -> (ServletRequestAttributes) attributes)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> log.info("接收请求: {} {} {}", 
                        methodName,
                        request.getMethod(),
                        getRequestPath(request)));
    }

    /**
     * 获取请求路径
     *
     * @param request HTTP请求
     * @return 请求路径
     */
    private String getRequestPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString != null ? 
                request.getRequestURI() + "?" + queryString : 
                request.getRequestURI();
    }
} 