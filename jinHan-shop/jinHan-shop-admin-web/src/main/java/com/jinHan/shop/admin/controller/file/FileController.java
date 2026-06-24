package com.jinHan.shop.admin.controller.file;

import com.aicommerce.common.exception.BusinessException;
import com.aicommerce.common.model.Result;
import com.aicommerce.start.file.service.FileFacadeImpl;
import com.aicommerce.log.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: FileController
 * 描述: 上传接口
 * 作者: xuzeyu
 * 创建时间: 2025/12/22
 */
@RestController
@RequestMapping("/public")
@Tag(name = "文件上传")
public class FileController {
    @Resource
    private FileFacadeImpl fileFacade;

    /**
     * 单文件上传
     */
    @Log(value = "单文件上传", operationType = "FILE_UPLOAD")
    @Operation(summary = "单文件上传")
    @PostMapping("/file/upload")
    Result<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            byte[] content = file.getBytes();
            String fileName = file.getOriginalFilename();
            String url = fileFacade.upload(content, fileName);
            return Result.success(url);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 批量文件上传
     */
    @Log(value = "批量文件上传", operationType = "FILE_BATCH_UPLOAD")
    @Operation(summary = "批量文件上传")
    @PostMapping("/file/batchUpload")
    Result<List<String>> batchUpload(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<String> urls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                byte[] content = file.getBytes();
                String fileName = file.getOriginalFilename();
                String url = fileFacade.upload(content, fileName);
                urls.add(url);
            }
            return Result.success(urls);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
