package com.aicommerce.auth.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import lombok.Getter;

import java.util.List;

/**
 * 【Merchant账号体系】Sa-Token 权限认证工具类
 *
 * @author xuzeyu
 * @since 1.0.0
 */
public class StpMerchantUtil {

    private StpMerchantUtil() {
    }

    /**
     * 多账号体系下的类型标识
     */
    public static final String TYPE = "merchant";

    /**
     * 底层使用的 StpLogic 对象
     */
    @Getter
    public static StpLogic stpLogic = new StpLogic(TYPE);

    /**
     * 获取当前 StpLogic 的账号类型
     *
     * @return 账号类型
     */
    public static String getLoginType() {
        return stpLogic.getLoginType();
    }

    /**
     * 安全重置 StpLogic 对象
     *
     * @param newStpLogic 新的 StpLogic
     */
    public static void setStpLogic(StpLogic newStpLogic) {
        stpLogic = newStpLogic;
        SaManager.putStpLogic(newStpLogic);
        SaTokenEventCenter.doSetStpLogic(stpLogic);
    }

    /**
     * 获取当前会话 token 信息
     *
     * @return token 信息
     */
    public static SaTokenInfo getTokenInfo() {
        return stpLogic.getTokenInfo();
    }

    /**
     * 获取当前请求的 token 值
     *
     * @return token 值
     */
    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }

    /**
     * 商家账号登录
     *
     * @param loginId 登录账号 ID
     */
    public static void login(Object loginId) {
        stpLogic.login(loginId);
    }

    /**
     * 商家账号登录
     *
     * @param loginId 登录账号 ID
     * @param loginParameter 登录参数
     */
    public static void login(Object loginId, SaLoginParameter loginParameter) {
        stpLogic.login(loginId, loginParameter);
    }

    /**
     * 当前客户端退出登录
     */
    public static void logout() {
        stpLogic.logout();
    }

    /**
     * 当前会话是否已登录
     *
     * @return 是否已登录
     */
    public static boolean isLogin() {
        return stpLogic.isLogin();
    }

    /**
     * 校验当前会话已登录
     */
    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    /**
     * 获取当前登录账号 ID
     *
     * @return 账号 ID
     */
    public static Object getLoginId() {
        return stpLogic.getLoginId();
    }

    /**
     * 获取当前登录账号 ID
     *
     * @return 账号 ID
     */
    public static long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    /**
     * 获取当前账号权限列表
     *
     * @return 权限列表
     */
    public static List<String> getPermissionList() {
        return stpLogic.getPermissionList();
    }

    /**
     * 获取当前账号角色列表
     *
     * @return 角色列表
     */
    public static List<String> getRoleList() {
        return stpLogic.getRoleList();
    }
}
