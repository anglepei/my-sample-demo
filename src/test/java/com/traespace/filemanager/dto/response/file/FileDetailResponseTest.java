package com.traespace.filemanager.dto.response.file;

import com.traespace.filemanager.enums.FileType;
import com.traespace.filemanager.vo.file.DataDetailItemVO;
import com.traespace.filemanager.vo.file.FileDetailVO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FileDetailResponse测试
 */
class FileDetailResponseTest {

    @Test
    void testFileDetailResponse() {
        // 测试文件详情响应
        FileDetailResponse response = new FileDetailResponse();

        // 设置文件信息
        FileDetailVO file = new FileDetailVO();
        file.setId(1L);
        file.setOriginalName("test.xlsx");
        file.setFileType(FileType.XLSX);
        file.setRowCount(10);
        file.setUploadTime(LocalDateTime.now());
        file.setUsername("testuser");
        response.setFile(file);

        // 设置数据明细
        List<DataDetailItemVO> list = new ArrayList<>();
        DataDetailItemVO item = new DataDetailItemVO();
        item.setSeqNo("001");
        item.setIdCard("110101199001011234");
        item.setPhone("13800138000");
        item.setCustomFields(new HashMap<>());
        list.add(item);

        response.setList(list);
        response.setPage(1);
        response.setSize(10);
        response.setTotal(1L);

        assertThat(response.getFile().getId()).isEqualTo(1L);
        assertThat(response.getFile().getOriginalName()).isEqualTo("test.xlsx");
        assertThat(response.getList()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(1L);
    }

    @Test
    void testConstructor() {
        // 测试构造函数
        FileDetailVO file = new FileDetailVO();
        file.setId(1L);
        file.setOriginalName("data.csv");

        FileDetailResponse response = new FileDetailResponse(file, new ArrayList<>(), 1, 10, 0L);

        assertThat(response.getFile().getId()).isEqualTo(1L);
        assertThat(response.getFile().getOriginalName()).isEqualTo("data.csv");
        assertThat(response.getTotal()).isEqualTo(0L);
    }

    @Test
    void testEmptyDetails() {
        // 测试空详情
        FileDetailResponse response = new FileDetailResponse();
        FileDetailVO file = new FileDetailVO();
        file.setId(1L);
        response.setFile(file);
        response.setList(new ArrayList<>());
        response.setTotal(0L);

        assertThat(response.getList()).isEmpty();
    }

    @Test
    void testDeprecatedMethods() {
        // 测试兼容旧的方法
        FileDetailVO file = new FileDetailVO();
        file.setId(1L);
        file.setOriginalName("test.csv");

        FileDetailResponse response = new FileDetailResponse();
        response.setFile(file);

        assertThat(response.getFileId()).isEqualTo(1L);
        assertThat(response.getFileName()).isEqualTo("test.csv");
    }
}
