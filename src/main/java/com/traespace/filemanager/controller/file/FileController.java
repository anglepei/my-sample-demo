package com.traespace.filemanager.controller.file;

import com.traespace.filemanager.annotation.Log;
import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.dto.response.file.FileDetailResponse;
import com.traespace.filemanager.dto.response.file.FileListResponse;
import com.traespace.filemanager.enums.OperationType;
import com.traespace.filemanager.service.file.FileService;
import com.traespace.filemanager.service.file.FileService.FileDownloadResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件控制器
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Slf4j
@Tag(name = "文件接口", description = "文件上传、下载、查询、删除")
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传文件
     */
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @Log(value = OperationType.UPLOAD, description = "上传文件")
    public Result<Long> uploadFile(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();
        Long fileId = fileService.uploadFile(userId, file);
        return Result.success(fileId);
    }

    /**
     * 获取文件列表
     */
    @Operation(summary = "获取文件列表")
    @GetMapping("/list")
    public Result<FileListResponse> getFileList(@Valid BasePageRequest request) {
        Long userId = UserContext.getUserId();
        FileListResponse response = fileService.getFileList(userId, request);
        return Result.success(response);
    }

    /**
     * 获取文件详情
     */
    @Operation(summary = "获取文件详情")
    @GetMapping("/detail/{fileId}")
    public Result<FileDetailResponse> getFileDetail(@PathVariable Long fileId,
                                                     @Valid BasePageRequest request) {
        Long userId = UserContext.getUserId();
        FileDetailResponse response = fileService.getFileDetail(userId, fileId, request);
        return Result.success(response);
    }

    /**
     * 下载文件
     */
    @Operation(summary = "下载文件")
    @GetMapping(value = "/download/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Log(value = OperationType.DOWNLOAD, description = "下载文件")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        log.info("========== [文件下载] 收到下载请求，fileId={} ==========", fileId);

        Long userId = UserContext.getUserId();
        log.info("[文件下载] userId={}", userId);

        FileDownloadResult fileData = fileService.downloadFileWithMetadata(userId, fileId);

        log.info("[文件下载] 文件生成完成，filename={}, size={} bytes", fileData.filename(), fileData.data().length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // 处理中文文件名编码
        String filename = fileData.filename();
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        headers.setContentDispositionFormData("attachment", encodedFilename);

        log.info("[文件下载] 返回文件，Content-Disposition: attachment; filename=\"{}\", bytes={}", encodedFilename, fileData.data().length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData.data());
    }

    /**
     * 测试端点 - 用于排查问题
     */
    @GetMapping("/test")
    public Result<String> test() {
        log.info("========== [测试端点] 被调用 ==========");
        return Result.success("测试成功");
    }

    /**
     * 删除文件
     */
    @Operation(summary = "删除文件")
    @DeleteMapping("/{fileId}")
    @Log(value = OperationType.DELETE, description = "删除文件")
    public Result<Void> deleteFile(@PathVariable Long fileId) {
        Long userId = UserContext.getUserId();
        fileService.deleteFile(userId, fileId);
        return Result.success();
    }
}
