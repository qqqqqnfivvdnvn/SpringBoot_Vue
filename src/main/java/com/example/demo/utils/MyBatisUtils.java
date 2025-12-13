package com.example.demo.utils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public final class MyBatisUtils {

    /**
     * 判断 condition 对象是否“全空”
     * 1. null 算空
     * 2. "" 算空
     * 3. 集合/数组 isEmpty 算空
     * 4. 内部嵌套对象递归判断
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

            ReflectionUtils.makeAccessible(f);
            Object val = ReflectionUtils.getField(f, condition);
            if (!isBlankValue(val, ignoreFields)) {
                return false;   // 只要有一个非空字段就返回 false
            }
        }
        return true;
    }

    /* 真正判空的逻辑 */
    private static boolean isBlankValue(Object val, String[] ignoreFields) {
        if (val == null) return true;
        if (val instanceof String) return ((String) val).trim().isEmpty();
        if (val instanceof Collection) return ((Collection<?>) val).isEmpty();
        if (val.getClass().isArray()) return ObjectUtils.isEmpty(val);
        // 嵌套对象递归
        return isAllBlank(val, ignoreFields);
    }

    private MyBatisUtils() {}
}