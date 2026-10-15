package com.jinHan.shop.admin.controller.ragBucket;

import com.aicommerce.common.exception.BusinessException;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.jinHan.shop.core.ragBucket.domain.command.RagRepositoryUploadCommand;
import com.jinHan.shop.core.ragBucket.domain.handler.RagRepositoryUploadHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/rag/repository")
@Tag(name = "RAG 知识库管理")
public class RagRepositoryController {

    @Resource
    private  RagRepositoryUploadHandler ragRepositoryUploadHandler;

    @Log(value = "上传RAG知识库文件", operationType = "RAG_REPOSITORY_UPLOAD")
    @Operation(summary = "上传RAG知识库文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file,
                                     @RequestParam("repoId") String repoId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        RagRepositoryUploadCommand command = new RagRepositoryUploadCommand();
        command.setRepoId(repoId);
        command.setFileName(file.getOriginalFilename());
        try {
            command.setFileContent(file.getBytes());
        } catch (IOException exception) {
            throw new BusinessException(exception, "读取上传文件失败");
        }
        return Result.success(ragRepositoryUploadHandler.uploadFile(command));
    }
}
