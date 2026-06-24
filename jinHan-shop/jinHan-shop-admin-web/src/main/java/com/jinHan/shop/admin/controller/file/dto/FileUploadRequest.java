package com.jinHan.shop.admin.controller.file.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 类名: FileUploadRequest
 * 描述: 文件上传请求DTO
 * 作者: xuzeyu
 * 创建时间: 2026/1/13
 */
@Data
public class FileUploadRequest {
    /**
     * 上传的文件
     */
    private MultipartFile file;
}
