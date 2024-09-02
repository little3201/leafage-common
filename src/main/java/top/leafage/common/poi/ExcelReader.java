/*
 *  Copyright 2018-2024 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package top.leafage.common.poi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Read and parse given inputStream, and convert to a specified type.
 *
 * @author liwenqiang 2021/8/26 9:37
 * @since 0.3.0
 */
public final class ExcelReader {

    private static final Logger log = StatusLogger.getLogger();

    /**
     * Read from given inputStream.
     *
     * @param clazz {@link Class}
     * @param <T>   Instance type
     * @return the read data
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> clazz) {
        assert inputStream != null;

        return read(inputStream, clazz, null);
    }


    /**
     * Read from given inputStream.
     *
     * @param clazz     {@link Class}
     * @param <T>       Instance type
     * @param sheetName sheet name
     * @return the read data
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> clazz, String sheetName) {
        assert inputStream != null;

        return read(inputStream, null, clazz, sheetName);
    }

    /**
     * Read from given inputStream, with password.
     *
     * @param inputStream {@link InputStream}
     * @param password    password
     * @param clazz       {@link Class}
     * @return the read data
     */
    public static <T> List<T> read(InputStream inputStream, String password, Class<T> clazz) {
        assert inputStream != null;

        return read(inputStream, password, clazz, null);
    }

    /**
     * Read from given inputStream, with password.
     *
     * @param inputStream {@link InputStream}
     * @param password    password
     * @param clazz       {@link Class}
     * @param sheetName   sheet name
     * @return the read data
     */
    public static <T> List<T> read(InputStream inputStream, String password, Class<T> clazz, String sheetName) {
        assert inputStream != null;

        try (Workbook workbook = createWorkbook(inputStream, password)) {
//            // Read data from Excel
            Sheet sheet = getSheet(workbook, sheetName);
            if (sheet == null) {
                return Collections.emptyList();
            }
            return readSheet(sheet, clazz);
        } catch (IOException e) {
            log.error("Read from inputStream error!", e);
            return Collections.emptyList();
        }
    }

    /**
     * Creates the appropriate HSSFWorkbook / XSSFWorkbook from given InputStream,
     * which may be password protected.
     *
     * @param inputStream {@link InputStream}
     * @param password    password
     * @return workbook {@link Workbook}
     */
    private static Workbook createWorkbook(InputStream inputStream, String password) {
        Workbook workbook = null;
        try {
            if (StringUtil.isBlank(password)) {
                workbook = WorkbookFactory.create(inputStream);
            } else {
                workbook = WorkbookFactory.create(inputStream, password);
            }
        } catch (IOException e) {
            log.error("Create workbook error: {}", e.getMessage(), e);
        }
        return workbook;
    }

    /**
     * Get sheet with the given name.
     * The sheet name is "sheet1" when given sheet name is blank.
     *
     * @param workbook {@link Workbook}
     * @param name     Sheet name
     * @return sheet {@link Sheet}
     */
    private static Sheet getSheet(final Workbook workbook, String name) {
        if (StringUtil.isBlank(name)) {
            name = "sheet1";
        }
        return workbook.getSheet(name);
    }

    /**
     * Read sheet
     *
     * @param sheet {@link Sheet}
     * @param clazz {@link Class}
     * @param <T>   the type of target
     * @return the read data
     */
    private static <T> List<T> readSheet(Sheet sheet, Class<T> clazz) {
        // 边界判断
        final int firstRowNum = sheet.getFirstRowNum();
        final int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum <= firstRowNum) {
            return Collections.emptyList();
        }

        // read header
        Row headerRow = sheet.getRow(firstRowNum);
        List<String> headerList = readHeader(headerRow);

        // put in map like (index, header)
        Map<Integer, String> headerMap = new HashMap<>(headerList.size());
        for (int i = 0; i < headerList.size(); i++) {
            String header = headerList.get(i);
            headerMap.put(i, header);
        }

        List<T> dataList = new ArrayList<>(lastRowNum - firstRowNum + 1);
        for (int i = firstRowNum; i <= lastRowNum; i++) {
            if (i != firstRowNum) {
                // put in map like (index, cell)
                Map<Integer, Object> cellValueMap = readRow(sheet.getRow(i));
                T t = mapping(headerMap, cellValueMap, clazz);
                dataList.add(t);
            }
        }

        return dataList;
    }

    /**
     * Read header
     *
     * @param row {@link Row}
     * @return the header
     */
    private static List<String> readHeader(Row row) {
        if (row == null) {
            return Collections.emptyList();
        }
        short cellSize = row.getLastCellNum();
        List<String> headerList = new ArrayList<>(cellSize);

        Cell cell;
        for (int cellNum = 0; cellNum < cellSize; cellNum++) {
            cell = row.getCell(cellNum);
            Object cellValue = readCell(cell);
            headerList.add(cellValue.toString());
        }
        return headerList;
    }

    /**
     * Read row data
     *
     * @param row {@link Row}
     * @return the row data
     */
    private static Map<Integer, Object> readRow(Row row) {
        if (row == null) {
            return Collections.emptyMap();
        }
        short cellSize = row.getLastCellNum();
        // store in map with like (index, cell)
        Map<Integer, Object> cellValueMap = new HashMap<>(cellSize);
        Cell cell;
        for (int i = 0; i < cellSize; i++) {
            cell = row.getCell(i);
            Object cellValue = readCell(cell);
            cellValueMap.put(i, cellValue);
        }
        return cellValueMap;
    }

    /**
     * Read cell
     *
     * @param cell {@link Cell}
     * @return value
     */
    private static Object readCell(Cell cell) {
        return switch (cell.getCellType()) {
            case BLANK -> // blank
                    "";
            case ERROR -> // error
                    cell.getErrorCellValue();
            case NUMERIC -> // numeric
                    cell.getNumericCellValue();
            case STRING -> // string
                    cell.getStringCellValue();
            case BOOLEAN -> // boolean
                    cell.getBooleanCellValue();
            case FORMULA -> // formula
                    cell.getCellFormula();
            default -> cell.getStringCellValue();
        };
    }

    /**
     * Mapping cell to header and convert to given type instance.
     *
     * @param headerMap    header
     * @param cellValueMap cell
     * @param clazz        {@link Class}
     * @return Data object
     */
    private static <T> T mapping(Map<Integer, String> headerMap, Map<Integer, Object> cellValueMap, Class<T> clazz) {
        Map<String, Object> valueMap = new HashMap<>(headerMap.size());
        for (Map.Entry<Integer, String> next : headerMap.entrySet()) {
            Object object = cellValueMap.get(next.getKey());
            valueMap.put(next.getValue(), object);
        }
        return convert(valueMap, clazz);
    }


    /**
     * Map row data to an object.
     *
     * @param valueMap cell values
     * @param clazz    {@link Class}
     * @return Data object
     */
    private static <T> T convert(Map<String, Object> valueMap, Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new UnsupportedOperationException("Target object is an interface, cannot execute!");
        }
        T t;
        try {
            t = clazz.getDeclaredConstructor().newInstance();

            // write value to field
            PropertyDescriptor descriptor;
            for (Map.Entry<String, Object> next : valueMap.entrySet()) {
                descriptor = new PropertyDescriptor(next.getKey(), clazz);
                descriptor.getWriteMethod().invoke(t, next.getValue());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | IntrospectionException | SecurityException e) {
            log.error("write value to field error!", e);
            return null;
        }
        return t;
    }
}
