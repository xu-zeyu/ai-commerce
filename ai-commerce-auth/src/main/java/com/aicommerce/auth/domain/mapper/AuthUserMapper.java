package com.aicommerce.auth.domain.mapper;

import com.aicommerce.auth.domain.model.Admin;
import com.aicommerce.auth.domain.model.Users;
import com.aicommerce.starter.mybatis.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 类名: AuthUserMapper
 * 描述:
 * 作者: xuzeyu
 * 创建时间: 2026/1/2
 */
@Mapper
public interface AuthUserMapper extends BaseMapperX<Users> {
}
