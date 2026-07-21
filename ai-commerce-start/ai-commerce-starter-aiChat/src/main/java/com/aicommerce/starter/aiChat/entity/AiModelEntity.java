package com.aicommerce.starter.aiChat.entity;

import com.aicommerce.starter.mybatis.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 类名: AiModelEntity
 * 描述: ai 模型实体
 * 作者: xuzeyu
 * 创建时间: 2026/7/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_model")
public class AiModelEntity  extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String provider;

    private String modelName;

    private String baseUrl;

    private String apiKey;

    private Boolean enabled;
}
