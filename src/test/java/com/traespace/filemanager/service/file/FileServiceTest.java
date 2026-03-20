package com.traespace.filemanager.service.file;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.dto.response.file.FileDetailResponse;
import com.traespace.filemanager.dto.response.file.FileListResponse;
import com.traespace.filemanager.entity.DataDetail;
import com.traespace.filemanager.entity.FileRecord;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.FileType;
import com.traespace.filemanager.enums.UserRole;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.DataDetailMapper;
import com.traespace.filemanager.mapper.FileRecordMapper;
import com.traespace.filemanager.mapper.UserMapper;
import com.traespace.filemanager.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FileService测试
 */
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRecordMapper fileRecordMapper;

    @Mock
    private DataDetailMapper dataDetailMapper;

    @Mock
    private com.traespace.filemanager.service.field.FieldConfigService fieldConfigService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    private Long userId;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        userId = 1001L;
        mockFile = mock(MultipartFile.class);
    }

    @Test
    void testUploadEmptyFile() {
        // 测试上传空文件
        when(mockFile.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> fileService.uploadFile(userId, mockFile))
                .isInstanceOf(BizException.class);

        verify(fileRecordMapper, never()).insert(any());
    }

    @Test
    void testUploadFileWithNullFilename() {
        // 测试文件名为空
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn(null);

        assertThatThrownBy(() -> fileService.uploadFile(userId, mockFile))
                .isInstanceOf(BizException.class);

        verify(fileRecordMapper, never()).insert(any());
    }

    @Test
    void testUploadFileWithInvalidExtension() {
        // 测试不支持的文件格式
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");

        assertThatThrownBy(() -> fileService.uploadFile(userId, mockFile))
                .isInstanceOf(BizException.class);

        verify(fileRecordMapper, never()).insert(any());
    }

    @Test
    void testGetFileList() {
        // 测试获取文件列表
        BasePageRequest request = new BasePageRequest();
        request.setPage(1);
        request.setSize(10);

        Page<FileRecord> page = new Page<>(1, 10);
        List<FileRecord> records = new ArrayList<>();
        FileRecord record = new FileRecord();
        record.setId(1L);
        record.setFileName("test.xlsx");
        record.setFileType(FileType.XLSX);
        record.setUserId(userId);
        record.setUploadTime(LocalDateTime.now());
        record.setStatus(1);
        records.add(record);
        page.setRecords(records);
        page.setTotal(1);

        when(fileRecordMapper.selectPage(any(), any())).thenReturn(page);
        when(dataDetailMapper.selectCount(any())).thenReturn(100L);

        FileListResponse response = fileService.getFileList(userId, request);

        assertThat(response).isNotNull();
        assertThat(response.getRecords()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(1L);
        verify(fileRecordMapper).selectPage(any(), any());
    }

    @Test
    void testGetFileDetail() {
        // 测试获取文件详情
        Long fileId = 1L;

        FileRecord fileRecord = new FileRecord();
        fileRecord.setId(fileId);
        fileRecord.setFileName("test.xlsx");
        fileRecord.setUserId(userId);

        List<DataDetail> details = new ArrayList<>();
        DataDetail detail = new DataDetail();
        detail.setFileId(fileId);
        detail.setSeqNo("001");
        detail.setIdCard("110101199001011234");
        detail.setPhone("13800138000");
        detail.setCustomFields(new HashMap<>());
        details.add(detail);

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        BasePageRequest pageRequest = new BasePageRequest();
        pageRequest.setPage(1);
        pageRequest.setSize(10);

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(userMapper.selectById(userId)).thenReturn(user);
        when(dataDetailMapper.selectList(any())).thenReturn(details);

        FileDetailResponse response = fileService.getFileDetail(userId, fileId, pageRequest);

        assertThat(response).isNotNull();
        assertThat(response.getFileId()).isEqualTo(fileId);
        assertThat(response.getDetails()).hasSize(1);
        verify(fileRecordMapper).selectById(fileId);
        verify(dataDetailMapper).selectList(any());
    }

    @Test
    void testGetFileDetailNotFound() {
        // 测试获取不存在的文件详情
        Long fileId = 999L;

        when(fileRecordMapper.selectById(fileId)).thenReturn(null);

        assertThatThrownBy(() -> fileService.getFileDetail(userId, fileId, null))
                .isInstanceOf(BizException.class);

        verify(fileRecordMapper).selectById(fileId);
        verify(dataDetailMapper, never()).selectList(any());
    }

    @Test
    void testGetFileDetailWithWrongUser() {
        // 测试获取其他用户的文件详情
        Long fileId = 1L;
        Long wrongUserId = 999L;

        FileRecord fileRecord = new FileRecord();
        fileRecord.setId(fileId);
        fileRecord.setUserId(userId); // Different user

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);

        assertThatThrownBy(() -> fileService.getFileDetail(wrongUserId, fileId, null))
                .isInstanceOf(BizException.class);

        verify(fileRecordMapper).selectById(fileId);
        verify(dataDetailMapper, never()).selectList(any());
    }

    @Test
    void testDeleteFile() {
        // 测试删除文件
        Long fileId = 1L;

        FileRecord fileRecord = new FileRecord();
        fileRecord.setId(fileId);
        fileRecord.setUserId(userId);
        fileRecord.setStatus(1);

        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.USER);

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(userMapper.selectById(userId)).thenReturn(user);
        when(fileRecordMapper.updateById(any())).thenReturn(1);

        fileService.deleteFile(userId, fileId);

        verify(fileRecordMapper).selectById(fileId);
        verify(fileRecordMapper).updateById(any(FileRecord.class));
        verify(dataDetailMapper).delete(any());
    }

    @Test
    void testDeleteFileNotFound() {
        // 测试删除不存在的文件
        Long fileId = 999L;

        when(fileRecordMapper.selectById(fileId)).thenReturn(null);

        assertThatThrownBy(() -> fileService.deleteFile(userId, fileId))
                .isInstanceOf(BizException.class);

        verify(fileRecordMapper).selectById(fileId);
        verify(fileRecordMapper, never()).updateById(any());
    }

    @Test
    void testDownloadFile() {
        // 测试下载文件
        Long fileId = 1L;

        FileRecord fileRecord = new FileRecord();
        fileRecord.setId(fileId);
        fileRecord.setFileName("test.xlsx");
        fileRecord.setFileType(FileType.XLSX);
        fileRecord.setUserId(userId);

        List<DataDetail> details = new ArrayList<>();
        DataDetail detail = new DataDetail();
        detail.setSeqNo("001");
        detail.setIdCard("110101199001011234");
        detail.setPhone("13800138000");
        detail.setCustomFields(new HashMap<>());
        details.add(detail);

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(dataDetailMapper.selectList(any())).thenReturn(details);

        byte[] fileBytes = fileService.downloadFile(userId, fileId);

        assertThat(fileBytes).isNotEmpty();
        verify(fileRecordMapper).selectById(fileId);
        verify(dataDetailMapper).selectList(any());
    }

    @Test
    void testDownloadCsvFile() {
        // 测试下载CSV文件
        Long fileId = 1L;

        FileRecord fileRecord = new FileRecord();
        fileRecord.setId(fileId);
        fileRecord.setFileName("test.csv");
        fileRecord.setFileType(FileType.CSV);
        fileRecord.setUserId(userId);

        List<DataDetail> details = new ArrayList<>();
        DataDetail detail = new DataDetail();
        detail.setSeqNo("001");
        detail.setIdCard("110101199001011234");
        detail.setPhone("13800138000");
        detail.setCustomFields(new HashMap<>());
        details.add(detail);

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(dataDetailMapper.selectList(any())).thenReturn(details);

        byte[] fileBytes = fileService.downloadFile(userId, fileId);

        assertThat(fileBytes).isNotEmpty();
        verify(fileRecordMapper).selectById(fileId);
        verify(dataDetailMapper).selectList(any());
    }
}
