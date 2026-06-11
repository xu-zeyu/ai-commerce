package com.aicommerce.log.service;

import com.aicommerce.log.entity.LogEntity;
import com.aicommerce.starter.mybatis.PageParam;
import com.aicommerce.starter.mybatis.PageResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 日志服务接口
 */
public interface LogService {

    /**
     * 保存日志
     */
    void saveLog(LogEntity logEntity);

    /**
     * 根据条件查询日志（PageParam分页）
     */
    IPage<LogEntity> queryLogs(PageParam pageParam, @Param("ew") Wrapper<LogEntity> queryWrapper);

    /**
     * 根据ID查询日志
     */
    LogEntity getLogById(Long id);

    /**
     * 删除日志
     */
    void deleteLog(Long id);

    /**
     * 批量删除日志
     */
    void deleteLogs(List<Long> ids);
}
