package com.jinHan.shop.core.product.domain.command;

import com.jinHan.shop.core.product.domain.model.ImageTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 类名: ProductSpuImageCommand
 * 描述: 商品SPU图片指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/30
 */
@Data
public class ProductSpuImageCommand {

    /**
     * SPU ID
     */
    @NotNull(message = "SPU ID不能为空")
    private Long spuId;

    /**
     * 图片类型：1-主图 2-轮播图 3-详情图
     */
    @NotNull(message = "图片类型不能为空")
    private ImageTypeEnum imageType;

    /**
     * 图片地址数组
     */
    @NotEmpty(message = "图片地址不能为空")
    private List<String> imageUrls;
}
