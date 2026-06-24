package com.jinHan.shop.core.goodsBrand.domain.command;

import com.aicommerce.starter.mybatis.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: GoodsBrandPageQueryCommand
 * 描述: 分页查询商品品牌指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsBrandPageQueryCommand extends PageParam {

    /**
     * 品牌名称（模糊搜索）
     */
    private String name;

    /**
     * 所属分类ID筛选
     */
    private Long categoryId;

    /**
     * 状态筛选：1启用 0禁用
     */
    private Integer status;

}
