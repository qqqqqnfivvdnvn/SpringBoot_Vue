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
import java.util.function.Consumer;


public class ReaderExcel {

    // ==================== 分批大小常量（根据 SQL Server 参数上限 2100 计算）====================

    /** 47 字段 VO，每批 40 条 (40*47=1880 < 2100)
     *  使用场景: HaoSenAppealDataServiceImpl, HaoSenInputCleanDataServiceImpl, HaoSenUpdateDataServicelmpl
     *  VO类: HaoSenInputAppealDataVO (申诉/清洗/更新数据导入) */
    public static final int BATCH_SIZE_47_FIELDS = 40;

    /** 28 字段 VO，每批 50 条 (50*28=1400 < 2100)
     *  使用场景: HrBatchAsyncService
     *  VO类: HrMonitoringDataVO (恒瑞批次数据导入) */
    public static final int BATCH_SIZE_28_FIELDS = 50;

    /** 14 字段 VO，每批 100 条 (100*14=1400 < 2100)
     *  使用场景: HaoSenDuplicateDataServiceImpl
     *  VO类: HaoSenDuplicateDataVO (重复数据处理) */
    public static final int BATCH_SIZE_14_FIELDS = 100;

    /** 5 字段 VO，每批 200 条 (200*5=1000 < 2100)
     *  使用场景: HrOrgRelationServiceImpl
     *  VO类: HrOrgRelationImportVO (恒瑞比对关系导入) */
    public static final int BATCH_SIZE_5_FIELDS = 200;

    /** 4 字段 VO，每批 100 条
     *  使用场景: MdLocationBatchAsyncService
     *  VO类: MdLocationVO (地理位置批次处理) */
    public static final int BATCH_SIZE_4_FIELDS = 100;

    /** 3 字段 VO，每批 100 条
     *  使用场景: MdFuzzyMatchProcessServiceImpl
     *  VO类: FuzzyMatchDataDTO (主数据模糊匹配) */
    public static final int BATCH_SIZE_3_FIELDS = 100;

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
     * 流式读取 Excel，分批回调处理
     * 每读取 batchSize 条数据后立即回调处理，避免全量数据驻留内存
     *
     * @param filePath     文件路径
     * @param clazz        目标类
     * @param batchConsumer 分批回调（每批数据立即处理）
     * @param batchSize    分批大小
     * @param <T>          泛型
     * @throws IOException 文件异常
     */
    public <T> void readExcelStreaming(String filePath, Class<T> clazz,
                                        Consumer<List<T>> batchConsumer, int batchSize) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }

        try {
            EasyExcel.read(file)
                    .registerReadListener(new StreamingReadListener<>(clazz, batchConsumer, batchSize))
                    .autoCloseStream(true)
                    .ignoreEmptyRow(true)
                    .headRowNumber(1)
                    .sheet()
                    .doRead();

        } catch (Exception e) {
            throw new IOException("读取Excel文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式读取监听器，分批回调处理
     */
    private static class StreamingReadListener<T> implements ReadListener<Map<Integer, String>> {
        private final Class<T> targetClass;
        private final Consumer<List<T>> batchConsumer;
        private final int batchSize;
        private final List<T> currentBatch;
        private Map<Integer, String> headerMap;
        private final Map<String, Field> fieldMap;

        public StreamingReadListener(Class<T> clazz, Consumer<List<T>> batchConsumer, int batchSize) {
            this.targetClass = clazz;
            this.batchConsumer = batchConsumer;
            this.batchSize = batchSize;
            this.currentBatch = new ArrayList<>(batchSize);
            this.fieldMap = buildFieldMap(clazz);
        }

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
                    currentBatch.add(obj);
                    // 达到分批大小，立即回调处理并清空
                    if (currentBatch.size() >= batchSize) {
                        batchConsumer.accept(new ArrayList<>(currentBatch));
                        currentBatch.clear();
                    }
                }
            } catch (Exception e) {
                System.err.println("解析第 " + (context.readRowHolder().getRowIndex() + 1) + " 行失败: " + e.getMessage());
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 处理剩余数据（不足 batchSize 的最后一批）
            if (!currentBatch.isEmpty()) {
                batchConsumer.accept(new ArrayList<>(currentBatch));
                currentBatch.clear();
            }
        }

        private T convertRowToObject(Map<Integer, String> rowData) throws Exception {
            if (rowData == null || rowData.isEmpty() || headerMap == null) {
                return null;
            }

            T obj = targetClass.getDeclaredConstructor().newInstance();
            boolean hasData = false;

            for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
                Integer columnIndex = entry.getKey();
                String cellValue = entry.getValue();

                String headerName = headerMap.get(columnIndex);
                if (headerName == null || cellValue == null) {
                    continue;
                }

                cellValue = cellValue.trim();
                Field field = fieldMap.get(headerName);
                if (field != null && !cellValue.isEmpty()) {
                    field.set(obj, cellValue);
                    hasData = true;
                }
            }

            return hasData ? obj : null;
        }
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