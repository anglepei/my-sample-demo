package com.traespace.filemanager.service.file;

import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.dto.response.file.FileDetailResponse;
import com.traespace.filemanager.dto.response.file.FileListResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 *
 * @author Traespace
 * @since 2024-03-17
 */
public interface FileService {

    /**
     * 上传文件并解析数据
     *
     * @param userId 用户ID
     * @param file   上传的文件
     * @return 文件ID
     */
    Long uploadFile(Long userId, MultipartFile file);

    /**
     * 获取文件列表
     *
     * @param userId  用户ID
     * @param request 分页请求
     * @return 文件列表
     */
    FileListResponse getFileList(Long userId, BasePageRequest request);

    /**
     * 获取文件详情（数据明细）
     *
     * @param userId  用户ID
     * @param fileId  文件ID
     * @param request 分页请求
     * @return 文件详情
     */
    FileDetailResponse getFileDetail(Long userId, Long fileId, BasePageRequest request);

    /**
     * 下载文件
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 文件字节数组
     */
    byte[] downloadFile(Long userId, Long fileId);

    /**
     * 删除文件
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     */
    void deleteFile(Long userId, Long fileId);
}
