package com.traespace.filemanager.service.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.traespace.filemanager.entity.DataDetail;
import com.traespace.filemanager.entity.FileRecord;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.ErrorCode;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * FileService删除权限测试
 */
@ExtendWith(MockitoExtension.class)
class FileServiceDeleteTest {

    @Mock
    private FileRecordMapper fileRecordMapper;

    @Mock
    private DataDetailMapper dataDetailMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    private Long userId;
    private Long fileId;
    private FileRecord fileRecord;
    private User user;

    @BeforeEach
    void setUp() {
        userId = 1001L;
        fileId = 1L;

        fileRecord = new FileRecord();
        fileRecord.setId(fileId);
        fileRecord.setFileName("test.xlsx");
        fileRecord.setStatus(1);

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
    }

    @Test
    void testDeleteFile_AsOwner() {
        // 测试场景：普通用户删除自己的文件 - 应成功
        fileRecord.setUserId(userId); // 文件所有者是当前用户
        user.setRole(UserRole.USER);

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(userMapper.selectById(userId)).thenReturn(user);
        when(fileRecordMapper.updateById(any(FileRecord.class))).thenReturn(1);

        fileService.deleteFile(userId, fileId);

        verify(fileRecordMapper).selectById(fileId);
        verify(userMapper).selectById(userId);
        verify(fileRecordMapper).updateById(any(FileRecord.class));
        verify(dataDetailMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    void testDeleteFile_AsAdmin_OwnerFile() {
        // 测试场景：管理员删除其他用户的文件 - 应成功
        Long ownerUserId = 1002L;
        Long adminUserId = 1001L;

        fileRecord.setUserId(ownerUserId); // 文件所有者是1002
        user.setId(adminUserId);
        user.setRole(UserRole.ADMIN); // 当前用户是管理员

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(userMapper.selectById(adminUserId)).thenReturn(user);
        when(fileRecordMapper.updateById(any(FileRecord.class))).thenReturn(1);

        fileService.deleteFile(adminUserId, fileId);

        verify(fileRecordMapper).selectById(fileId);
        verify(userMapper).selectById(adminUserId);
        verify(fileRecordMapper).updateById(any(FileRecord.class));
        verify(dataDetailMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    void testDeleteFile_AsUser_OtherUserFile() {
        // 测试场景：普通用户删除其他用户的文件 - 应抛出PERMISSION_DENIED
        Long ownerUserId = 1002L;
        Long normalUserId = 1001L;

        fileRecord.setUserId(ownerUserId); // 文件所有者是1002
        user.setId(normalUserId);
        user.setRole(UserRole.USER); // 当前用户是普通用户

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(userMapper.selectById(normalUserId)).thenReturn(user);

        assertThatThrownBy(() -> fileService.deleteFile(normalUserId, fileId))
            .isInstanceOf(BizException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PERMISSION_DENIED);

        verify(fileRecordMapper).selectById(fileId);
        verify(userMapper).selectById(normalUserId);
        verify(fileRecordMapper, never()).updateById(any());
        verify(dataDetailMapper, never()).delete(any());
    }

    @Test
    void testDeleteFile_FileNotFound() {
        // 测试场景：文件不存在 - 应抛出FILE_NOT_FOUND
        when(fileRecordMapper.selectById(fileId)).thenReturn(null);

        assertThatThrownBy(() -> fileService.deleteFile(userId, fileId))
            .isInstanceOf(BizException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_NOT_FOUND);

        verify(fileRecordMapper).selectById(fileId);
        verify(userMapper, never()).selectById(any());
        verify(fileRecordMapper, never()).updateById(any());
    }

    @Test
    void testDeleteFile_UserNotFound() {
        // 测试场景：用户不存在 - 应抛出USER_NOT_FOUND
        fileRecord.setUserId(userId);

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(userMapper.selectById(userId)).thenReturn(null);

        assertThatThrownBy(() -> fileService.deleteFile(userId, fileId))
            .isInstanceOf(BizException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(fileRecordMapper).selectById(fileId);
        verify(userMapper).selectById(userId);
        verify(fileRecordMapper, never()).updateById(any());
    }

    @Test
    void testDeleteFile_AsAdmin_FileStatusSetToZero() {
        // 测试场景：管理员删除文件后，文件状态应被设为0
        user.setRole(UserRole.ADMIN);
        fileRecord.setUserId(userId + 1); // 其他用户的文件

        when(fileRecordMapper.selectById(fileId)).thenReturn(fileRecord);
        when(userMapper.selectById(userId)).thenReturn(user);

        fileService.deleteFile(userId, fileId);

        verify(fileRecordMapper).updateById(any(FileRecord.class));
    }
}
