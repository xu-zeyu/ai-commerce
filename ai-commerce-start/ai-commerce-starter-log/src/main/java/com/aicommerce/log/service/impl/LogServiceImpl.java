package com.aicommerce.log.service.impl;

import com.aicommerce.log.entity.LogEntity;
import com.aicommerce.log.mapper.LogMapper;
import com.aicommerce.log.service.LogService;
import com.aicommerce.starter.mybatis.PageParam;
import com.aicommerce.starter.mybatis.PageResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 日志服务实现
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    private LogMapper logMapper;

    @Override
    public void saveLog(LogEntity logEntity) {
        logMapper.insert(logEntity);
    }

    @Override
    public IPage<LogEntity> queryLogs(PageParam pageParam, @Param("ew") Wrapper<LogEntity> queryWrapper) {

        return logMapper.selectPage(pageParam,queryWrapper);
    }

    @Override
    public LogEntity getLogById(Long id) {
        return logMapper.selectById(id);
    }

    @Override
    public void deleteLog(Long id) {
        logMapper.deleteById(id);
    }

    @Override
    public void deleteLogs(List<Long> ids) {
        logMapper.deleteBatchIds(ids);
    }
}
