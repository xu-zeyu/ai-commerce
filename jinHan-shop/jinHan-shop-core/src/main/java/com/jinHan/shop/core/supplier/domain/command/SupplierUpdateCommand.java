package com.jinHan.shop.core.supplier.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: SupplierUpdateCommand
 * 描述: 根据id更新供应商内容
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SupplierUpdateCommand extends SupplierCreateCommand {

    @NotNull(message = "供应商ID不能为空")
    private Long id;

    public SupplierUpdateCommand(Long id, SupplierCreateCommand command) {
        this.setId(id);
        this.setSupplierCode(command.getSupplierCode());
        this.setSupplierName(command.getSupplierName());
        this.setContactName(command.getContactName());
        this.setContactPhone(command.getContactPhone());
        this.setEmail(command.getEmail());
        this.setAddress(command.getAddress());
        this.setStatus(command.getStatus());
        this.setRemark(command.getRemark());
    }
}
