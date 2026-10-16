package com.jinHan.shop.core.ragBucket.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.ragBucket.domain.command.RagRepositoryUploadCommand;
import com.jinHan.shop.core.ragBucket.domain.mapper.RagBucketMapper;
import com.jinHan.shop.core.ragBucket.domain.model.RagBucket;
import com.jinHan.shop.core.ragBucket.infra.RagRepositoryHttpAdapter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RagRepositoryUploadHandler {

    @Resource
    private RagBucketMapper ragBucketMapper;

    @Resource
    private RagRepositoryHttpAdapter ragRepositoryHttpAdapter;

    public String uploadFile(RagRepositoryUploadCommand command) {

        if (command == null) {
            throw new BusinessException("上传参数不能为空");
        }
        if (command.getRepoId() == null || command.getRepoId().isBlank()) {
            throw new BusinessException("repoId 不能为空");
        }
        if (command.getFileName() == null || command.getFileName().isBlank()) {
            throw new BusinessException("文件名不能为空");
        }
        if (command.getFileContent() == null || command.getFileContent().length == 0) {
            throw new BusinessException("上传文件不能为空");
        }

        RagBucket ragBucket = ragBucketMapper.selectOne(RagBucket::getCode, command.getRepoId());
        if (ragBucket == null) {
            throw new BusinessException("知识库不存在");
        }
        Long loginUserId = Long.valueOf(StpUtil.getLoginId().toString());
        if (!Objects.equals(ragBucket.getUserId(), loginUserId)) {
            throw new BusinessException("无权上传文件到该知识库");
        }

        return ragRepositoryHttpAdapter.uploadFile(command);
    }
}
