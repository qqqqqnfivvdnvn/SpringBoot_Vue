package com.example.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.example.demo.annotation.ExcelColumn;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReaderExcel {

    /**
     * 读取Excel文件，所有数据按字符串处理
     * @param filePath 文件路径
     * @param clazz    目标类
     * @param <T>      泛型
     * @return 对象列表
     * @throws IOException 文件异常
     */
    public <T> List<T> readExcel(String filePath, Class<T> clazz) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }

        List<T> result = new ArrayList<>();

        try {
            EasyExcel.read(file)
                    .registerReadListener(new HaoSenReadListener<>(clazz, result))
                    .autoCloseStream(true)
                    .ignoreEmptyRow(true)
                    .headRowNumber(1)     // 表头在第1行
                    .sheet()              // 读取第一个sheet
                    .doRead();

        } catch (Exception e) {
            throw new IOException("读取Excel文件失败: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * 自定义读取监听器
     */
    private static class HaoSenReadListener<T> implements ReadListener<Map<Integer, String>> {
        private final Class<T> targetClass;
        private final List<T> resultList;
        private Map<Integer, String> headerMap; // 列索引 -> 表头名称
        private final Map<String, Field> fieldMap; // 表头名称 -> 字段

        public HaoSenReadListener(Class<T> clazz, List<T> resultList) {
            this.targetClass = clazz;
            this.resultList = resultList;
            this.fieldMap = buildFieldMap(clazz);
        }

        /**
         * 构建字段映射（表头名称 -> 字段对象）
         */
        private Map<String, Field> buildFieldMap(Class<T> clazz) {
            Map<String, Field> map = new HashMap<>();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                if (annotation != null) {
                    String columnName = annotation.name().trim();
                    if (!columnName.isEmpty()) {
                        field.setAccessible(true);
                        map.put(columnName, field);
                    }
                }
            }
            return map;
        }

        @Override
        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
            // 读取表头，建立列索引与表头名称的映射
            this.headerMap = new HashMap<>();

            for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                String headerName = entry.getValue().getStringValue();
                if (headerName != null) {
                    String trimmedName = headerName.trim();
                    if (!trimmedName.isEmpty()) {
                        headerMap.put(entry.getKey(), trimmedName);
                    }
                }
            }
        }

        @Override
        public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
            try {
                T obj = convertRowToObject(rowData);
                if (obj != null) {
                    resultList.add(obj);
                }
            } catch (Exception e) {
                System.err.println("解析第 " + (context.readRowHolder().getRowIndex() + 1) + " 行失败: " + e.getMessage());
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 所有数据解析完成
        }

        /**
         * 将单行数据转换为对象
         */
        private T convertRowToObject(Map<Integer, String> rowData) throws Exception {
            if (rowData == null || rowData.isEmpty() || headerMap == null) {
                return null;
            }

            T obj = targetClass.getDeclaredConstructor().newInstance();
            boolean hasData = false;

            for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
                Integer columnIndex = entry.getKey();
                String cellValue = entry.getValue();

                // 获取表头名称
                String headerName = headerMap.get(columnIndex);
                if (headerName == null || cellValue == null) {
                    continue;
                }

                // 去除值两端的空格
                cellValue = cellValue.trim();

                // 查找对应的字段
                Field field = fieldMap.get(headerName);
                if (field != null && !cellValue.isEmpty()) {
                    field.set(obj, cellValue);
                    hasData = true;
                }
            }

            return hasData ? obj : null;
        }
    }
}