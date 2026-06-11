package com.aicommerce.log.mapper;

import com.aicommerce.auth.domain.model.Admin;
import com.aicommerce.log.entity.LogEntity;
import com.aicommerce.starter.mybatis.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 类名: AdminMapper
 * 描述:
 * 作者: xuzeyu
 * 创建时间: 2026/1/2
 */
@Mapper
public interface LogAdminMapper extends BaseMapperX<Admin> {
}