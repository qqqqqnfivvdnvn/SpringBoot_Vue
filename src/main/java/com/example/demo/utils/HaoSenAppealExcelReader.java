package com.example.demo.utils;

import com.example.demo.annotation.ExcelColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HaoSenAppealExcelReader {

    /**
     * 读取Excel文件，所有数据按字符串处理
     * @param filePath 文件路径
     * @param clazz    目标类
     * @param <T>      泛型
     * @return 对象列表
     * @throws IOException 文件异常
     */
    public <T> List<T> readExcel(String filePath, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return dataList; // 无数据
            }

            // 构建表头映射（列名 -> 列索引）
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> headerMap = buildHeaderMap(headerRow);

            // 遍历数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                T obj = createObjectFromRow(row, clazz, headerMap);
                if (obj != null) {
                    dataList.add(obj);
                }
            }
        }

        return dataList;
    }

    /**
     * 构建表头映射关系
     */
    private Map<String, Integer> buildHeaderMap(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String headerName = getCellValueAsString(cell);
            if (headerName != null) {
                headerMap.put(headerName.trim(), cell.getColumnIndex());
            }
        }
        return headerMap;
    }

    /**
     * 从数据行创建对象
     */
    private <T> T createObjectFromRow(Row row, Class<T> clazz, Map<String, Integer> headerMap) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            boolean hasData = false;

            for (Field field : clazz.getDeclaredFields()) {
                ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                if (annotation == null) continue;

                Integer colIndex = headerMap.get(annotation.name());
                if (colIndex == null) continue;

                String cellValue = getCellValueAsString(row.getCell(colIndex));
                if (cellValue != null) {
                    field.setAccessible(true);
                    field.set(obj, cellValue); // 全部作为字符串写入
                    hasData = true;
                }
            }

            return hasData ? obj : null;
        } catch (Exception e) {
            System.err.println("行解析失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 核心方法：获取单元格字符串值（强制所有类型转为String）
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        // 使用DataFormatter直接按Excel显示格式转为字符串
        return new DataFormatter().formatCellValue(cell).trim();
    }
}