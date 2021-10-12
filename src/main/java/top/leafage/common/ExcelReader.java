package top.leafage.common;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 读取excel文件，并解析为指定类型的对象
 *
 * @author liwenqiang 2021/8/26 9:37
 */
public class ExcelReader {

    private ExcelReader() {
    }

    private static final Logger log = LoggerFactory.getLogger(ExcelReader.class);

    private static final String XLS = ".xls";
    private static final String XLSX = ".xlsx";

    /**
     * 根据文件后缀名类型获取对应的工作簿对象
     *
     * @param inputStream 读取文件的输入流
     * @param type        文件后缀名类型（xls或xlsx）
     * @return 包含文件数据的工作簿对象
     */
    private static Workbook getWorkbook(InputStream inputStream, String type) {
        Workbook workbook = null;
        try {
            if (type.equalsIgnoreCase(XLS)) {

                workbook = new HSSFWorkbook(inputStream);
            } else if (type.equalsIgnoreCase(XLSX)) {
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            log.error("解析异常，文件类型：{}", type, e);
        }
        return workbook;
    }

    /**
     * 读取
     *
     * @param path  文件路径
     * @param clazz 实体类型
     * @return 读取结果列表，失败时返回 null
     */
    public static <T> List<T> read(String path, Class<T> clazz) {
        // 获取Excel文件
        File file = new File(path);
        if (!file.exists()) {
            log.warn("文件不存在！");
            return Collections.emptyList();
        }
        String type = path.substring(path.lastIndexOf("."));

        try (FileInputStream inputStream = new FileInputStream(file);
             Workbook workbook = getWorkbook(inputStream, type)
        ) {
            // 读取excel中的数据
            return parse(workbook, clazz);
        } catch (NullPointerException | SecurityException | IOException e) {
            log.error("文件：{} 读取异常！", path, e);
            return Collections.emptyList();
        }
    }

    /**
     * 解析
     *
     * @param workbook Excel工作簿对象
     * @param clazz    实体类型
     * @return 解析结果
     */
    private static <T> List<T> parse(Workbook workbook, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        // 解析sheet
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);

            // 校验sheet是否合法
            if (sheet == null) {
                continue;
            }

            // 获取表头
            Row firstRow = sheet.getRow(sheet.getFirstRowNum());
            if (null == firstRow) {
                throw new NoSuchElementException("未读取到表头信息！");
            }

            // 解析每一行的数据，构造数据对象
            for (int rowNum = 1; rowNum < sheet.getPhysicalNumberOfRows(); rowNum++) {
                Row row = sheet.getRow(rowNum);

                if (null == row) {
                    continue;
                }

                list.add(mapping(row, clazz));
            }
        }

        return list;
    }

    /**
     * 映射
     *
     * @param row   行数据
     * @param clazz 实体类型
     * @return 数据对象
     */
    private static <T> T mapping(Row row, Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new UnsupportedOperationException("目标对象为接口，无法进行！");
        }
        T t;
        try {
            t = clazz.getDeclaredConstructor().newInstance();
            int cells = row.getPhysicalNumberOfCells();

            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i <= cells; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    continue;
                }
                PropertyDescriptor descriptor = new PropertyDescriptor(fields[i].getName(), clazz);
                writeData(t, cell, descriptor);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                IntrospectionException e) {
            log.error("对象映射异常!", e);
            return null;
        }
        return t;
    }

    /**
     * 填值
     *
     * @param t          实例
     * @param cell       列数据
     * @param descriptor 操作属性
     */
    private static <T> void writeData(T t, Cell cell, PropertyDescriptor descriptor) {
        try {
            switch (cell.getCellType()) {
                case NUMERIC: // 数字
                    descriptor.getWriteMethod().invoke(t, (long) cell.getNumericCellValue());
                    break;
                case STRING: // 字符串
                    descriptor.getWriteMethod().invoke(t, cell.getStringCellValue());
                    break;
                case BOOLEAN: // Boolean
                    descriptor.getWriteMethod().invoke(t, cell.getBooleanCellValue());
                    break;
                case FORMULA: // 公式
                    descriptor.getWriteMethod().invoke(t, cell.getCellFormula());
                    break;
                default:
                    descriptor.getWriteMethod().invoke(t, cell.getErrorCellValue());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("设置数据时发生异常：", e);
        }
    }

}