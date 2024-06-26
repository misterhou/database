package com.example.database.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelUtils {

    public static List<Map<String, String>> getData(String excelFile, Integer sheetIndex, String valueColumnName,
                               Integer keyColumnOffset) throws Exception {
        List<Map<String, String>> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(excelFile);
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = cell.toString();
                if (valueColumnName.equals(cellValue)) {
                    int startRowNum = cell.getRowIndex();
                    int startCellNum = cell.getColumnIndex();
                    log.info(valueColumnName + " row: " + startRowNum + ", column: " + startCellNum);
                    for (int i = (startRowNum + 1); i <= sheet.getLastRowNum(); i++) {
                        Row commandRow = sheet.getRow(i);
                        Cell commandColumn = commandRow.getCell(startCellNum);
                        if (null == commandColumn) {
                            log.warn("遇到空白数据列，退出");
                            break;
                        } else if (!StringUtils.hasText(commandColumn.toString())) {
                            log.warn("遇到空白数据列，退出（row：{}，column：{}）", commandColumn.getRowIndex(),
                                    commandColumn.getColumnIndex());
                            break;
                        }
                        int commandValueColumnIndex = commandColumn.getColumnIndex();
                        int minRowIndex = 0;
                        String value = getColumnValue(sheet, i, commandValueColumnIndex, minRowIndex);
                        Integer keyColumnIndex = commandValueColumnIndex + keyColumnOffset;
                        String key = getColumnValue(sheet, i, keyColumnIndex, minRowIndex);
                        Map<String, String> dataRow = new HashMap<>();
                        dataRow.put(key, value);
                        data.add(dataRow);
                        if ("306".equals(key)) {
                            System.out.println(key);
                        }
                        System.out.println("rowIndex: " + i + " - key：" + key + "， value：" + value);
                    }
                    log.info("\n=========================================================================================================");
                }
            }
        }
        workbook.close();
        return data;
    }

    public static List<Map<String, String>> getData2(String excelFile, Integer sheetIndex, String valueColumnName,
            List<Integer> keyColumnOffsetList) throws Exception {
        List<Map<String, String>> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(excelFile);
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = cell.toString();
                if (valueColumnName.equals(cellValue)) {
                    int startRowNum = cell.getRowIndex();
                    int startCellNum = cell.getColumnIndex();
                    log.info(valueColumnName + " row: " + startRowNum + ", column: " + startCellNum);
                    for (int i = (startRowNum + 1); i <= sheet.getLastRowNum(); i++) {
                        Row commandRow = sheet.getRow(i);
                        Cell commandColumn = commandRow.getCell(startCellNum);
                        if (null == commandColumn) {
                            log.warn("遇到空白数据列，退出");
                            break;
                        } else if (!StringUtils.hasText(commandColumn.toString())) {
                            log.warn("遇到空白数据列，退出（row：{}，column：{}）", commandColumn.getRowIndex(),
                                    commandColumn.getColumnIndex());
                            break;
                        }
                        int commandValueColumnIndex = commandColumn.getColumnIndex();
                        int minRowIndex = 0;
                        StringBuilder key = new StringBuilder();
                        String value = getColumnValue(sheet, i, commandValueColumnIndex, minRowIndex);
                        for (Integer keyColumnOffset : keyColumnOffsetList) {
                            Integer keyColumnIndex = commandValueColumnIndex + keyColumnOffset;
                            String subKey = getColumnValue(sheet, i, keyColumnIndex, minRowIndex);
                            subKey = replaceSpecialSymbol(subKey);
                            key.append(subKey);
                        }
                        Map<String, String> dataRow = new HashMap<>();
                        dataRow.put(key.toString(), value);
                        data.add(dataRow);
                        System.out.println("rowIndex: " + i + " - key：" + key + "， value：" + value);
                    }
                    log.info("\n=========================================================================================================");
                }
            }
        }
        workbook.close();
        return data;
    }

    /**
     * 获取厂站联络图配置
     *
     * @param excelFile excel文件
     * @throws Exception 读取文件异常
     */
    public static String getSubstationContactPicConfig(String excelFile) throws Exception {
        StringBuilder fileContent = new StringBuilder();
        FileInputStream fis = new FileInputStream(excelFile);
        Workbook workbook = WorkbookFactory.create(fis);
        int sheetIndex = 0;
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        for (Row row : sheet) {
            Cell idCell = row.getCell(0);
            Cell nameCell = row.getCell(1);
            String idValue = idCell.toString();
            String nameValue = nameCell.toString();
            fileContent.append("打开").append(nameValue).append("联络图\t")
                    .append("112_").append(idValue)
                    .append("\t开图指令\n");
//            System.out.println("打开" + nameValue + "联络图\t" + "112_" + idValue + "\t开图指令");
        }
        workbook.close();
        return fileContent.toString();
    }

    /**
     * 获取单元格中的数据（字符串形式）
     *
     * @param sheet 工作表
     * @param rowIndex 行索引
     * @param columnIndex 列索引
     * @param minRowIndex 最小行索引（合并单元格只有第一行有数据）
     * @return 单元格中的数据
     */
    private static String getColumnValue(Sheet sheet, Integer rowIndex, Integer columnIndex, Integer minRowIndex) {
        if (minRowIndex < 0) {
            minRowIndex = 0;
        }
        Row row = sheet.getRow(rowIndex);
        Cell cell = row.getCell(columnIndex);
        String cellValue = cell.toString().trim();
        if (cell.getCellType() == CellType.NUMERIC) {
            // 去除小数
            cellValue = cellValue.replaceFirst("\\.\\d*", "");
        }
        return cellValue;
    }

    /**
     * 特殊字符处理
     *
     * @param text 待处理文本
     * @return 处理后的文本
     */
    private static String replaceSpecialSymbol(String text) {
        text = text.replaceAll("#1|1#", "1号");
        text = text.replaceAll("#2|2#", "2号");
        text = text.replaceAll("#3|3#", "3号");
        text = text.replaceAll("#4|4#", "4号");
        text = text.replaceAll("#5|5#", "5号");
        return text;
    }
}
