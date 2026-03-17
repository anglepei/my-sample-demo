package com.traespace.filemanager.controller.template;

import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.service.template.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模板控制器
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Tag(name = "模板接口", description = "生成Excel/CSV模板")
@RestController
@RequestMapping("/api/template")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * 下载Excel模板
     */
    @Operation(summary = "下载Excel模板")
    @GetMapping(value = "/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        Long userId = UserContext.getUserId();
        byte[] excelBytes = templateService.generateExcelTemplate(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "template.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    /**
     * 下载CSV模板
     */
    @Operation(summary = "下载CSV模板")
    @GetMapping(value = "/csv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadCsvTemplate() {
        Long userId = UserContext.getUserId();
        byte[] csvBytes = templateService.generateCsvTemplate(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "template.csv");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
}
