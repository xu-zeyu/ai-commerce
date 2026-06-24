package com.jinHan.shop.core.goodsCategory.domain.model;

import com.aicommerce.starter.mybatis.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 类名: goodsCategory
 * 描述: 商品分类数据库 model
 * 作者: xuzeyu
 * 创建时间: 2026/6/15
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("goods_category")
public class GoodsCategory extends BaseEntity {

    /**
     * 商品分类最大层级：最多支持三级分类
     */
    public static final int MAX_LEVEL = 3;

    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父分类ID，0表示一级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;
}
