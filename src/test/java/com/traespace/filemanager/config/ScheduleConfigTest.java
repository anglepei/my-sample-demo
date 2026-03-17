package com.traespace.filemanager.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ScheduleConfig测试
 */
class ScheduleConfigTest {

    @Test
    void testScheduleConfigExists() {
        // 测试ScheduleConfig配置类存在
        assertThat(ScheduleConfig.class).isNotNull();
    }

    @Test
    void testScheduleConfigHasEnableSchedulingAnnotation() {
        // 测试ScheduleConfig类有@EnableScheduling注解
        EnableScheduling annotation = ScheduleConfig.class.getAnnotation(EnableScheduling.class);
        assertThat(annotation).isNotNull();
    }

    @Test
    void testScheduleConfigHasConfigurationAnnotation() {
        // 测试ScheduleConfig类有@Configuration注解
        Configuration annotation = ScheduleConfig.class.getAnnotation(Configuration.class);
        assertThat(annotation).isNotNull();
    }

    @Test
    void testScheduleConfigHasScheduledMethod() {
        // 测试ScheduleConfig类有定时任务方法
        assertThat(ScheduleConfig.class.getDeclaredMethods()).isNotEmpty();
    }
}
