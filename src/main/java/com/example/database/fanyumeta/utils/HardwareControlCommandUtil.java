package com.example.database.fanyumeta.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 中控指令工具
 */
@Slf4j
public class HardwareControlCommandUtil {

//    /**
//     * 请求指令缓存
//     */
//    private static final Map<String, String> REQUEST_COMMAND_CACHE = new HashMap<>(256);

    /**
     * 请求指令缓存
     */
    private static final Map<String, List<HardwareCommand>> REQUEST_COMMAND_CACHE2 = new HashMap<>(256);

    /**
     * 响应指令缓存
     */
    private static final Map<String, String> RECEIVE_COMMAND_CACHE = new HashMap<>(256);

    /**
     * 需要忽略的描述
     */
    private static final List<String> IGNORE_DESCRIPTION = Arrays.asList("大屏前空调", "1号", "2号", "参观台1区空调", "参观台2区空调",
            "合二为一", "东侧灯带", "西侧灯带");

    private static final Map<String, String> REPLACE_DESCRIPTION = new HashMap<>();

    /**
     * 设备列表（用于区分设备大类）
     */
    private static final List<String> DEVICE_LIST = Arrays.asList(
            "大屏前西侧空调",
            "大屏前东侧空调",
            "参观台西侧空调",
            "参观台东侧空调",
            "工作区西侧空调",
            "工作区东侧空调",


            "工作区全部灯光",
            "工作一区全部灯光",
            "工作二区全部灯光",
            "工作三区全部灯光",
            "工作四区全部灯光",

            "工作一区南排灯光",
            "工作二区南排灯光",
            "工作三区南排灯光",
            "工作四区南排灯光",

            "工作一区北排灯光",
            "工作二区北排灯光",
            "工作三区北排灯光",
            "工作四区北排灯光",

            "东北侧灯带",
            "东南侧灯带",
            "西北侧灯带",
            "西南侧灯带",
            "东侧曲线灯带",
            "西侧曲线灯带",
            "参观台顶部灯带",
            "参观台后墙灯带",
            "休息区柱子灯带",

            "灯光模式",


            "西门",
            "东门",
            "四楼西门",
            "四楼东门",
            "五楼西门",
            "五楼东门",


            "窗帘",
            "西侧窗帘",
            "东侧窗帘",


            "拼控",

            "音频",

            "坐席台灯带");

    static {
        REPLACE_DESCRIPTION.put("18", "(18|十八)");
        REPLACE_DESCRIPTION.put("19", "(19|十九)");
        REPLACE_DESCRIPTION.put("20", "(20|二十)");
        REPLACE_DESCRIPTION.put("21", "(21|二十一)");
        REPLACE_DESCRIPTION.put("22", "(22|二十二)");
        REPLACE_DESCRIPTION.put("23", "(23|二十三)");
        REPLACE_DESCRIPTION.put("24", "(24|二十四)");
        REPLACE_DESCRIPTION.put("25", "(25|二十五)");
        REPLACE_DESCRIPTION.put("26", "(26|二十六)");
        REPLACE_DESCRIPTION.put("27", "(27|二十七)");
        REPLACE_DESCRIPTION.put("28", "(28|二十八)");
        REPLACE_DESCRIPTION.put("29", "(29|二十九)");
        REPLACE_DESCRIPTION.put("30", "(30|三十)");

        REPLACE_DESCRIPTION.put("1", "(1|一)");
        REPLACE_DESCRIPTION.put("2", "(2|二)");
        REPLACE_DESCRIPTION.put("3", "(3|三)");
        REPLACE_DESCRIPTION.put("4", "(4|四)");
        REPLACE_DESCRIPTION.put("5", "(5|五)");
        REPLACE_DESCRIPTION.put("6", "(6|六)");
        REPLACE_DESCRIPTION.put("7", "(7|七)");
        REPLACE_DESCRIPTION.put("8", "(8|八)");
        REPLACE_DESCRIPTION.put("9", "(9|九)");
    }

//    /**
//     * 解析文本中的指令
//     *
//     * @param message 文本信息
//     * @return 指令集合
//     */
//    public static List<String> parse(String message) {
//        List<String> command = new CopyOnWriteArrayList<>();
//        for (String regStr : REQUEST_COMMAND_CACHE.keySet()) {
//            Pattern pattern = Pattern.compile(regStr);
////            if (pattern.matcher(message).matches()) {
//            if (pattern.matcher(message).find()) {
//                command.add(REQUEST_COMMAND_CACHE.get(regStr));
//            }
//        }
//        return command;
//    }

