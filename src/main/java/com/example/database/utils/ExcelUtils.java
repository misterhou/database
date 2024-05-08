package com.example.database.utils;

import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
}
