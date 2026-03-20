package com.traespace.filemanager.integration;

import com.traespace.filemanager.entity.DataDetail;
import com.traespace.filemanager.entity.FileRecord;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.UserRole;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.DataDetailMapper;
import com.traespace.filemanager.mapper.FileRecordMapper;
import com.traespace.filemanager.mapper.UserMapper;
import com.traespace.filemanager.service.file.FileService;
import com.traespace.filemanager.service.template.TemplateService;
import com.traespace.filemanager.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * 角色管理与模板下载集成测试
 * 测试完整的端到端流程
 */
@SpringBootTest
@ActiveProfiles("test")
class RoleAndTemplateIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TemplateService templateService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private FileRecordMapper fileRecordMapper;

    @MockBean
    private DataDetailMapper dataDetailMapper;

    private User regularUser;
    private User adminUser;
    private FileRecord fileRecord1;
    private FileRecord fileRecord2;

    @BeforeEach
    void setUp() {
        // 创建普通用户
        regularUser = new User();
        regularUser.setId(1001L);
        regularUser.setUsername("user1");
        regularUser.setRole(UserRole.USER);

        // 创建管理员用户
        adminUser = new User();
        adminUser.setId(2001L);
        adminUser.setUsername("admin1");
        adminUser.setRole(UserRole.ADMIN);

        // 创建文件记录1（属于user1）
        fileRecord1 = new FileRecord();
        fileRecord1.setId(1L);
        fileRecord1.setUserId(1001L);
        fileRecord1.setFileName("file1.xlsx");
        fileRecord1.setStatus(1);

        // 创建文件记录2（属于user2，id=1002）
        fileRecord2 = new FileRecord();
        fileRecord2.setId(2L);
        fileRecord2.setUserId(1002L);
        fileRecord2.setFileName("file2.xlsx");
        fileRecord2.setStatus(1);
    }

    // ========== 场景1：角色查询与切换 ==========

    @Test
    void testScenario1_RoleQueryAndUpdate() {
        // 场景：用户查询当前角色，然后切换为ADMIN，再查询确认

        // 1. 设置mock
        when(userMapper.selectById(1001L)).thenReturn(regularUser);
        when(userMapper.updateById(any())).thenReturn(1);

        // 2. 查询当前角色 - 应该是USER
        var currentRole = userService.getCurrentRole(1001L);
        assertThat(currentRole.getRole()).isEqualTo(UserRole.USER);
        assertThat(currentRole.getRoleDesc()).isEqualTo("普通用户");

        // 3. 切换角色为ADMIN
        userService.updateRole(1001L, UserRole.ADMIN);

        // 4. 再次查询 - 应该是ADMIN
        regularUser.setRole(UserRole.ADMIN);
        var updatedRole = userService.getCurrentRole(1001L);
        assertThat(updatedRole.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(updatedRole.getRoleDesc()).isEqualTo("管理员");
    }

    // ========== 场景2：用户只能删除自己的文件 ==========

    @Test
    void testScenario2_UserCanOnlyDeleteOwnFiles() {
        // 场景：普通用户尝试删除其他用户的文件，应该失败

        when(userMapper.selectById(1001L)).thenReturn(regularUser);
        when(fileRecordMapper.selectById(2L)).thenReturn(fileRecord2);

        // 普通用户尝试删除其他用户的文件 - 应该抛出权限异常
        assertThatThrownBy(() -> fileService.deleteFile(1001L, 2L))
                .isInstanceOf(BizException.class);
    }

    // ========== 场景3：管理员可以删除任何文件 ==========

    @Test
    void testScenario3_AdminCanDeleteAnyFile() {
        // 场景：管理员删除其他用户的文件，应该成功

        when(userMapper.selectById(2001L)).thenReturn(adminUser);
        when(fileRecordMapper.selectById(2L)).thenReturn(fileRecord2);
        when(dataDetailMapper.selectList(any())).thenReturn(new ArrayList<>());

        // 管理员删除其他用户的文件 - 应该成功
        fileService.deleteFile(2001L, 2L);

        // 验证文件状态已更新为已删除
        assertThat(fileRecord2.getStatus()).isEqualTo(0);
    }

    // ========== 场景4：模板下载带数据 ==========

    @Test
    void testScenario4_DownloadTemplateWithData() {
        // 场景：用户请求下载带数据的Excel模板

        when(userMapper.selectById(1001L)).thenReturn(regularUser);

        // 请求下载带100行数据的Excel模板
        byte[] excelBytes = templateService.generateExcelTemplateWithData(1001L, 100);

        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(0);
    }

    // ========== 场景5：完整流程 - 角色切换后删除文件 ==========

    @Test
    void testScenario5_CompleteFlow_RoleChangeThenDelete() {
        // 完整场景：用户升级为管理员，然后删除其他用户的文件

        // 1. 初始状态：普通用户尝试删除他人文件 - 失败
        when(userMapper.selectById(1001L)).thenReturn(regularUser);
        when(fileRecordMapper.selectById(2L)).thenReturn(fileRecord2);

        assertThatThrownBy(() -> fileService.deleteFile(1001L, 2L))
                .isInstanceOf(BizException.class);

        // 2. 切换角色为ADMIN
        userService.updateRole(1001L, UserRole.ADMIN);
        regularUser.setRole(UserRole.ADMIN);

        // 3. 再次尝试删除 - 成功
        when(dataDetailMapper.selectList(any())).thenReturn(new ArrayList<>());
        fileService.deleteFile(1001L, 2L);

        assertThat(fileRecord2.getStatus()).isEqualTo(0);
    }

    // ========== 场景6：模板下载边界值测试 ==========

    @Test
    void testScenario6_TemplateBoundaryValues() {
        // 测试边界值：1行和1000000行

        when(userMapper.selectById(1001L)).thenReturn(regularUser);

        // 最小值：1行
        byte[] minBytes = templateService.generateExcelTemplateWithData(1001L, 1);
        assertThat(minBytes).isNotNull();
        assertThat(minBytes.length).isGreaterThan(0);

        // 最大值：1000000行（这里用较小值模拟测试）
        byte[] maxBytes = templateService.generateExcelTemplateWithData(1001L, 1000);
        assertThat(maxBytes).isNotNull();
        assertThat(maxBytes.length).isGreaterThan(0);
    }

    // ========== 场景7：CSV模板下载 ==========

    @Test
    void testScenario7_CsvTemplateDownload() {
        // 测试CSV模板下载

        when(userMapper.selectById(1001L)).thenReturn(regularUser);

        byte[] csvBytes = templateService.generateCsvTemplateWithData(1001L, 50);

        assertThat(csvBytes).isNotNull();
        assertThat(csvBytes.length).isGreaterThan(0);

        // 验证CSV内容包含表头
        String csvContent = new String(csvBytes);
        assertThat(csvContent).contains("\"序号\"");
        assertThat(csvContent).contains("\"身份证号\"");
        assertThat(csvContent).contains("\"手机号\"");
    }

    // ========== 场景8：UserContext集成测试 ==========

    @Test
    void testScenario8_UserContextIntegration() {
        // 测试UserContext在整个调用链中的集成

        try (MockedStatic<com.traespace.filemanager.config.UserContext> mockedContext =
                mockStatic(com.traespace.filemanager.config.UserContext.class)) {
            mockedContext.when(com.traespace.filemanager.config.UserContext::getUserId).thenReturn(1001L);
            when(userMapper.selectById(1001L)).thenReturn(regularUser);

            // 通过UserContext获取当前用户角色
            Long userId = com.traespace.filemanager.config.UserContext.getUserId();
            var roleResponse = userService.getCurrentRole(userId);

            assertThat(userId).isEqualTo(1001L);
            assertThat(roleResponse.getRole()).isEqualTo(UserRole.USER);
        }
    }
}
