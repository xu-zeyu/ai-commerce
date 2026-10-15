package com.jinHan.shop.core.ragBucket.domain.command;

import lombok.Data;

@Data
public class RagRepositoryUploadCommand {

    private String repoId;

    private String fileName;

    private byte[] fileContent;
}
