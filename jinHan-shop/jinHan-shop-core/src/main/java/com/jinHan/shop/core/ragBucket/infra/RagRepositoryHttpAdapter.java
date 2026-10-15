package com.jinHan.shop.core.ragBucket.infra;

import com.jinHan.shop.core.ragBucket.domain.command.RagRepositoryUploadCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class RagRepositoryHttpAdapter{

    private final RestTemplate restTemplate;
    private final String repositoryApiUrl;

    public RagRepositoryHttpAdapter(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${RAG_REPOSITORY_API_URL:http://192.168.7.31:8080/api/v1/repos}") String repositoryApiUrl) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
        this.repositoryApiUrl = repositoryApiUrl;
    }

    public String uploadFile(RagRepositoryUploadCommand command) {
        ByteArrayResource fileResource = new ByteArrayResource(command.getFileContent()) {
            @Override
            public String getFilename() {
                return command.getFileName();
            }
        };

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", new HttpEntity<>(fileResource, fileHeaders));
        formData.add("repoId", command.getRepoId());

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(formData, requestHeaders);
        return restTemplate.postForObject(repositoryApiUrl, request, String.class);
    }
}
