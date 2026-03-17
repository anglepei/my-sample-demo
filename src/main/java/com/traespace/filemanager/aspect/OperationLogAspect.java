package com.traespace.filemanager.aspect;

import com.traespace.filemanager.annotation.Log;
import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.service.log.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 操作日志切面
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    private final OperationLogService operationLogService;

    public OperationLogAspect(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 环绕通知：拦截带@Log注解的方法
     */
    @Around("@annotation(com.traespace.filemanager.annotation.Log)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log logAnnotation = signature.getMethod().getAnnotation(Log.class);

        // 获取用户信息
        Long userId = UserContext.getUserId();
        String username = UserContext.getUsername();

        // 获取请求IP
        String clientIp = getClientIp();

        // 执行原方法
        Object result = joinPoint.proceed();

        // 保存操作日志
        try {
            operationLogService.saveLog(
                    userId,
                    username,
                    logAnnotation.value(),
                    logAnnotation.description(),
                    clientIp
            );
        } catch (Exception e) {
            log.error("[操作日志] 保存日志失败, userId={}, operation={}", userId, logAnnotation.value(), e);
        }

        return result;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }

        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
