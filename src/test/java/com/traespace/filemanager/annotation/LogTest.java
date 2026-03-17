package com.traespace.filemanager.annotation;

import com.traespace.filemanager.enums.OperationType;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Log注解测试
 */
class LogTest {

    @Test
    void testLogAnnotationExists() {
        // 测试@Log注解存在
        assertThat(Log.class).isNotNull();
        assertThat(Log.class.isAnnotation()).isTrue();
    }

    @Test
    void testLogAnnotationTarget() {
        // 测试@Log注解目标为METHOD
        Target target = Log.class.getAnnotation(Target.class);
        assertThat(target).isNotNull();
        assertThat(target.value()).containsExactly(ElementType.METHOD);
    }

    @Test
    void testLogAnnotationRetention() {
        // 测试@Log注解保留策略为RUNTIME
        Retention retention = Log.class.getAnnotation(Retention.class);
        assertThat(retention).isNotNull();
        assertThat(retention.value()).isEqualTo(RetentionPolicy.RUNTIME);
    }

    @Test
    void testLogAnnotationDefaultValue() throws NoSuchMethodException {
        // 测试@Log注解默认值
        // 创建一个测试类来使用注解
        class TestClass {
            @Log
            public void testMethod() {
            }
        }

        Method method = TestClass.class.getMethod("testMethod");
        Log logAnnotation = method.getAnnotation(Log.class);

        assertThat(logAnnotation).isNotNull();
        assertThat(logAnnotation.value()).isEqualTo(OperationType.OTHER);
        assertThat(logAnnotation.description()).isEmpty();
    }

    @Test
    void testLogAnnotationWithValues() throws NoSuchMethodException {
        // 测试@Log注解带参数值
        class TestClass {
            @Log(value = OperationType.UPLOAD, description = "上传文件")
            public void testMethod() {
            }
        }

        Method method = TestClass.class.getMethod("testMethod");
        Log logAnnotation = method.getAnnotation(Log.class);

        assertThat(logAnnotation).isNotNull();
        assertThat(logAnnotation.value()).isEqualTo(OperationType.UPLOAD);
        assertThat(logAnnotation.description()).isEqualTo("上传文件");
    }
}
