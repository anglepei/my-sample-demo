package com.traespace.filemanager.dto.request.common;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * BasePageRequest分页请求测试
 */
class BasePageRequestTest {

    @Test
    void testDefaultConstructor() {
        // 测试默认构造函数
        BasePageRequest request = new BasePageRequest();
        assertThat(request.getPage()).isEqualTo(1);
        assertThat(request.getSize()).isEqualTo(10);
    }

    @Test
    void testConstructorWithArgs() {
        // 测试带参构造函数
        BasePageRequest request = new BasePageRequest(2, 20);
        assertThat(request.getPage()).isEqualTo(2);
        assertThat(request.getSize()).isEqualTo(20);
    }

    @Test
    void testSetters() {
        // 测试setter方法
        BasePageRequest request = new BasePageRequest();
        request.setPage(3);
        request.setSize(50);

        assertThat(request.getPage()).isEqualTo(3);
        assertThat(request.getSize()).isEqualTo(50);
    }

    @Test
    void testGetOffset() {
        // 测试计算偏移量
        BasePageRequest request = new BasePageRequest(2, 10);
        assertThat(request.getOffset()).isEqualTo(10);

        request.setPage(3);
        assertThat(request.getOffset()).isEqualTo(20);
    }
}
