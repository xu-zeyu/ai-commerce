package com.aicommerce.auth.infra;

import com.aicommerce.common.exception.BusinessException;
import com.aicommerce.starter.cache.CacheUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * 类名: SmsCodeCache
 * 描述:
 * 作者: xuzeyu
 * 创建时间: 2025/12/25
 */
@Component
public class SmsCodeCache {
    @Resource
    private CacheUtils cache;

    private final Pattern phonePattern = Pattern.compile("^1([3456789])[0-9]{9}$");


    private static final String SMS_CODE_CACHE_PREFIX = "sms:";

    private static final String USERS_SMS_CODE_CACHE_PREFIX = "smsUsers:";

    private static final String MERCHANTS_SMS_CODE_CACHE_PREFIX = "smsMerchants:";

    public void setCache(String mobile, String code) {
        this.cache.set(SMS_CODE_CACHE_PREFIX + mobile, code, Duration.ofMinutes(3));
    }

    public Optional<String> getCache(String mobile) {
        return this.cache.getCache(SMS_CODE_CACHE_PREFIX + mobile).filter( o -> o instanceof String).map( o-> (String) o);
    }

    public void removeCache(String mobile) {this.cache.delete(SMS_CODE_CACHE_PREFIX + mobile);}


    public void setUserCache(String mobile, String code) {
        this.cache.set(USERS_SMS_CODE_CACHE_PREFIX + mobile, code, Duration.ofMinutes(3));
    }

    public Optional<String> getUserCache(String mobile) {
        return this.cache.getCache(USERS_SMS_CODE_CACHE_PREFIX + mobile).filter( o -> o instanceof String).map( o-> (String) o);
    }

    public void removeUserCache(String mobile) {this.cache.delete(USERS_SMS_CODE_CACHE_PREFIX + mobile);}


    public void setMerchantCache(String mobile, String code) {
        this.cache.set(MERCHANTS_SMS_CODE_CACHE_PREFIX + mobile, code, Duration.ofMinutes(3));
    }

    public Optional<String> getMerchantCache(String mobile) {
        return this.cache.getCache(MERCHANTS_SMS_CODE_CACHE_PREFIX + mobile).filter(o -> o instanceof String).map(o -> (String) o);
    }

    public void removeMerchantCache(String mobile) {this.cache.delete(MERCHANTS_SMS_CODE_CACHE_PREFIX + mobile);}


    // 创建验证码
    public String createCache(String mobile) {
        var m = this.phonePattern.matcher(mobile);
        if(! m.matches()){
            throw new BusinessException("手机号格式不对");
        }
        return String.format("%06d", ThreadLocalRandom.current().nextInt(999999));
    }
}
