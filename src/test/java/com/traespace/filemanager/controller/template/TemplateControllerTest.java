package com.traespace.filemanager.controller.template;

import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.service.template.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TemplateController测试
 */
@ExtendWith(MockitoExtension.class)
class TemplateControllerTest {

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private TemplateController templateController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(templateController).build();
    }

    @Test
    void testControllerExists() {
        // 测试控制器类存在
        assertThat(TemplateController.class).isNotNull();
    }

    // ========== 带数据模板接口测试 ==========

    @Test
    void testDownloadExcelTemplateWithData() throws Exception {
        // 测试下载带数据的Excel模板
        byte[] testBytes = new byte[]{1, 2, 3, 4, 5};

        try (MockedStatic<UserContext> mockedContext = mockStatic(UserContext.class)) {
            mockedContext.when(UserContext::getUserId).thenReturn(1001L);
            when(templateService.generateExcelTemplateWithData(eq(1001L), eq(10))).thenReturn(testBytes);

            mockMvc.perform(get("/api/template/download/excelWithData")
                    .param("count", "10"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"data_template_with_data.xlsx\""));
        }
    }

    @Test
    void testDownloadCsvTemplateWithData() throws Exception {
        // 测试下载带数据的CSV模板
        byte[] testBytes = "seq_no,id_card,phone\n1,110101199001011234,13800138000\n".getBytes();

        try (MockedStatic<UserContext> mockedContext = mockStatic(UserContext.class)) {
            mockedContext.when(UserContext::getUserId).thenReturn(1001L);
            when(templateService.generateCsvTemplateWithData(eq(1001L), eq(10))).thenReturn(testBytes);

            mockMvc.perform(get("/api/template/download/csvWithData")
                    .param("count", "10"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"data_template_with_data.csv\""));
        }
    }

    @Test
    void testDownloadExcelTemplateWithData_InvalidCount() throws Exception {
        // 测试无效的数据条数（count=0，低于最小值1）
        mockMvc.perform(get("/api/template/download/excelWithData")
                        .param("count", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadCsvTemplateWithData_InvalidCount() throws Exception {
        // 测试无效的数据条数（count=0，低于最小值1）
        mockMvc.perform(get("/api/template/download/csvWithData")
                        .param("count", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadExcelTemplateWithData_MaxCount() throws Exception {
        // 测试最大数据条数（1000000）
        byte[] testBytes = new byte[]{1, 2, 3};

        try (MockedStatic<UserContext> mockedContext = mockStatic(UserContext.class)) {
            mockedContext.when(UserContext::getUserId).thenReturn(1001L);
            when(templateService.generateExcelTemplateWithData(eq(1001L), eq(1000000))).thenReturn(testBytes);

            mockMvc.perform(get("/api/template/download/excelWithData")
                    .param("count", "1000000"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void testDownloadExcelTemplateWithData_DefaultCount() throws Exception {
        // 测试默认数据条数（应该使用默认值10）
        byte[] testBytes = new byte[]{1, 2, 3};

        try (MockedStatic<UserContext> mockedContext = mockStatic(UserContext.class)) {
            mockedContext.when(UserContext::getUserId).thenReturn(1001L);
            when(templateService.generateExcelTemplateWithData(eq(1001L), eq(10))).thenReturn(testBytes);

            mockMvc.perform(get("/api/template/download/excelWithData"))
                    .andExpect(status().isOk());
        }
    }
}
