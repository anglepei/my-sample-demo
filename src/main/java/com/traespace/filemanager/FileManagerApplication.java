package com.traespace.filemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 文件上传下载校验管理系统 - 主应用类
 *
 * @author Traespace
 * @since 2024-03-17
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class FileManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileManagerApplication.class, args);
    }
}