    /**
     * 获取指令值
     *
     * @param message 指令描述
     * @return 指令值
     */
    public static String getCommandValue(String message) {
        String commandValue = null;
        if (isFind("空调", message)) {
            // 大屏前空调
            if (isFind("屏", message)) {
                if (isFind("(西侧|东)", message)) {
                    commandValue = getRequestCommand("大屏前西侧空调", message);
                }
                if (isFind("(东侧| 东)", message)) {
                    commandValue = getRequestCommand("大屏前东侧空调", message);
                }
            }
            // 工作区空调
            if (isFind("工作区", message)) {
                if (isFind("(西侧|东)", message)) {
                    commandValue = getRequestCommand("工作区西侧空调", message);
                }
                if (isFind("(东侧| 东)", message)) {
                    commandValue = getRequestCommand("工作区东侧空调", message);
                }
            }
            // 参观台空调
            if (isFind("参观台", message)) {
                if (isFind("(西侧|东)", message)) {
                    commandValue = getRequestCommand("参观台西侧空调", message);
                }
                if (isFind("(东侧| 东)", message)) {
                    commandValue = getRequestCommand("参观台东侧空调", message);
                }
            }
        }

        // 灯带
        else if (isFind("灯带", message)) {
            if (isFind("东北", message)) {
                commandValue = getRequestCommand("东北侧灯带", message);
            } else if (isFind("东南", message)) {
                commandValue = getRequestCommand("东南侧灯带", message);
            } else if (isFind("西北", message)) {
                commandValue = getRequestCommand("西北侧灯带", message);
            } else if (isFind("西南", message)) {
                commandValue = getRequestCommand("西南侧灯带", message);
            } else if (isFind("曲线", message)) {
                if (isFind("东", message)) {
                    commandValue = getRequestCommand("东侧曲线灯带", message);
                } else if (isFind("西", message)) {
                    commandValue = getRequestCommand("西侧曲线灯带", message);
                }
            } else if (isFind("参观台", message)) {
                if (isFind("顶", message)) {
                    commandValue = getRequestCommand("参观台顶部灯带", message);
                } else if (isFind("后", message)) {
                    commandValue = getRequestCommand("参观台后墙灯带", message);
                }
            } else if (isFind("休息区", message) || isFind("柱子", message)) {
                commandValue = getRequestCommand("休息区柱子灯带", message);
            } else if (isFind("坐席台", message)) {
                commandValue = getRequestCommand("坐席台灯带", message);
            }
        }

        // 灯光模式
        else if (isFind("灯光模式", message)) {
            commandValue = getRequestCommand("灯光模式", message);
        }

        // 灯光
        else if (isFind("灯", message)) {
            if (isFind("工作区", message)) {
                commandValue = getRequestCommand("工作区全部灯光", message);
            } else if (isFind("工作(1|一)区", message)) {
                if (isFind("南排|南", message)) {
                    commandValue = getRequestCommand("工作一区南排灯光", message);
                } else if (isFind("北排|北", message)) {
                    commandValue = getRequestCommand("工作一区北排灯光", message);
                } else {
                    commandValue = getRequestCommand("工作一区全部灯光", message);
                }
            } else if (isFind("工作(2|二)区", message)) {
                if (isFind("南排|南", message)) {
                    commandValue = getRequestCommand("工作二区南排灯光", message);
                } else if (isFind("北排|北", message)) {
                    commandValue = getRequestCommand("工作二区北排灯光", message);
                } else {
                    commandValue = getRequestCommand("工作二区全部灯光", message);
                }
            } else if (isFind("工作(3|三)区", message)) {
                if (isFind("南排|南", message)) {
                    commandValue = getRequestCommand("工作三区南排灯光", message);
                } else if (isFind("北排|北", message)) {
                    commandValue = getRequestCommand("工作三区北排灯光", message);
                } else {
                    commandValue = getRequestCommand("工作三区全部灯光", message);
                }
            } else if (isFind("工作(4|四)区", message)) {
                if (isFind("南排|南", message)) {
                    commandValue = getRequestCommand("工作四区南排灯光", message);
                } else if (isFind("北排|北", message)) {
                    commandValue = getRequestCommand("工作四区北排灯光", message);
                } else {
                    commandValue = getRequestCommand("工作四区全部灯光", message);
                }
            }

        }

        // 门
        else if (isFind("门", message)) {
            if (isFind("东", message)) {
                if (isFind("4|四", message)) {
                    commandValue = getRequestCommand("四楼东门", message);
                } else if (isFind("5|五", message)) {
                    commandValue = getRequestCommand("五楼东门", message);
                } else {
                    commandValue = getRequestCommand("东门", message);
                }
            } else if (isFind("西", message)) {
                if (isFind("4|四", message)) {
                    commandValue = getRequestCommand("四楼西门", message);
                } else if (isFind("5|五", message)) {
                    commandValue = getRequestCommand("五楼西门", message);
                } else {
                    commandValue = getRequestCommand("西门", message);
                }
            }

        }

        // 窗帘
        else if (isFind("窗帘", message)) {
            if (isFind("西", message)) {
                commandValue = getRequestCommand("西侧窗帘", message);
            } else if (isFind("东", message)) {
                commandValue = getRequestCommand("东侧窗帘", message);
            } else {
                commandValue = getRequestCommand("窗帘", message);
            }
        }

        // 拼控
        else if (isFind("拼控", message)) {
            commandValue = getRequestCommand("拼控", message);
        }

        // 音频
        else if (isFind("音频", message)) {
            commandValue = getRequestCommand("音频", message);
        }
        return commandValue;
    }

