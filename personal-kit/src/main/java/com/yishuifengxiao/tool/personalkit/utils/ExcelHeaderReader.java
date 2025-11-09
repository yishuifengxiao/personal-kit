package com.yishuifengxiao.tool.personalkit.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 高性能Excel表头读取工具类 (适配POI 4.1.0)
 * 仅读取第一行表头，不解析多余内容，确保最佳性能和最低内存占用
 */
public final class ExcelHeaderReader {
    
    private ExcelHeaderReader() {
        // 工具类，防止实例化
    }
    
    /**
     * 读取Excel文件的表头（仅第一行）
     * @param filePath 文件路径
     * @return 表头列表，如果读取失败返回空列表
     */
    public static List<String> readHeaders(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.err.println("文件不存在或不是文件: " + filePath);
            return new ArrayList<>();
        }
        
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".xlsx")) {
            return readXlsxHeaders(filePath);
        } else if (fileName.endsWith(".xls")) {
            return readXlsHeaders(filePath);
        } else {
            System.err.println("不支持的文件格式: " + filePath);
            return new ArrayList<>();
        }
    }
    
    /**
     * 读取.xlsx格式表头（仅第一行）
     */
    private static List<String> readXlsxHeaders(String filePath) {
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;
        
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            
            // 只获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            
            // 只读取第一行
            return extractHeaderRow(sheet);
            
        } catch (Exception e) {
            System.err.println("读取.xlsx文件表头失败: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            // 立即关闭资源，释放内存
            closeQuietly(workbook);
            closeQuietly(fis);
        }
    }
    
    /**
     * 读取.xls格式表头（仅第一行）
     */
    private static List<String> readXlsHeaders(String filePath) {
        FileInputStream fis = null;
        HSSFWorkbook workbook = null;
        
        try {
            fis = new FileInputStream(filePath);
            workbook = new HSSFWorkbook(fis);
            
            // 只获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            
            // 只读取第一行
            return extractHeaderRow(sheet);
            
        } catch (Exception e) {
            System.err.println("读取.xls文件表头失败: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            // 立即关闭资源，释放内存
            closeQuietly(workbook);
            closeQuietly(fis);
        }
    }
    
    /**
     * 提取表头行数据
     */
    private static List<String> extractHeaderRow(Sheet sheet) {
        List<String> headers = new ArrayList<>();
        
        // 只获取第一行
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return headers;
        }
        
        DataFormatter formatter = new DataFormatter();
        
        // 只遍历第一行的单元格，限制最大列数避免性能问题
        int maxColumns = Math.min(headerRow.getLastCellNum(), 500);
        for (int i = 0; i < maxColumns; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                // 使用POI 4.1.0兼容的方式获取单元格值
                String value = getCellValueAsString(cell, formatter);
                if (!value.isEmpty()) {
                    headers.add(value);
                }
            } else {
                // 遇到空单元格，如果后面还有非空单元格继续读取
                // 但限制最大列数避免性能问题
                if (i > 100 && isRestCellsEmpty(headerRow, i, 5)) {
                    break;
                }
            }
        }
        
        return headers;
    }
    
    /**
     * 检查后续单元格是否为空（避免读取过多空列）
     */
    private static boolean isRestCellsEmpty(Row row, int startIndex, int checkCount) {
        DataFormatter formatter = new DataFormatter();
        for (int i = startIndex; i < Math.min(row.getLastCellNum(), startIndex + checkCount); i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                String value = formatter.formatCellValue(cell).trim();
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 安全获取单元格值（适配POI 4.1.0）
     */
    private static String getCellValueAsString(Cell cell, DataFormatter formatter) {
        try {
            // 使用DataFormatter安全获取各种类型的单元格值
            return formatter.formatCellValue(cell).trim();
        } catch (Exception e) {
            // 如果格式化失败，尝试基本方式获取
            try {
                // 使用POI 4.1.0的CellType枚举
                CellType cellType = cell.getCellType();
                
                switch (cellType) {
                    case STRING:
                        return cell.getStringCellValue().trim();
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            return cell.getDateCellValue().toString();
                        } else {
                            // 处理数值，避免显示科学计数法
                            double numericValue = cell.getNumericCellValue();
                            if (numericValue == Math.floor(numericValue) && !Double.isInfinite(numericValue)) {
                                return String.valueOf((long) numericValue);
                            } else {
                                return String.valueOf(numericValue);
                            }
                        }
                    case BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                    case FORMULA:
                        // 对于公式单元格，尝试获取计算公式结果
                        try {
                            return formatter.formatCellValue(cell);
                        } catch (Exception ex) {
                            return cell.getCellFormula();
                        }
                    case BLANK:
                        return "";
                    case ERROR:
                        return "ERROR";
                    default:
                        return "";
                }
            } catch (Exception ex) {
                return "";
            }
        }
    }
    
    /**
     * 安静关闭资源
     */
    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 忽略关闭异常
            }
        }
    }
}