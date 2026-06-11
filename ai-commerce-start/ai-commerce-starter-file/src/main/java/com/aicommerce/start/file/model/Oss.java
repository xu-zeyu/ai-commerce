package com.aicommerce.start.file.model;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类名: Oss
 * 描述: oss 配置类
 * 作者: xuzeyu
 * 创建时间: 2025/12/22
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class Oss {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String region;

    public OSS ossClient() {
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setSignatureVersion(SignVersion.V4);
        return OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(new DefaultCredentialProvider(accessKeyId, accessKeySecret))
                .clientConfiguration(configuration)
                .region(region)
                .build();
    }
}
