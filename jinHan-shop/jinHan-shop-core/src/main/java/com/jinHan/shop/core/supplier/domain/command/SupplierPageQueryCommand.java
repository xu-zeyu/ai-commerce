package com.jinHan.shop.core.supplier.domain.command;

import com.aicommerce.starter.mybatis.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: SupplierPageQueryCommand
 * 描述: 分页查询供应商指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SupplierPageQueryCommand extends PageParam {

    /**
     * 供应商编码（模糊搜索）
     */
    private String supplierCode;

    /**
     * 供应商名称（模糊搜索）
     */
    private String supplierName;

    /**
     * 状态：1正常 0禁用
     */
    private Integer status;
}
