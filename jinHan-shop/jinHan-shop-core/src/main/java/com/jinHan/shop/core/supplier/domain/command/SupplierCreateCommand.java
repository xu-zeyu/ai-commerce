package com.jinHan.shop.core.supplier.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类名: SupplierCreateCommand
 * 描述: 新增供应商指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Data
public class SupplierCreateCommand {

    /**
     * 供应商编码
     */
    @NotBlank(message = "供应商编码不能为空")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @NotBlank(message = "供应商名称不能为空")
    private String supplierName;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态：1正常 0禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
