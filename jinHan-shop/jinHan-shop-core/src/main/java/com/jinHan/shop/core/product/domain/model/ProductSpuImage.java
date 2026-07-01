package com.jinHan.shop.core.product.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 类名: ProductSpuImage
 * 描述: 商品主图
 * 作者: xuzeyu
 * 创建时间: 2026/6/30
 */
@Data
@TableName(value = "product_spu_image", autoResultMap = true)
public class ProductSpuImage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 图片类型：1-主图 2-轮播图 3-详情图
     */
    private ImageTypeEnum imageType;

    /**
     * 图片地址JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> imageUrls;

    /**
     * 逻辑删除标识
     * 0-未删除
     */
    @TableLogic
    private Long deleted;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 更新人
     */
    private Long updatedBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
