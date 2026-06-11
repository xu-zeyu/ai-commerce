package com.aicommerce.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public final class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String SEPARATOR = ",";

    public static final String SLASH = "/";

    public static final String UNDERLINE = "_";

    public static final String DOT = ".";

    public static final String COLON = ":";

    private StringUtils() {
    }

    public static String format(String pattern, Object... args) {
        return org.slf4j.helpers.MessageFormatter.arrayFormat(pattern, args).getMessage();
    }

    /**
     * 获取一段文字中的最后一句
     */
    public static String getLastSentence(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // 正则表达式匹配一个完整的句子
        // 解释: [^.?!。？！]+ 匹配一个或多个非结束符的字符
        // [.?!。？！]? 匹配一个可选的结束符
        // \\s* 匹配结尾的任意空格
        Pattern pattern = Pattern.compile("[^.?!。？！]+[.?!。？！]?\\s*");
        Matcher matcher = pattern.matcher(text.trim());
        String lastSentence = "";
        // 循环查找所有匹配的句子，最后一个找到的就是我们想要的
        while (matcher.find()) {
            lastSentence = matcher.group();
        }
        return lastSentence.trim();
    }

    /**
     * 将驼峰命名转换为下划线命名
     * <p>
     * 例如: userName -> user_name, UserName -> user_name
     *
     * @param str 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public static String toUnderlineCase(CharSequence str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length + length / 2);
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append(UNDERLINE);
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * 字符串不相等
     */
    public static boolean notEquals(final CharSequence cs1, final CharSequence cs2) {
        return !equals(cs1, cs2);
    }

}