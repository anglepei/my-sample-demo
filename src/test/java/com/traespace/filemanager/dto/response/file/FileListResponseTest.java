package com.traespace.filemanager.dto.response.file;

import com.traespace.filemanager.dto.response.common.BasePageResponse;
import com.traespace.filemanager.vo.file.FileListItemVO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

/**
 * FileListResponse测试
 */
class FileListResponseTest {

    @Test
    void testFileListResponse() {
        // 测试文件列表响应
        FileListResponse response = new FileListResponse();
        List<FileListItemVO> items = new ArrayList<>();

        FileListItemVO item1 = new FileListItemVO();
        item1.setId(1L);
        item1.setFileName("test.xlsx");
        items.add(item1);

        response.setRecords(items);
        response.setTotal(1L);

        assertThat(response.getRecords()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(1L);
    }

    @Test
    void testExtendsBasePageResponse() {
        // 测试继承分页响应
        assertThat(BasePageResponse.class.isAssignableFrom(FileListResponse.class)).isTrue();
    }

    @Test
    void testEmptyRecords() {
        // 测试空记录
        FileListResponse response = new FileListResponse();
        response.setRecords(new ArrayList<>());
        response.setTotal(0L);

        assertThat(response.getRecords()).isEmpty();
        assertThat(response.getTotal()).isEqualTo(0L);
    }
}
