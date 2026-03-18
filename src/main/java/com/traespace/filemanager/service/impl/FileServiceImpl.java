package com.traespace.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.dto.response.file.FileDetailResponse;
import com.traespace.filemanager.dto.response.file.FileListResponse;
import com.traespace.filemanager.entity.DataDetail;
import com.traespace.filemanager.entity.FileRecord;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.ErrorCode;
import com.traespace.filemanager.enums.FileType;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.DataDetailMapper;
import com.traespace.filemanager.mapper.FileRecordMapper;
import com.traespace.filemanager.mapper.UserMapper;
import com.traespace.filemanager.service.field.FieldConfigService;
import com.traespace.filemanager.service.file.FileService;
import com.traespace.filemanager.util.CsvUtil;
import com.traespace.filemanager.util.ExcelUtil;
import com.traespace.filemanager.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private final FileRecordMapper fileRecordMapper;
    private final DataDetailMapper dataDetailMapper;
    private final FieldConfigService fieldConfigService;
    private final UserMapper userMapper;

    public FileServiceImpl(FileRecordMapper fileRecordMapper,
                          DataDetailMapper dataDetailMapper,
                          FieldConfigService fieldConfigService,
                          UserMapper userMapper) {
        this.fileRecordMapper = fileRecordMapper;
        this.dataDetailMapper = dataDetailMapper;
        this.fieldConfigService = fieldConfigService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadFile(Long userId, MultipartFile file) {
        log.info("[文件上传] 开始上传，userId={}, filename={}", userId, file.getOriginalFilename());

        // 校验文件
        if (file.isEmpty()) {
            throw new BizException(ErrorCode.FILE_FORMAT_ERROR);
        }

        String originalFilename = file.getOriginalFilename();
        FileType fileType = getFileType(originalFilename);
        log.info("[文件上传] 文件类型={}", fileType);

        // 解析文件数据
        List<Map<String, String>> data;
        try {
            if (fileType == FileType.XLSX || fileType == FileType.XLS) {
                data = ExcelUtil.readExcel(file);
            } else {
                data = CsvUtil.readCsv(file);
            }
            log.info("[文件上传] 解析完成，数据总行数={}（包含表头）", data.size());
        } catch (Exception e) {
            log.error("[文件上传] 解析失败", e);
            throw new BizException(ErrorCode.FILE_FORMAT_ERROR);
        }

        // 校验数据格式
        validateData(data);
        log.info("[文件上传] 数据校验通过");

        // 获取用户信息
        User user = userMapper.selectById(userId);
        String username = user != null ? user.getUsername() : "user";

        // 生成系统文件名：用户名_日期_xx（xx表示当天第几个文件）
        String systemFileName = generateSystemFileName(userId, username, fileType);
        log.info("[文件上传] 生成系统文件名={}", systemFileName);

        // 保存文件记录
        FileRecord fileRecord = new FileRecord();
        fileRecord.setUserId(userId);
        fileRecord.setFileName(systemFileName);
        fileRecord.setOriginalName(originalFilename);
        fileRecord.setFileType(fileType);
        fileRecord.setUploadTime(LocalDateTime.now());
        fileRecord.setStatus(1);
        fileRecord.setRowCount(data.size()); // data只包含数据行，不包含表头
        fileRecordMapper.insert(fileRecord);
        log.info("[文件上传] 文件记录已保存，fileId={}, rowCount={}", fileRecord.getId(), fileRecord.getRowCount());

        // 保存数据明细（data只包含数据行，不包含表头）
        saveDataDetails(fileRecord.getId(), data);

        log.info("[文件上传] 上传完成，fileId={}", fileRecord.getId());
        return fileRecord.getId();
    }

    /**
     * 生成系统文件名：用户名_日期_xx
     * 例如：user1_20240318_01
     */
    private String generateSystemFileName(Long userId, String username, FileType fileType) {
        // 获取今天的日期字符串
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询用户今天已上传的文件数量
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);

        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileRecord::getUserId, userId)
                .between(FileRecord::getUploadTime, todayStart, todayEnd);

        Long todayCount = fileRecordMapper.selectCount(wrapper);
        int sequence = (todayCount != null ? todayCount.intValue() : 0) + 1;

        // 生成序列号（01, 02, ..., 99）
        String sequenceStr = String.format("%02d", sequence);

        // 获取文件扩展名
        String extension = fileType == FileType.CSV ? ".csv" : ".xlsx";

        // 生成文件名：用户名_日期_xx.扩展名
        return username + "_" + dateStr + "_" + sequenceStr + extension;
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
        log.info("[文件详情] 查询文件详情，userId={}, fileId={}", userId, fileId);

        // 查询文件记录
        FileRecord fileRecord = fileRecordMapper.selectById(fileId);
        if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.FILE_NOT_FOUND);
        }

        // 查询用户名
        User user = userMapper.selectById(userId);
        String username = user != null ? user.getUsername() : null;

        // 查询数据明细
        LambdaQueryWrapper<DataDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDetail::getFileId, fileId)
                .orderByAsc(DataDetail::getRowNum);

        // TODO: 实现分页
        List<DataDetail> details = dataDetailMapper.selectList(wrapper);
        log.info("[文件详情] 查询到{}条数据明细", details.size());

        // 构建文件详情VO
        com.traespace.filemanager.vo.file.FileDetailVO fileVO =
                new com.traespace.filemanager.vo.file.FileDetailVO();
        fileVO.setId(fileRecord.getId());
        fileVO.setOriginalName(fileRecord.getOriginalName());
        fileVO.setFileType(fileRecord.getFileType());
        fileVO.setRowCount(fileRecord.getRowCount());
        fileVO.setUploadTime(fileRecord.getUploadTime());
        fileVO.setUsername(username);
        fileVO.setFieldConfigSnapshot(fileRecord.getFieldConfigSnapshot());

        // 构建响应
        FileDetailResponse response = new FileDetailResponse();
        response.setFile(fileVO);
        response.setList(convertToDetailVO(details));
        response.setPage(request.getPage());
        response.setSize(request.getSize());
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

        // CsvUtil.readCsv已经解析好数据，只返回数据行（不包含表头）
        // 校验第一行数据是否包含必需字段（通过字段名判断）
        if (!data.isEmpty()) {
            Map<String, String> firstRow = data.get(0);
            if (!firstRow.containsKey("序号") || !firstRow.containsKey("身份证号") || !firstRow.containsKey("手机号")) {
                throw new BizException(ErrorCode.FILE_HEADER_MISMATCH);
            }
        }

        // 校验每行数据（data已经不包含表头，从索引0开始就是数据行）
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            String idCard = row.get("身份证号");
            String phone = row.get("手机号");

            // 行号 = 数据行索引 + 1（Excel中从第2行开始是数据）
            int rowNumber = i + 2; // 第1行是表头，第2行开始是数据

            if (idCard != null && !idCard.trim().isEmpty() && !ValidationUtil.isValidIdCard(idCard)) {
                throw new BizException(ErrorCode.ID_CARD_ERROR,
                    String.format("第%d行身份证号格式错误: %s", rowNumber, idCard));
            }
            if (phone != null && !phone.trim().isEmpty() && !ValidationUtil.isValidPhone(phone)) {
                throw new BizException(ErrorCode.PHONE_ERROR,
                    String.format("第%d行手机号格式错误: %s", rowNumber, phone));
            }
        }
    }

    /**
     * 保存数据明细
     */
    private void saveDataDetails(Long fileId, List<Map<String, String>> data) {
        log.info("[数据保存] 开始保存数据明细，fileId={}, 数据总行数={}", fileId, data.size());

        // data只包含数据行，不包含表头，从索引0开始处理
        int insertCount = 0;
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            log.info("[数据保存] 处理第{}行数据: {}", i + 1, row);

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

            detail.setRowNum(i + 1); // 行号从1开始
            dataDetailMapper.insert(detail);
            insertCount++;
            log.info("[数据保存] 第{}行已插入，detailId={}", i + 1, detail.getId());
        }

        log.info("[数据保存] 完成，共插入{}条记录到t_data_detail", insertCount);
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
            log.info("[列表查询] 文件ID={}, fileName={}, 数据条数={}", record.getId(), record.getFileName(), count);
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
