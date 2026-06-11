package com.aicommerce.start.file.service;

import com.aicommerce.start.file.facade.FileFacade;
import com.aicommerce.start.file.model.Oss;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 类名: FileFacadeImpl
 * 描述: oss 上传实现类
 * 作者: xuzeyu
 * 创建时间: 2025/12/22
 */

@Service
@RequiredArgsConstructor
public class FileFacadeImpl implements FileFacade {
    @Resource
    private Oss oss;

    @Override
    public String upload(byte[] content, String originFilename) throws ClientException {
        OSS ossClient = oss.ossClient();
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String newFileName = UUID.randomUUID() + originFilename.substring(originFilename.lastIndexOf("."));
        String filePath = dir + '/' + newFileName;
        try {
            ossClient.putObject(oss.getBucketName(),filePath,new ByteArrayInputStream(content));
            return "https://" + oss.getBucketName() + "." + oss.getEndpoint() + "/" + filePath;
        }catch (Exception e) {
            throw new RuntimeException("上传失败",e);
        }finally {
            if (ossClient != null) ossClient.shutdown();
        }
    }
}
