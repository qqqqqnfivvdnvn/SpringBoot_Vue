package com.example.demo.utils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public final class MyBatisUtils {

    /**
     * 判断 condition 对象是否"全空"
     * 1. null 算空
     * 2. "" 算空
     * 3. 集合/数组 isEmpty 算空
     * 4. 内部嵌套对象递归判断（只处理自定义对象，不处理 JDK 内部类）
     * 5. 白名单字段直接跳过（如分页参数）
     */
    public static boolean isAllBlank(Object condition) {
        return isAllBlank(condition, "pageNum", "pageSize", "orderBy", "orderAsc");
    }

    public static boolean isAllBlank(Object condition, String... ignoreFields) {
        if (condition == null) {
            return true;
        }

        // Map 特判
        if (condition instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) condition;
            if (map.isEmpty()) return true;
            return map.values().stream()
                    .allMatch(v -> isBlankValue(v, ignoreFields));
        }

        // 如果是 JDK 内部类，不进行递归，直接判断是否为基本空值
        if (isJdkClass(condition.getClass())) {
            return !isBasicNonBlank(condition);
        }

        // 普通对象反射
        Field[] fields = condition.getClass().getDeclaredFields();
        for (Field f : fields) {
            // 跳过静态、白名单
            if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
            boolean skip = false;
            for (String ignore : ignoreFields) {
                if (ignore.equals(f.getName())) { skip = true; break; }
            }
            if (skip) continue;

            try {
                f.setAccessible(true);
                Object val = f.get(condition);
                if (!isBlankValue(val, ignoreFields)) {
                    return false;   // 只要有一个非空字段就返回 false
                }
            } catch (IllegalAccessException e) {
                // 如果无法访问，当作非空处理，避免误判
                return false;
            } catch (Exception e) {
                // 其他异常也当作非空
                return false;
            }
        }
        return true;
    }

    /* 真正判空的逻辑 */
    private static boolean isBlankValue(Object val, String[] ignoreFields) {
        if (val == null) return true;

        // 检查是否为基本空值
        if (isBasicBlank(val)) {
            return true;
        }

        // 如果是 JDK 内部类，不进行递归
        if (isJdkClass(val.getClass())) {
            return !isBasicNonBlank(val);
        }

        // 对于自定义对象，递归检查
        return isAllBlank(val, ignoreFields);
    }

    /**
     * 判断是否为 JDK 内部类
     */
    private static boolean isJdkClass(Class<?> clazz) {
        return clazz.getName().startsWith("java.") ||
                clazz.getName().startsWith("javax.") ||
                clazz.getName().startsWith("sun.") ||
                clazz.getName().startsWith("com.sun.");
    }

    /**
     * 判断基本类型是否为空
     */
    private static boolean isBasicBlank(Object val) {
        if (val == null) return true;
        if (val instanceof String) return ((String) val).trim().isEmpty();
        if (val instanceof Collection) return ((Collection<?>) val).isEmpty();
        if (val instanceof Map) return ((Map<?, ?>) val).isEmpty();
        if (val.getClass().isArray()) return ObjectUtils.isEmpty(val);
        return false;
    }

    /**
     * 判断基本类型是否非空
     */
    private static boolean isBasicNonBlank(Object val) {
        return !isBasicBlank(val);
    }

    private MyBatisUtils() {}
}