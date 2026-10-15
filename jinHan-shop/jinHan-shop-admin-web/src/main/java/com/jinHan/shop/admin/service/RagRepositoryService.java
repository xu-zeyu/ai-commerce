package com.jinHan.shop.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@Service
public class RagRepositoryService {

    private final RestTemplate restTemplate;
    private final String repositoryApiUrl;

    public RagRepositoryService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${RAG_REPOSITORY_API_URL:http://192.168.7.21:8080/api/v1/repos}") String repositoryApiUrl) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
        this.repositoryApiUrl = repositoryApiUrl;
    }

    public String uploadFile(MultipartFile file, String repoId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (!StringUtils.hasText(repoId)) {
            throw new IllegalArgumentException("repoId 不能为空");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource fileResource = file.getResource();

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", new HttpEntity<>(fileResource, fileHeaders));
        formData.add("repoId", repoId);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(formData, headers);
        return restTemplate.postForObject(repositoryApiUrl, request, String.class);
    }
}
