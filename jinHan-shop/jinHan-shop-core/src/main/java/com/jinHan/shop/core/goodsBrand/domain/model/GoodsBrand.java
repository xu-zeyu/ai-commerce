package com.jinHan.shop.core.goodsBrand.domain.model;

import com.aicommerce.starter.mybatis.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 类名: GoodsBrand
 * 描述: 商品品牌数据库 model
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("goods_brand")
public class GoodsBrand extends BaseEntity {

    /**
     * 品牌ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 品牌Logo
     */
    private String logo;

    /**
     * 品牌首字母
     */
    private String firstLetter;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

}
