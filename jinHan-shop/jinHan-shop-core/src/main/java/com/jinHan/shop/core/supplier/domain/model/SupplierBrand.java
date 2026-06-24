package com.jinHan.shop.core.supplier.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 类名: SupplierBrand
 * 描述: 供应商品牌关系数据库 model
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("supplier_brand")
public class SupplierBrand {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

}
