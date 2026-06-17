package com.jinHan.shop.core.goodsCategory.domain.model;

import lombok.Data;

import java.util.List;

/**
 * 类名: GoodsCategoryTreeVO
 * 描述: 商品分类组装树结构
 * 作者: xuzeyu
 * 创建时间: 2026/6/17
 */
@Data
public class GoodsCategoryTreeVO {
    private Long id;

    private Long parentId;

    private String name;

    private Integer level;

    private Integer sort;

    private String icon;

    private Integer status;

    /**
     * 子节点
     */
    private List<GoodsCategoryTreeVO> children;
}
