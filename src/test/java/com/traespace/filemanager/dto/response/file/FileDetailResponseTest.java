package com.traespace.filemanager.dto.response.file;

import com.traespace.filemanager.vo.file.DataDetailItemVO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

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
        response.setFileId(1L);
        response.setFileName("test.xlsx");

        List<DataDetailItemVO> details = new ArrayList<>();
        DataDetailItemVO item = new DataDetailItemVO();
        item.setSeqNo("001");
        item.setIdCard("110101199001011234");
        item.setPhone("13800138000");
        item.setCustomFields(new HashMap<>());
        details.add(item);

        response.setDetails(details);
        response.setTotal(1L);

        assertThat(response.getFileId()).isEqualTo(1L);
        assertThat(response.getFileName()).isEqualTo("test.xlsx");
        assertThat(response.getDetails()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(1L);
    }

    @Test
    void testConstructor() {
        // 测试构造函数
        FileDetailResponse response = new FileDetailResponse(1L, "data.csv", new ArrayList<>(), 0L);

        assertThat(response.getFileId()).isEqualTo(1L);
        assertThat(response.getFileName()).isEqualTo("data.csv");
        assertThat(response.getTotal()).isEqualTo(0L);
    }

    @Test
    void testEmptyDetails() {
        // 测试空详情
        FileDetailResponse response = new FileDetailResponse();
        response.setFileId(1L);
        response.setDetails(new ArrayList<>());
        response.setTotal(0L);

        assertThat(response.getDetails()).isEmpty();
    }
}
