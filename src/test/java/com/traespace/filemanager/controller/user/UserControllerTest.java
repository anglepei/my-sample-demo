package com.traespace.filemanager.controller.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserController测试
 */
class UserControllerTest {

    @Test
    void testControllerExists() {
        // 测试控制器类存在
        assertThat(UserController.class).isNotNull();
    }
}
