package com.aicommerce.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@Accessors(chain = true)
@TableName("sys_log")
public class LogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 操作IP
     */
    private String ip;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 执行时长（毫秒）
     */
    private Long executeTime;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;
}
