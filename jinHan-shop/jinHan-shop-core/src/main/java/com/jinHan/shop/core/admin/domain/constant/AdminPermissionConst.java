package com.jinHan.shop.core.admin.domain.constant;

/**
 * 类名: AdminPermissionConst
 * 描述: 后台接口权限码常量，供 @SaCheckPermission 使用（注解只能引用编译期常量）。
 * 作者: xuzeyu
 * 创建时间: 2026/6/17
 */
public final class AdminPermissionConst {

    private AdminPermissionConst() {
    }

    /**
     * 超级管理员
     */
    public static final String SUB_ADMIN = "SUB_ADMIN";

    /**
     * 商品分类 - 新增
     */
    public static final String GOODS_CATEGORY_CREATE = "GOODS_CATEGORY_CREATE";

    /**
     * 商品分类 - 分页/层级列表
     */
    public static final String GOODS_CATEGORY_PAGE = "GOODS_CATEGORY_PAGE";

    /**
     * 商品分类 - 树形列表
     */
    public static final String GOODS_CATEGORY_TREE = "GOODS_CATEGORY_TREE";

    // ==================== 商品品牌 ====================

    /**
     * 商品品牌 - 新增
     */
    public static final String GOODS_BRAND_CREATE = "GOODS_BRAND_CREATE";

    /**
     * 商品品牌 - 分页列表
     */
    public static final String GOODS_BRAND_PAGE = "GOODS_BRAND_PAGE";

    /**
     * 商品品牌 - 编辑
     */
    public static final String GOODS_BRAND_UPDATE = "GOODS_BRAND_UPDATE";

    /**
     * 商品品牌 - 删除
     */
    public static final String GOODS_BRAND_DELETE = "GOODS_BRAND_DELETE";
}
