package com.aicommerce.auth.utils;

import cn.dev33.satoken.secure.SaSecureUtil;
import org.springframework.stereotype.Service;

/**
 * 类名: PasswordService
 * 描述: 密码加密/解密
 * 作者: xuzeyu
 * 创建时间: 2026/1/2
 */
@Service
public class PasswordService {

    // 加密
    public String passwordEncrypt(String inputPassword) {
        return SaSecureUtil.sha256(inputPassword);
    }

    /**
     * 解密
     */
    public Boolean passwordValid(String inputPassword,String userPassword) {
        return userPassword.equals(SaSecureUtil.sha256(inputPassword));
    }
}