    private static boolean isFind(String reg, String message) {
        boolean result = false;
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(message).find();
    }

    private static String getRequestCommand(String device, String message) {
        String commandValue = null;
        List<HardwareCommand> hardwareCommandList = REQUEST_COMMAND_CACHE2.get(device);
        if (!ObjectUtils.isEmpty(hardwareCommandList)) {
            for (HardwareCommand hardwareCommand : hardwareCommandList) {
                String description = hardwareCommand.getDescription();
                Pattern pattern = Pattern.compile(description);
                if (pattern.matcher(message).find()) {
                    commandValue = hardwareCommand.getValue();
                }
            }
        }
        return commandValue;
    }


    /**
     * 获取响应指令描述
     *
     * @param commandValue 响应指令
     * @return 响应指令描述
     */
    public static String getReceiveCommandDescription(String commandValue) {
        return RECEIVE_COMMAND_CACHE.get(commandValue.replace("\r\n", ""));
    }

    /**
     * 通过解析 excel 文件，生成指令缓存文件
     *d
     * @param excelFile excel 文件
     * @param commandCacheFile 缓存文件
     */
    public static void generateRequestCommandCacheFile(String excelFile, String commandCacheFile) throws IOException {
        int sheetIndex = 0;
        String targetColumnData = "与小鸟对接口号";
        int commandDescriptionColumnOffset = -2;
        int maxCommandDescriptionColumnLength = 4;
        boolean isContainCurrentColumn = false;

        List<Command> data = getCommandInfo(excelFile, sheetIndex, targetColumnData, commandDescriptionColumnOffset,
                maxCommandDescriptionColumnLength, isContainCurrentColumn, true);
//        Map<String, String> cache = new HashMap<>();
//        for (Command command : data) {
//            String value = command.getValue();
//            List<String> description = command.getDescription();
//            String commandKey = getCommandKey(description);
//            cache.put(commandKey, value);
//        }
        Map<String, List<HardwareCommand>> cache = new HashMap<>();
        for (String device : DEVICE_LIST) {
            cache.put(device, new ArrayList<>());
            for (Command command : data) {
                List<String> descriptionList = command.getDescription();
                for (String description : descriptionList) {
                    if (descriptionList.size() == 3) {
                        String des2 = descriptionList.get(1);
                        String des3 = descriptionList.get(2);
                        description = des3 + des2;
                        if (isFind("灯光模式", description)) {
                            description = "灯光模式";
                        }
                        if (isFind(description, device)) {
                            List<HardwareCommand> hardwareCommandList = cache.get(device);
                            hardwareCommandList.add(new HardwareCommand(command.getValue(), descriptionList.get(0)));
                        }
                        break;
                    }
                    if (device.equals(description)) {
                        List<HardwareCommand> hardwareCommandList = cache.get(device);
                        hardwareCommandList.add(new HardwareCommand(command.getValue(), descriptionList.get(0)));
                    }
                }
            }
        }
//        for (Command command : data) {
//            List<String> descriptionList = command.getDescription();
//            for (String description : descriptionList) {
//                if (!DEVICE_LIST.contains(description)) {
//                    new HardwareCommand(command.getValue(), description);
//                }
//            }
//        }
//        System.out.println(cache);
        writeObject2File(cache, commandCacheFile);
    }

