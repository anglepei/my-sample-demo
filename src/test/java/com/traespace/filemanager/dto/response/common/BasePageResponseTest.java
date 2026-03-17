package com.traespace.filemanager.dto.response.common;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

/**
 * BasePageResponse分页响应测试
 */
class BasePageResponseTest {

    @Test
    void testConstructorWithArgs() {
        // 测试带参构造函数
        List<String> list = Arrays.asList("a", "b", "c");
        BasePageResponse<String> response = new BasePageResponse<>(list, 100L);

        assertThat(response.getList()).isEqualTo(list);
        assertThat(response.getTotal()).isEqualTo(100L);
    }

    @Test
    void testSetters() {
        // 测试setter方法
        BasePageResponse<String> response = new BasePageResponse<>();
        List<String> list = Arrays.asList("x", "y", "z");
        response.setList(list);
        response.setTotal(50L);

        assertThat(response.getList()).isEqualTo(list);
        assertThat(response.getTotal()).isEqualTo(50L);
    }

    @Test
    void testEmptyConstructor() {
        // 测试默认构造函数
        BasePageResponse<String> response = new BasePageResponse<>();
        assertThat(response.getList()).isNull();
        assertThat(response.getTotal()).isNull();
    }

    @Test
    void testBuildMethod() {
        // 测试build方法
        List<String> list = Arrays.asList("1", "2", "3");
        BasePageResponse<String> response = BasePageResponse.build(list, 30L);

        assertThat(response.getList()).isEqualTo(list);
        assertThat(response.getTotal()).isEqualTo(30L);
    }
}
