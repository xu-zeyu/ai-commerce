package com.aicommerce.common.log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
@Slf4j
public class RestControllerLoggingAop {

    private static final int MAX_STRING_LENGTH = 300;

    private static final int MAX_COLLECTION_SIZE = 10;

    private static final int MAX_OBJECT_DEPTH = 5;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerPointcut() {
        // 空方法，作为切入点标记
    }

    @Around("controllerPointcut()")
    public Object handlerControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        // todo 做个顶级获取用户Id的方法 由业务去实现
        String userId = "userId";
        String methodSignature = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        try {
            String requestParams = formatParams(args);
            log.info("Request => [userId: {}, url: {}, method: {}, params: {}]", userId, request.getRequestURI(),
                    methodSignature, requestParams);
        }
        catch (Exception ex) {
            log.error("Request => [userId: {}, url: {}, method: {}, params: {}]", userId, request.getRequestURI(),
                    methodSignature, ex.getMessage());
        }
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // 执行目标方法
        long endTime = System.currentTimeMillis();

        log.info("Success => use time: {}ms", (endTime - startTime)); // 记录请求时间
        return result;
    }

    /**
     * 处理顶层参数
     */
    private String formatParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            // 递归处理每个参数
            sb.append(formatValue(args[i], 0));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 递归处理值，depth用于控制递归深度
     */
    private String formatValue(Object value, int depth) {
        if (value == null) {
            return "null";
        }

        // 防止递归过深
        if (depth > MAX_OBJECT_DEPTH) {
            return value.getClass().getSimpleName() + "{...}";
        }

        // 处理不同类型的参数
        if (value instanceof MultipartFile file) {
            return String.format("MultipartFile{name=%s, size=%d bytes}", file.getOriginalFilename(), file.getSize());
        }
        else if (value instanceof HttpServletRequest httpServletRequest) {
            return "[HttpServletRequest]";
        }
        else if (value instanceof byte[]) {
            return String.format("byte[%d]", ((byte[]) value).length);
        }
        else if (value instanceof String) {
            return formatString((String) value);
        }
        else if (value instanceof Collection) {
            return formatCollection((Collection<?>) value, depth);
        }
        else if (value instanceof Map) {
            return formatMap((Map<?, ?>) value, depth);
        }
        else if (value.getClass().isArray()) {
            return formatArray(value, depth);
        }
        else if (value instanceof Throwable) {
            return value.getClass().getSimpleName() + "{message=" + ((Throwable) value).getMessage() + "}";
        }
        else if (isPrimitiveOrWrapper(value.getClass())) {
            // 原始类型或包装类直接使用toString
            return value.toString();
        }
        else {
            // 处理自定义对象或其他复杂对象
            return formatObject(value, depth);
        }
    }

    /**
     * 处理字符串，截断过长的内容
     */
    private String formatString(String str) {
        if (str.length() > MAX_STRING_LENGTH) {
            // 判断是否可能是base64
            if (str.matches("^[A-Za-z0-9+/]+={0,2}$")) {
                return String.format("Base64String(length=%d, starts with: %s...)", str.length(),
                        truncateString(str, 20));
            }
            else {
                return String.format("String(length=%d, content=%s...)", str.length(),
                        truncateString(str, MAX_STRING_LENGTH));
            }
        }
        return "\"" + str + "\"";
    }

    /**
     * 处理集合类型
     */
    private String formatCollection(Collection<?> collection, int depth) {
        StringBuilder sb = new StringBuilder(collection.getClass().getSimpleName()).append("(size=")
            .append(collection.size())
            .append(")[");

        int count = 0;
        for (Object item : collection) {
            if (count > 0) {
                sb.append(", ");
            }
            if (count >= MAX_COLLECTION_SIZE) {
                sb.append("...");
                break;
            }
            sb.append(formatValue(item, depth + 1));
            count++;
        }

        return sb.append("]").toString();
    }

    /**
     * 处理Map类型
     */
    private String formatMap(Map<?, ?> map, int depth) {
        StringBuilder sb = new StringBuilder(map.getClass().getSimpleName()).append("(size=")
            .append(map.size())
            .append("){");

        int count = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (count > 0) {
                sb.append(", ");
            }
            if (count >= MAX_COLLECTION_SIZE) {
                sb.append("...");
                break;
            }
            sb.append(formatValue(entry.getKey(), depth + 1))
                .append("=")
                .append(formatValue(entry.getValue(), depth + 1));
            count++;
        }

        return sb.append("}").toString();
    }

    /**
     * 处理数组类型
     */
    private String formatArray(Object array, int depth) {
        int length = Array.getLength(array);
        StringBuilder sb = new StringBuilder("Array(length=").append(length).append(")[");

        int displayCount = Math.min(length, MAX_COLLECTION_SIZE);
        for (int i = 0; i < displayCount; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatValue(Array.get(array, i), depth + 1));
        }

        if (length > MAX_COLLECTION_SIZE) {
            sb.append(", ...");
        }

        return sb.append("]").toString();
    }

    /**
     * 处理自定义对象
     */
    private String formatObject(Object obj, int depth) {
        Class<?> clazz = obj.getClass();
        StringBuilder sb = new StringBuilder(clazz.getSimpleName()).append("{");

        // 获取所有字段并处理
        try {
            List<Field> fields = getAllFields(clazz);
            int count = 0;

            for (Field field : fields) {
                field.setAccessible(true);

                // 跳过静态字段和transient字段
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                if (count > 0) {
                    sb.append(", ");
                }

                if (count >= MAX_COLLECTION_SIZE) {
                    sb.append("...");
                    break;
                }

                sb.append(field.getName()).append("=");
                try {
                    Object value = field.get(obj);
                    sb.append(formatValue(value, depth + 1));
                }
                catch (Exception ex) {
                    sb.append("[ERROR]");
                }

                count++;
            }
        }
        catch (Exception ex) {
            // 如果反射失败，则回退到toString
            return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
        }

        return sb.append("}").toString();
    }

    /**
     * 获取类的所有字段（包括继承的）
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 判断是否为原始类型或其包装类
     */
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || clazz == String.class || clazz == Date.class
                || clazz == LocalDate.class || clazz == LocalDateTime.class;
    }

    /**
     * 截断字符串到指定长度
     */
    private String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

}