package com.traespace.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.dto.response.file.FileDetailResponse;
import com.traespace.filemanager.dto.response.file.FileListResponse;
import com.traespace.filemanager.entity.DataDetail;
import com.traespace.filemanager.entity.FileRecord;
import com.traespace.filemanager.enums.ErrorCode;
import com.traespace.filemanager.enums.FileType;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.DataDetailMapper;
import com.traespace.filemanager.mapper.FileRecordMapper;
import com.traespace.filemanager.service.field.FieldConfigService;
import com.traespace.filemanager.service.file.FileService;
import com.traespace.filemanager.util.CsvUtil;
import com.traespace.filemanager.util.ExcelUtil;
import com.traespace.filemanager.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Service
public class FileServiceImpl implements FileService {

    private final FileRecordMapper fileRecordMapper;
    private final DataDetailMapper dataDetailMapper;
    private final FieldConfigService fieldConfigService;

    public FileServiceImpl(FileRecordMapper fileRecordMapper,
                          DataDetailMapper dataDetailMapper,
                          FieldConfigService fieldConfigService) {
        this.fileRecordMapper = fileRecordMapper;
        this.dataDetailMapper = dataDetailMapper;
        this.fieldConfigService = fieldConfigService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadFile(Long userId, MultipartFile file) {
        // 校验文件
        if (file.isEmpty()) {
            throw new BizException(ErrorCode.FILE_FORMAT_ERROR);
        }

        String filename = file.getOriginalFilename();
        FileType fileType = getFileType(filename);

        // 解析文件数据
        List<Map<String, String>> data;
        try {
            if (fileType == FileType.XLSX || fileType == FileType.XLS) {
                data = ExcelUtil.readExcel(file);
            } else {
                data = CsvUtil.readCsv(file);
            }
        } catch (Exception e) {
            throw new BizException(ErrorCode.FILE_FORMAT_ERROR);
        }

        // 校验数据格式
        validateData(data);

        // 保存文件记录
        FileRecord fileRecord = new FileRecord();
        fileRecord.setUserId(userId);
        fileRecord.setFileName(filename);
        fileRecord.setFileType(fileType);
        fileRecord.setUploadTime(LocalDateTime.now());
        fileRecord.setStatus(1);
        fileRecordMapper.insert(fileRecord);

        // 保存数据明细
        saveDataDetails(fileRecord.getId(), data);

        return fileRecord.getId();
    }

    @Override
    public FileListResponse getFileList(Long userId, BasePageRequest request) {
        Page<FileRecord> page = new Page<>(request.getPage(), request.getSize());

        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileRecord::getUserId, userId)
                .orderByDesc(FileRecord::getUploadTime);

        IPage<FileRecord> result = fileRecordMapper.selectPage(page, wrapper);

        FileListResponse response = new FileListResponse();
        response.setRecords(convertToListVO(result.getRecords()));
        response.setTotal(result.getTotal());

        return response;
    }

