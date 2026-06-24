package com.jinHan.shop.core.supplier.domain.model;

import com.aicommerce.starter.mybatis.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 类名: Supplier
 * 描述: 供应商数据库 model
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("supplier")
public class Supplier extends BaseEntity {

    /**
     * 供应商ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
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
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}
