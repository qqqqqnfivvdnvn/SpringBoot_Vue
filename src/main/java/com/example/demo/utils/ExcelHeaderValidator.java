package com.example.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.metadata.data.ReadCellData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Excel表头验证工具类（通用版）
 * 用于验证上传的Excel文件表头是否与预期一致
 * 只检查预期表头中的列是否都存在，多余列忽略
 */
public class ExcelHeaderValidator {

    /**
     * 验证Excel表头是否与预期一致
     * @param filePath Excel文件路径
     * @param expectedHeaders 预期的表头列表
     * @return 验证结果
     * @throws IOException 文件读取异常
     */
    public static HeaderValidationResult validate(String filePath, List<String> expectedHeaders) throws IOException {
        // 参数校验
        if (expectedHeaders == null || expectedHeaders.isEmpty()) {
            throw new IllegalArgumentException("预期表头不能为空");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }

        // 存储验证结果
        HeaderValidationResult result = new HeaderValidationResult();
        result.setExpectedHeaders(expectedHeaders);

        try {
            EasyExcel.read(file)
                    .registerReadListener(new ReadListener<Map<Integer, String>>() {
                        @Override
                        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                            // 读取实际表头（按列索引排序，保持顺序）
                            List<String> actualHeaders = new ArrayList<>();
                            Map<Integer, String> headerMap = new TreeMap<>(); // TreeMap按key排序

                            for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                                String header = entry.getValue().getStringValue();
                                if (header != null) {
                                    headerMap.put(entry.getKey(), header.trim());
                                }
                            }

                            // 按列索引顺序添加到列表
                            actualHeaders.addAll(headerMap.values());
                            result.setActualHeaders(actualHeaders);

                            // 执行验证（只检查预期列是否存在）
                            validateHeaders(result, expectedHeaders, actualHeaders);
                        }

                        @Override
                        public void invoke(Map<Integer, String> data, AnalysisContext context) {
                            // 不处理数据行，只验证表头
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            // 完成
                        }
                    })
                    .sheet(0)  // 只读取第一个sheet
                    .headRowNumber(1)  // 表头在第1行
                    .doRead();
        } catch (Exception e) {
            throw new IOException("读取Excel表头失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 验证Excel表头（可指定sheet和表头行数）
     * @param filePath Excel文件路径
     * @param expectedHeaders 预期的表头列表
     * @param sheetIndex sheet索引（从0开始）
     * @param headRowNumber 表头所在行数（从1开始计数）
     * @return 验证结果
     * @throws IOException 文件读取异常
     */
    public static HeaderValidationResult validate(String filePath, List<String> expectedHeaders,
                                                  int sheetIndex, int headRowNumber) throws IOException {
        // 参数校验
        if (expectedHeaders == null || expectedHeaders.isEmpty()) {
            throw new IllegalArgumentException("预期表头不能为空");
        }
        if (sheetIndex < 0) {
            throw new IllegalArgumentException("sheet索引不能小于0");
        }
        if (headRowNumber < 1) {
            throw new IllegalArgumentException("表头行数不能小于1");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }

        // 存储验证结果
        HeaderValidationResult result = new HeaderValidationResult();
        result.setExpectedHeaders(expectedHeaders);

        try {
            EasyExcel.read(file)
                    .registerReadListener(new ReadListener<Map<Integer, String>>() {
                        @Override
                        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                            // 读取实际表头（按列索引排序）
                            List<String> actualHeaders = new ArrayList<>();
                            Map<Integer, String> headerMap = new TreeMap<>();

                            for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                                String header = entry.getValue().getStringValue();
                                if (header != null) {
                                    headerMap.put(entry.getKey(), header.trim());
                                }
                            }

                            actualHeaders.addAll(headerMap.values());
                            result.setActualHeaders(actualHeaders);

                            // 执行验证（只检查预期列是否存在）
                            validateHeaders(result, expectedHeaders, actualHeaders);
                        }

                        @Override
                        public void invoke(Map<Integer, String> data, AnalysisContext context) {
                            // 不处理数据行，只验证表头
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            // 完成
                        }
                    })
                    .sheet(sheetIndex)
                    .headRowNumber(headRowNumber)
                    .doRead();
        } catch (Exception e) {
            throw new IOException("读取Excel表头失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 验证表头（只检查预期列是否存在，多余列忽略）
     */
    private static void validateHeaders(HeaderValidationResult result,
                                        List<String> expected,
                                        List<String> actual) {
        // 只检查缺少的列（预期中有，实际没有）
        List<String> missingHeaders = new ArrayList<>();

        for (String expectedHeader : expected) {
            if (!actual.contains(expectedHeader)) {
                missingHeaders.add(expectedHeader);
            }
        }

        result.setMissingHeaders(missingHeaders);

        // 多余列不报错，但记录下来供参考
        List<String> extraHeaders = new ArrayList<>();
        for (String actualHeader : actual) {
            if (!expected.contains(actualHeader)) {
                extraHeaders.add(actualHeader);
            }
        }
        result.setExtraHeaders(extraHeaders);

        // 如果有缺少的列，验证失败
        if (!missingHeaders.isEmpty()) {
            result.setValid(false);

            StringBuilder message = new StringBuilder();
            message.append("缺少必需的列(").append(missingHeaders.size()).append("个)：")
                    .append(String.join("、", missingHeaders));

            // 可选：提示有多余列，但不影响验证结果
            if (!extraHeaders.isEmpty()) {
                message.append("（另有").append(extraHeaders.size()).append("个多余列已忽略）");
            }

            result.setMessage(message.toString());
        } else {
            // 全部通过
            result.setValid(true);

            if (!extraHeaders.isEmpty()) {
                result.setMessage("表头验证通过（忽略" + extraHeaders.size() + "个多余列）");
            } else {
                result.setMessage("表头验证通过");
            }
        }
    }

    /**
     * 获取实际表头中存在的预期列的位置信息
     * @param filePath Excel文件路径
     * @param expectedHeaders 预期的表头列表
     * @return 返回每个预期列在实际表中的列位置（从1开始），不存在的列为null
     * @throws IOException 文件读取异常
     */
    public static Map<String, Integer> getHeaderPositions(String filePath, List<String> expectedHeaders) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }

        Map<String, Integer> positionMap = new HashMap<>();
        // 初始化所有预期列为null
        for (String header : expectedHeaders) {
            positionMap.put(header, null);
        }

        try {
            EasyExcel.read(file)
                    .registerReadListener(new ReadListener<Map<Integer, String>>() {
                        @Override
                        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                            // 遍历实际表头，记录每个列的位置
                            for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                                String header = entry.getValue().getStringValue();
                                if (header != null) {
                                    String trimmedHeader = header.trim();
                                    // 如果是预期列，记录位置（列号从1开始）
                                    if (expectedHeaders.contains(trimmedHeader)) {
                                        positionMap.put(trimmedHeader, entry.getKey() + 1);
                                    }
                                }
                            }
                        }

                        @Override
                        public void invoke(Map<Integer, String> data, AnalysisContext context) {}

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {}
                    })
                    .sheet(0)
                    .headRowNumber(1)
                    .doRead();
        } catch (Exception e) {
            throw new IOException("读取Excel表头失败: " + e.getMessage());
        }

        return positionMap;
    }

    /**
     * 验证结果类
     */
    public static class HeaderValidationResult {
        private boolean valid;
        private String message;
        private List<String> expectedHeaders;
        private List<String> actualHeaders;
        private List<String> missingHeaders = new ArrayList<>();
        private List<String> extraHeaders = new ArrayList<>();

        public HeaderValidationResult() {
            this.valid = false;
            this.message = "未验证";
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getExpectedHeaders() {
            return expectedHeaders;
        }

        public void setExpectedHeaders(List<String> expectedHeaders) {
            this.expectedHeaders = expectedHeaders;
        }

        public List<String> getActualHeaders() {
            return actualHeaders;
        }

        public void setActualHeaders(List<String> actualHeaders) {
            this.actualHeaders = actualHeaders;
        }

        public List<String> getMissingHeaders() {
            return missingHeaders;
        }

        public void setMissingHeaders(List<String> missingHeaders) {
            this.missingHeaders = missingHeaders;
        }

        public List<String> getExtraHeaders() {
            return extraHeaders;
        }

        public void setExtraHeaders(List<String> extraHeaders) {
            this.extraHeaders = extraHeaders;
        }

        /**
         * 获取缺失列的数量
         */
        public int getMissingCount() {
            return missingHeaders.size();
        }

        /**
         * 获取多余列的数量
         */
        public int getExtraCount() {
            return extraHeaders.size();
        }

        /**
         * 检查是否所有预期列都存在
         */
        public boolean isAllExpectedPresent() {
            return missingHeaders.isEmpty();
        }

        @Override
        public String toString() {
            return "HeaderValidationResult{" +
                    "valid=" + valid +
                    ", message='" + message + '\'' +
                    ", expectedCount=" + (expectedHeaders != null ? expectedHeaders.size() : 0) +
                    ", actualCount=" + (actualHeaders != null ? actualHeaders.size() : 0) +
                    ", missingCount=" + missingHeaders.size() +
                    ", extraCount=" + extraHeaders.size() +
                    ", missingHeaders=" + missingHeaders +
                    ", extraHeaders=" + extraHeaders +
                    '}';
        }
    }
}