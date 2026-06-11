package com.aicommerce.auth.domain.handler.sms;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.aicommerce.auth.domain.command.UserSmsPwsLoginCommand;
import com.aicommerce.auth.domain.mapper.AuthUserMapper;
import com.aicommerce.auth.domain.model.Users;
import com.aicommerce.auth.domain.model.UsersStatusEnum;
import com.aicommerce.auth.infra.SmsCodeCache;
import com.aicommerce.auth.stp.StpUserUtil;
import com.aicommerce.auth.utils.PasswordService;
import com.aicommerce.common.exception.BusinessException;
import com.aicommerce.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 类名: UserSmsPwsLoginHandler
 * 描述: 短信验证码登录处理器，支持自动注册
 * 作者: xuzeyu
 * 创建时间: 2026/1/15
 */
@Component
public class UserSmsPwsLoginHandler {
    @Resource
    private AuthUserMapper authUserMapper;

    @Resource
    private PasswordService passwordService;

    @Resource
    private SmsCodeCache smsCodeCache;

    /**
     * 短信验证码登录
     * 如果用户不存在，自动创建新用户
     *
     * @param command 登录命令
     * @return 登录token
     */
    @Transactional(rollbackFor = Exception.class)
    public String handler(UserSmsPwsLoginCommand command) {
        try {
            // 1. 查询用户
            Users users = getOrCreateUser(command);

            // 2. 验证码校验
            validateSmsCode(users.getPhone(), command.getSmsCode());

            // 3. 密码校验（如果是新注册的用户，跳过密码校验）
            if (command.getPassword() != null && !isNewlyRegistered(users)) {
                validatePassword(command.getPassword(), users.getPassword());
            }

            // 4. 执行登录（使用独立的 User StpLogic）
            StpUserUtil.login(users.getUserId());
            SaTokenInfo tokenInfo = StpUserUtil.getTokenInfo();

            // 5. 更新最后登录时间
            updateLastLoginTime(users.getUserId());

            return tokenInfo.getTokenValue();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取或创建用户
     * 如果用户不存在，自动创建新用户
     *
     * @param command 登录命令
     * @return 用户实体
     */
    private Users getOrCreateUser(UserSmsPwsLoginCommand command) {
        LambdaQueryWrapper<Users> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(Users::getPhone, command.getPhone());
        Users users = authUserMapper.selectOne(lambdaQuery);

        if (users == null) {
            // 用户不存在，自动注册
            users = registerNewUser(command);
        }

        return users;
    }

    /**
     * 注册新用户
     *
     * @param command 登录命令
     * @return 新创建的用户
     */
    private Users registerNewUser(UserSmsPwsLoginCommand command) {
        Users newUser = new Users();

        // 设置基本信息
        newUser.setPhone(command.getPhone());

        // 设置默认密码：123456（已加密）
        // 用户可以在认证后修改密码
        String defaultEncryptedPassword = passwordService.passwordEncrypt("123456");
        newUser.setPassword(defaultEncryptedPassword);

        // 设置初始状态为未认证
        newUser.setStatus(UsersStatusEnum.UNAUTHENTICATED);

        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        newUser.setCreatedTime(now);
        newUser.setUpdatedTime(now);

        // 插入数据库
        int insertCount = authUserMapper.insert(newUser);
        if (insertCount <= 0) {
            throw new BusinessException("用户注册失败");
        }

        return newUser;
    }

    /**
     * 判断用户是否为新注册的
     * 可以根据创建时间和当前时间的差值判断
     *
     * @param users 用户实体
     * @return 是否新注册
     */
    private boolean isNewlyRegistered(Users users) {
        // 简单判断：如果创建时间在1分钟内，认为是新注册的
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        return users.getCreatedTime() != null && users.getCreatedTime().isAfter(oneMinuteAgo);
    }

    /**
     * 验证短信验证码
     *
     * @param phone 手机号
     * @param smsCode 验证码
     */
    private void validateSmsCode(String phone, String smsCode) {
        String expected = smsCodeCache.getUserCache(phone).orElse(null);
        if (expected == null || !StringUtils.equals(expected, smsCode)) {
            throw new BusinessException("验证码错误或已过期");
        }
        // 验证后删除验证码
        smsCodeCache.removeUserCache(phone);
    }

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     */
    private void validatePassword(String rawPassword, String encodedPassword) {
        Boolean passwordValid = passwordService.passwordValid(rawPassword, encodedPassword);
        if (!passwordValid) {
            throw new BusinessException("账户或密码错误");
        }
    }

    /**
     * 更新最后登录时间
     *
     * @param userId 用户ID
     */
    private void updateLastLoginTime(Long userId) {
        Users updateUser = new Users();
        updateUser.setUserId(userId);
        updateUser.setLastLoginAt(LocalDateTime.now());
        authUserMapper.updateById(updateUser);
    }
}
