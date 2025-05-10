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

public class ExcelReader {

    public <T> List<T> readExcel(String filePath, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return dataList; // 没有数据行
            }

            // 获取表头行
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> headerMap = getHeaderMap(headerRow);

            // 遍历数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    T obj = clazz.getDeclaredConstructor().newInstance();
                    boolean hasData = false;

                    // 设置对象属性
                    for (Field field : clazz.getDeclaredFields()) {
                        ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                        if (annotation == null) continue;

                        String headerName = annotation.name();
                        Integer columnIndex = headerMap.get(headerName);
                        if (columnIndex == null) continue;

                        Cell cell = row.getCell(columnIndex);
                        if (cell == null) continue;

                        // 简单处理：所有值都转为字符串
                        String value = getCellStringValue(cell);
                        if (value != null) {
                            field.setAccessible(true);
                            field.set(obj, value);
                            hasData = true;
                        }
                    }

                    if (hasData) {
                        dataList.add(obj);
                    }
                } catch (Exception e) {
                    System.err.println("创建对象失败: " + e.getMessage());
                }
            }
        }

        return dataList;
    }

    /**
     * 获取表头映射关系
     */
    private Map<String, Integer> getHeaderMap(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String headerName = cell.getStringCellValue().trim();
            headerMap.put(headerName, cell.getColumnIndex());
        }
        return headerMap;
    }

    /**
     * 简单获取单元格字符串值
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}