    public static void generateRequestReceiveCommandCacheFile(String excelFile, String commandCacheFile) throws IOException {
        int sheetIndex = 0;
        String targetColumnData = "与小鸟对接口号";
        int commandDescriptionColumnOffset = 1;
        int maxCommandDescriptionColumnLength = 1;
        boolean isContainCurrentColumn = false;

        List<Command> data = getCommandInfo(excelFile, sheetIndex, targetColumnData, commandDescriptionColumnOffset,
                maxCommandDescriptionColumnLength, isContainCurrentColumn, false);
        Map<String, String> cache = new HashMap<>();
        for (Command command : data) {
            String value = command.getValue();
            List<String> description = command.getDescription();
            cache.put(value, description.get(0));
        }
        writeObject2File(cache, commandCacheFile);
    }

    /**
     * 加载指令文件
     *
     * @param commandCacheFile 缓存文件
     * @return 缓存 map
     * @throws IOException 配置文件读取出错，会抛出此异常
     * @throws ClassNotFoundException 配置文件数据有问题，会抛出此异常
     */
    public static Map<String, String> loadData(String commandCacheFile) throws IOException, ClassNotFoundException {
        Map<String, String> data = null;
        log.info("开始加载指令缓存文件：{}", commandCacheFile);
        ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(Paths.get(commandCacheFile)));
        data = (Map<String, String>) inputStream.readObject();
        if (log.isDebugEnabled()) {
            log.debug("缓存文件数据：{}", data);
        }
        log.info("指令数量：{}", data.size());
        return data;
    }

    /**
     * 加载指令文件
     *
     * @param commandCacheFile 缓存文件
     * @return 缓存 map
     * @throws IOException 配置文件读取出错，会抛出此异常
     * @throws ClassNotFoundException 配置文件数据有问题，会抛出此异常
     */
    public static Map<String, List<HardwareCommand>> loadRequestCommandData(String commandCacheFile) throws IOException, ClassNotFoundException {
        Map<String, List<HardwareCommand>> data = null;
        log.info("开始加载指令缓存文件：{}", commandCacheFile);
        ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(Paths.get(commandCacheFile)));
        data = (Map<String, List<HardwareCommand>>) inputStream.readObject();
        if (log.isDebugEnabled()) {
            log.debug("缓存文件数据：{}", data);
        }
        log.info("指令数量：{}", data.size());
        return data;
    }

    /**
     * 更新指令缓存数据
     *
     * @param requestCommandConfigFile 请求指令文件
     * @param receiveCommandConfigFile 接收指令文件
     */
    public static void initCache(String requestCommandConfigFile, String receiveCommandConfigFile) {
        try {
//            REQUEST_COMMAND_CACHE.clear();
//            REQUEST_COMMAND_CACHE.putAll(loadData(requestCommandConfigFile));
            REQUEST_COMMAND_CACHE2.clear();
            REQUEST_COMMAND_CACHE2.putAll(loadRequestCommandData(requestCommandConfigFile));
            RECEIVE_COMMAND_CACHE.clear();
            RECEIVE_COMMAND_CACHE.putAll(loadData(receiveCommandConfigFile));
        } catch (Exception e) {
            log.error("加载指令数据异常", e);
        }
    }

    /**
     * 通过解析 excel 文件，生成指令缓存文件
     *
     * @param excelFile excel 文件
     * @param commandCacheFile 缓存文件
     * @throws IOException 解析配置文件出错，会抛出此异常
     */
    public static void generateReceiveCommandCacheFile(String excelFile, String commandCacheFile) throws IOException {
        int sheetIndex = 0;
        String targetColumnData = "反馈指令";
        int commandDescriptionColumnOffset = -3;
        int maxCommandDescriptionColumnLength = 3;
        boolean isContainCurrentColumn = false;

        List<Command> data = getCommandInfo(excelFile, sheetIndex, targetColumnData, commandDescriptionColumnOffset,
                maxCommandDescriptionColumnLength, isContainCurrentColumn, false);
        Map<String, String> cache = new HashMap<>();
        for (Command command : data) {
            String value = command.getValue();
            List<String> description = command.getDescription();
            Collections.reverse(description);
            String commandDescription = String.join("", description);
            cache.put(value, commandDescription);
        }
        writeObject2File(cache, commandCacheFile);
    }

    /**
     * 获取指令信息
     * <p>指令解析是与到空白列会自动终止</p>
     *
     * @param excelFile 指令存放 excel 文件
     * @param sheetIndex 指令所在 sheet 索引，从 0 开始
     * @param targetColumnData 目标列数据
     * @param commandDesColumnOffset 指令描述列相对于目标列的偏移量
     * @param commandDesColumnLength 指令描述最大列数
     * @param isContainCurrentColumn 是否包含目标列数据
     * @return 指令信息
     * @throws IOException 指令数据读取出错时，会抛出此异常
     */
    private static List<Command> getCommandInfo(String excelFile, Integer sheetIndex, String targetColumnData,
                                                Integer commandDesColumnOffset, Integer commandDesColumnLength,
                                                Boolean isContainCurrentColumn, Boolean isReplace) throws IOException {
        List<Command> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(excelFile);
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = cell.toString();
                if (targetColumnData.equals(cellValue)) {
                    int startRowNum = cell.getRowIndex();
                    int startCellNum = cell.getColumnIndex();
                    log.info(targetColumnData + " row: " + startRowNum + ", column: " + startCellNum);
                    for (int i = (startRowNum + 1); i <= sheet.getLastRowNum(); i++) {
                        Row commandRow = sheet.getRow(i);
                        Cell commandColumn = commandRow.getCell(startCellNum);
                        if (!StringUtils.hasText(commandColumn.toString())) {
                            log.warn("遇到空白数据列，退出（row：{}，column：{}）", commandColumn.getRowIndex(),
                                    commandColumn.getColumnIndex());
                            break;
                        }
                        int commandValueColumnIndex = commandColumn.getColumnIndex();
                        String command = getColumnValue(sheet, i, commandValueColumnIndex, i);

                        List<String> commandDescription = getCommandDescription(commandColumn, sheet,
                                commandDesColumnOffset, commandDesColumnLength, isContainCurrentColumn, isReplace);
                        data.add(new Command(command, commandDescription));
                        System.out.println("rowIndex: " + i + " - 指令描述：" + commandDescription + "， 指令值：" + command);
                    }
                    log.info("\n=========================================================================================================");
                }
            }
        }
        workbook.close();
        return data;
    }

    /**
     * 获取反馈指令描述
     * <p>指令描述在 excel 文件中分多列存储，需要分别提取</p>
     *
     * @param commandCell 指令所在 Cell
     * @param sheet 工作簿
     * @return 指令描述集合
     */
    private static List<String> getCommandDescription(Cell commandCell, Sheet sheet, Integer startOffset,
                                                      Integer maxColumnNum, Boolean isContainThis, Boolean isReplace) {
        List<String> commandDes = new ArrayList<>();
        if (isContainThis) {
            commandDes.add(commandCell.toString());
        }
        for (int i = 0; i < maxColumnNum; i++) {
            int columnIndex = commandCell.getColumnIndex() + startOffset;
            String cellValue = getColumnValue(sheet, commandCell.getRowIndex(), columnIndex, 0);
            if (!StringUtils.hasText(cellValue)) {
                break;
            }

            if (isReplace) {
                for (String key : REPLACE_DESCRIPTION.keySet()) {
                    if (cellValue.contains(key)) {
                        String numberStr = cellValue.replaceAll("\\D+", "");
                        if (Integer.valueOf(numberStr).equals(Integer.valueOf(key))) {
                            cellValue = cellValue.replace(key, REPLACE_DESCRIPTION.get(key));
                            break;
                        }
                    }
                }
            }
//            if (StringUtils.hasText(cellValue) & !isDouble(cellValue)) {
//                for (String key : REPLACE_DESCRIPTION.keySet()) {
//                    if (cellValue.contains(key)) {
//                        cellValue = cellValue.replace(key, REPLACE_DESCRIPTION.get(key));
//                        break;
//                    }
//                }
//            }

            // 判读指令名称是否重复，重复则丢弃
            String finalCellValue = cellValue;
            if (!commandDes.stream().anyMatch(e -> e.contains(finalCellValue))) {
                commandDes.add(cellValue);
            }
            startOffset--;
        }
        return commandDes;
    }

    /**
     * 获取指令存入 map 中的 key
     *
     * @param commandList 指令名称集合
     * @return 指令名称集合对应的 key
     */
    private static String getCommandKey(List<String> commandList) {
        List<String> commandKeyList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(commandList)) {
            int commandListSize = commandList.size();
            if (commandListSize == 4) {
                commandKeyList.add(commandList.get(0) + commandList.get(1) + commandList.get(2) + commandList.get(3));
                commandKeyList.add(commandList.get(3) + commandList.get(2) + commandList.get(1) + commandList.get(0));
            } else if (commandListSize == 3) {
                commandKeyList.add(commandList.get(0) + commandList.get(1) + commandList.get(2));
                commandKeyList.add(commandList.get(2) + commandList.get(1) + commandList.get(0));
            } else if (commandListSize == 2) {
                commandKeyList.add(commandList.get(0) + commandList.get(1));
                commandKeyList.add(commandList.get(1) + commandList.get(0));
            }
        }
        return String.join("|", commandKeyList);
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
        String cellValue = cell.toString();
        // 去除小数
        cellValue = cellValue.replaceFirst("\\.\\d*", "");
        if (IGNORE_DESCRIPTION.contains(cellValue)) {
            cellValue = "";
        } else if (!StringUtils.hasText(cellValue) && rowIndex > minRowIndex) {
            cellValue = getColumnValue(sheet, (rowIndex - 1), columnIndex, minRowIndex);
        }
        return cellValue;
    }

    /**
     * 将对象写入文件
     *
     * @param obj 待写入的对象
     * @param file 待写入的文件
     * @throws IOException 将对象写入文件报错时，会抛出此异常
     */
    private static void writeObject2File(Object obj, String file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        outputStream.writeObject(obj);
        outputStream.close();
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str 字符串
     * @return true：是数字，false：不是数字
     */
    private static Boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 指令实体
     */
    @Data
    @AllArgsConstructor
    private static class Command {

        /**
         * 指令值
         */
        String value;

        /**
         * 指令描述
         */
        List<String> description;
    }

    /**
     * 硬件指令实体
     */
    @Data
    @AllArgsConstructor
    private static class HardwareCommand implements Serializable {

        /**
         * 指令值
         */
        String value;

        /**
         * 指令描述
         */
        String description;
    }
}
