package com.jinHan.shop.core.ragBucket.domain.mapper;

import com.aicommerce.starter.mybatis.mapper.BaseMapperX;
import com.jinHan.shop.core.admin.domain.model.AdminPermission;
import com.jinHan.shop.core.ragBucket.domain.model.RagBucket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 类名: RagBucketMapper
 * 描述: rag 桶数据访问层
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Mapper
public interface RagBucketMapper extends BaseMapperX<RagBucket> {
}
