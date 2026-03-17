package com.traespace.filemanager.interceptor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtInterceptor测试
 */
class JwtInterceptorTest {

    @Test
    void testInterceptorExists() {
        // 测试拦截器存在
        assertThat(JwtInterceptor.class).isNotNull();
    }
}
