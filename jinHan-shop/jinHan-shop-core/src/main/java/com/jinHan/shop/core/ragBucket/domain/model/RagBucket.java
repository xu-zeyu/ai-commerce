package com.jinHan.shop.core.ragBucket.domain.model;

import com.aicommerce.starter.mybatis.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 类名: RagBucket
 * 描述: rag桶原始数据库 model
 * 作者: xuzeyu
 * 创建时间: 2026/10/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("rag_bucket")
public class RagBucket extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String code;

    private String description;
}
