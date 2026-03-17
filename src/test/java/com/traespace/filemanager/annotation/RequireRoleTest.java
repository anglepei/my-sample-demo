package com.traespace.filemanager.annotation;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RequireRole注解测试
 */
class RequireRoleTest {

    @Test
    void testAnnotationExists() {
        // 测试注解存在
        assertThat(RequireRole.class).isNotNull();
    }

    @Test
    void testAnnotationProperties() {
        // 测试注解属性
        assertThat(RequireRole.class.isAnnotation()).isTrue();
    }

    @Test
    void testAnnotationRetention() {
        // 测试注解保留策略
        Retention retention = RequireRole.class.getAnnotation(Retention.class);
        assertThat(retention).isNotNull();
        assertThat(retention.value()).isEqualTo(RetentionPolicy.RUNTIME);
    }

    @Test
    void testAnnotationTarget() {
        // 测试注解目标
        Target target = RequireRole.class.getAnnotation(Target.class);
        assertThat(target).isNotNull();
        assertThat(target.value()).contains(ElementType.METHOD);
    }

    @Test
    void testAnnotationValue() {
        // 测试注解值
        // 实际使用时需要在方法上测试
        assertThat(RequireRole.class.getDeclaredMethods()).hasSize(1);
    }
}
