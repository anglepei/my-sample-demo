package com.traespace.filemanager.annotation;

import com.traespace.filemanager.enums.OperationType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 操作类型
     */
    OperationType value() default OperationType.OTHER;

    /**
     * 操作描述
     */
    String description() default "";
}
