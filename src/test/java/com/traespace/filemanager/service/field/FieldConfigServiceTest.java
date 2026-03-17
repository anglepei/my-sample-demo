package com.traespace.filemanager.service.field;

import com.traespace.filemanager.dto.request.field.FieldConfigItem;
import com.traespace.filemanager.dto.request.field.FieldConfigRequest;
import com.traespace.filemanager.entity.FieldConfig;
import com.traespace.filemanager.enums.ErrorCode;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.FieldConfigMapper;
import com.traespace.filemanager.service.impl.FieldConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FieldConfigService测试
 */
@ExtendWith(MockitoExtension.class)
class FieldConfigServiceTest {

    @Mock
    private FieldConfigMapper fieldConfigMapper;

    @InjectMocks
    private FieldConfigServiceImpl fieldConfigService;

    private Long userId;
    private FieldConfigRequest request;
    private List<FieldConfigItem> items;

    @BeforeEach
    void setUp() {
        userId = 1001L;

        request = new FieldConfigRequest();
        items = new ArrayList<>();

        FieldConfigItem item1 = new FieldConfigItem();
        item1.setFieldName("联系地址");
        item1.setFieldType("TEXT");
        item1.setRequired(false);
        item1.setSortOrder(1);

        FieldConfigItem item2 = new FieldConfigItem();
        item2.setFieldName("联系电话");
        item2.setFieldType("TEXT");
        item2.setRequired(true);
        item2.setSortOrder(2);

        items.add(item1);
        items.add(item2);
        request.setFields(items);
    }

    @Test
    void testSaveFieldConfig() {
        // 测试保存字段配置
        when(fieldConfigMapper.insert(any(FieldConfig.class))).thenAnswer(invocation -> {
            FieldConfig config = invocation.getArgument(0);
            config.setId(1L);
            return 1;
        });

        fieldConfigService.saveFieldConfig(userId, request);

        verify(fieldConfigMapper, times(2)).insert(any(FieldConfig.class));
    }

    @Test
    void testGetFieldConfigByUserId() {
        // 测试获取用户字段配置
        List<FieldConfig> configs = new ArrayList<>();
        FieldConfig config1 = new FieldConfig();
        config1.setId(1L);
        config1.setUserId(userId);
        config1.setFieldName("联系地址");
        config1.setFieldType(com.traespace.filemanager.enums.FieldType.TEXT);
        config1.setRequired(false);
        config1.setSortOrder(1);

        configs.add(config1);

        when(fieldConfigMapper.selectList(any())).thenReturn(configs);

        List<FieldConfigItem> result = fieldConfigService.getFieldConfigByUserId(userId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFieldName()).isEqualTo("联系地址");
    }

    @Test
    void testDeleteFieldConfig() {
        // 测试删除字段配置
        // BaseMapper的delete方法接受Wrapper参数，返回删除的记录数
        when(fieldConfigMapper.delete(any())).thenReturn(2);

        fieldConfigService.deleteFieldConfig(userId);

        verify(fieldConfigMapper).delete(any());
    }
}
