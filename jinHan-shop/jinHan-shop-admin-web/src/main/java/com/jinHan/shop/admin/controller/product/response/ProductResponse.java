package com.jinHan.shop.admin.controller.product.response;

import com.jinHan.shop.core.product.domain.model.AuditStatusEnum;
import com.jinHan.shop.core.product.domain.model.ProductDetails;
import com.jinHan.shop.core.product.domain.model.SaleStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 类名: ProductResponse
 * 描述: 商品详情
 * 作者: xuzeyu
 * 创建时间: 2026/7/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;

    private Long supplierId;

    private String spuCode;

    private String name;

    private String subTitle;

    private Long categoryId;

    private Long brandId;

    private SaleStatusEnum saleStatus;

    private AuditStatusEnum auditStatus;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String supplierName;

    private String categoryName;

    private String brandName;

    private List<String> mainImage;

    public ProductResponse toProductDetails(ProductDetails productDetails) {
        this.setId(productDetails.getId());
        this.setSupplierId(productDetails.getSupplierId());
        this.setSpuCode(productDetails.getSpuCode());
        this.setName(productDetails.getName());
        this.setSubTitle(productDetails.getSubTitle());
        this.setCategoryId(productDetails.getCategoryId());
        this.setBrandId(productDetails.getBrandId());
        this.setSaleStatus(productDetails.getSaleStatus());
        this.setAuditStatus(productDetails.getAuditStatus());
        this.setCreatedBy(productDetails.getCreatedBy());
        this.setUpdatedBy(productDetails.getUpdatedBy());
        this.setCreatedTime(productDetails.getCreatedTime());
        this.setUpdatedTime(productDetails.getUpdatedTime());
        this.setSupplierName(productDetails.getSupplierName());
        this.setCategoryName(productDetails.getCategoryName());
        this.setBrandName(productDetails.getBrandName());
        this.setMainImage(productDetails.getMainImage());
        return this;
    }

}