    @Override
    public FileDetailResponse getFileDetail(Long userId, Long fileId, BasePageRequest request) {
        // 查询文件记录
        FileRecord fileRecord = fileRecordMapper.selectById(fileId);
        if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.FILE_NOT_FOUND);
        }

        // 查询数据明细
        LambdaQueryWrapper<DataDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDetail::getFileId, fileId);

        // TODO: 实现分页
        List<DataDetail> details = dataDetailMapper.selectList(wrapper);

        FileDetailResponse response = new FileDetailResponse();
        response.setFileId(fileId);
        response.setFileName(fileRecord.getFileName());
        response.setDetails(convertToDetailVO(details));
        response.setTotal((long) details.size());

        return response;
    }

    @Override
    public byte[] downloadFile(Long userId, Long fileId) {
        // 查询文件记录
        FileRecord fileRecord = fileRecordMapper.selectById(fileId);
        if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.FILE_NOT_FOUND);
        }

        // 查询数据明细
        LambdaQueryWrapper<DataDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDetail::getFileId, fileId)
                .orderByAsc(DataDetail::getRowNum);

        List<DataDetail> details = dataDetailMapper.selectList(wrapper);

        // 生成文件
        List<Map<String, String>> data = convertToMapList(details);
        List<String> headers = buildHeaders();

        if (fileRecord.getFileType() == FileType.XLSX || fileRecord.getFileType() == FileType.XLS) {
            return ExcelUtil.generateExcel(data, headers);
        } else {
            return CsvUtil.generateCsv(data, headers);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long userId, Long fileId) {
        FileRecord fileRecord = fileRecordMapper.selectById(fileId);
        if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.FILE_NOT_FOUND);
        }

        // 软删除文件记录
        fileRecord.setStatus(0);
        fileRecordMapper.updateById(fileRecord);

        // 删除数据明细
        LambdaQueryWrapper<DataDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDetail::getFileId, fileId);
        dataDetailMapper.delete(wrapper);
    }

    /**
     * 获取文件类型
     */
    private FileType getFileType(String filename) {
        if (filename == null) {
            throw new BizException(ErrorCode.FILE_FORMAT_ERROR);
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "xlsx" -> FileType.XLSX;
            case "xls" -> FileType.XLS;
            case "csv" -> FileType.CSV;
            default -> throw new BizException(ErrorCode.FILE_FORMAT_ERROR);
        };
    }

    /**
     * 校验数据格式
     */
    private void validateData(List<Map<String, String>> data) {
        if (data == null || data.isEmpty()) {
            throw new BizException(ErrorCode.FILE_FORMAT_ERROR);
        }

        // 校验表头
        Map<String, String> firstRow = data.get(0);
        if (!firstRow.containsKey("序号") || !firstRow.containsKey("身份证号") || !firstRow.containsKey("手机号")) {
            throw new BizException(ErrorCode.FILE_HEADER_MISMATCH);
        }

        // 校验每行数据
        for (int i = 1; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            String idCard = row.get("身份证号");
            String phone = row.get("手机号");

            if (idCard != null && !ValidationUtil.isValidIdCard(idCard)) {
                throw new BizException(ErrorCode.ID_CARD_ERROR);
            }
            if (phone != null && !ValidationUtil.isValidPhone(phone)) {
                throw new BizException(ErrorCode.PHONE_ERROR);
            }
        }
    }

    /**
     * 保存数据明细
     */
    private void saveDataDetails(Long fileId, List<Map<String, String>> data) {
        // 跳过表头，从第2行开始
        for (int i = 1; i < data.size(); i++) {
            Map<String, String> row = data.get(i);

            DataDetail detail = new DataDetail();
            detail.setFileId(fileId);
            detail.setSeqNo(row.get("序号"));
            detail.setIdCard(row.get("身份证号"));
            detail.setPhone(row.get("手机号"));

            // 提取自定义字段
            Map<String, String> customFields = new HashMap<>(row);
            customFields.remove("序号");
            customFields.remove("身份证号");
            customFields.remove("手机号");
            detail.setCustomFields(customFields);

            detail.setRowNum(i);
            dataDetailMapper.insert(detail);
        }
    }

    /**
     * 转换为列表VO
     */
    private List<com.traespace.filemanager.vo.file.FileListItemVO> convertToListVO(List<FileRecord> records) {
        List<com.traespace.filemanager.vo.file.FileListItemVO> result = new ArrayList<>();
        for (FileRecord record : records) {
            com.traespace.filemanager.vo.file.FileListItemVO vo =
                    new com.traespace.filemanager.vo.file.FileListItemVO();
            vo.setId(record.getId());
            vo.setFileName(record.getFileName());
            vo.setFileType(record.getFileType());
            vo.setStatus(record.getStatus());
            vo.setUploadTime(record.getUploadTime());

            // 查询数据条数
            LambdaQueryWrapper<DataDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DataDetail::getFileId, record.getId());
            Long count = dataDetailMapper.selectCount(wrapper);
            vo.setDataCount(count.intValue());

            result.add(vo);
        }
        return result;
    }

    /**
     * 转换为详情VO
     */
    private List<com.traespace.filemanager.vo.file.DataDetailItemVO> convertToDetailVO(List<DataDetail> details) {
        List<com.traespace.filemanager.vo.file.DataDetailItemVO> result = new ArrayList<>();
        for (DataDetail detail : details) {
            com.traespace.filemanager.vo.file.DataDetailItemVO vo =
                    new com.traespace.filemanager.vo.file.DataDetailItemVO();
            vo.setSeqNo(detail.getSeqNo());
            vo.setIdCard(detail.getIdCard());
            vo.setPhone(detail.getPhone());
            vo.setCustomFields(detail.getCustomFields());
            result.add(vo);
        }
        return result;
    }

    /**
     * 转换为Map列表
     */
    private List<Map<String, String>> convertToMapList(List<DataDetail> details) {
        List<Map<String, String>> result = new ArrayList<>();
        for (DataDetail detail : details) {
            Map<String, String> row = new HashMap<>();
            row.put("序号", detail.getSeqNo());
            row.put("身份证号", detail.getIdCard());
            row.put("手机号", detail.getPhone());
            if (detail.getCustomFields() != null) {
                row.putAll(detail.getCustomFields());
            }
            result.add(row);
        }
        return result;
    }

    /**
     * 构建表头
     */
    private List<String> buildHeaders() {
        // 固定字段 + 自定义字段（这里简化处理，实际应从字段配置获取）
        List<String> headers = new ArrayList<>();
        headers.add("序号");
        headers.add("身份证号");
        headers.add("手机号");
        return headers;
    }
}
