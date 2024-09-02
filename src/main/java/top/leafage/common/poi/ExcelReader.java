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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Read and parse given excel file，inputStream, and convert to a specified type.
 *
 * @author liwenqiang 2021/8/26 9:37
 * @since 0.1.4
 */
public final class ExcelReader {

    private static final Logger log = StatusLogger.getLogger();

    /**
     * Creates the appropriate HSSFWorkbook / XSSFWorkbook from given InputStream.
     *
     * @param inputStream {@link InputStream}
     * @return workbook {@link Workbook}
     */
    private static Workbook createWorkbook(InputStream inputStream) {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            log.error("Create workbook error: {}", e.getMessage(), e);
        }
        return workbook;
    }

    /**
     * Creates the appropriate HSSFWorkbook / XSSFWorkbook from given file.
     *
     * @param file {@link File}
     * @return workbook {@link Workbook}
     */
    private static Workbook createWorkbook(File file) {
        assert file != null;

        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(file);
        } catch (IOException e) {
            log.error("Create workbook error: {}", e.getMessage(), e);
        }
        return workbook;
    }

    /**
     * Creates the appropriate HSSFWorkbook / XSSFWorkbook from given file.
     *
     * @param file     {@link File}
     * @param password password
     * @return workbook {@link Workbook}
     */
    private static Workbook createWorkbook(File file, String password) {
        assert file != null;

        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(file, password);
        } catch (IOException e) {
            log.error("Create workbook error: {}", e.getMessage(), e);
        }
        return workbook;
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
        assert inputStream != null;

        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream, password);
        } catch (IOException e) {
            log.error("Create workbook error: {}", e.getMessage(), e);
        }
        return workbook;
    }

    /**
     * Get sheet.
     * The default sheet name is "sheet1".
     *
     * @param workbook {@link Workbook}
     * @return sheet {@link Sheet}
     */
    private static Sheet getSheet(final Workbook workbook) {
        assert workbook != null;

        return workbook.getSheet("sheet1");
    }

    /**
     * Get sheet with the given name.
     * The sheet name is "sheet1" when given sheet name is blank.
     *
     * @param workbook {@link Workbook}
     * @param sheetName Sheet name
     * @return sheet {@link Sheet}
     */
    private static Sheet getSheet(final Workbook workbook, String sheetName) {
        assert workbook != null;

        if (StringUtil.isBlank(sheetName)) {
            sheetName = "sheet1";
        }
        return workbook.getSheet(sheetName);
    }

    /**
     * Read from given inputStream.
     *
     * @param clazz {@link Class}
     * @param <T>   Instance type
     * @return the read data
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> clazz) {
        assert inputStream != null;

        try {
            Workbook workbook = createWorkbook(inputStream);
            // Read data from Excel
            Sheet sheet = getSheet(workbook);
            if (sheet == null) {
                workbook.close();
                return Collections.emptyList();
            }
            return read(sheet, clazz);
        } catch (NullPointerException | SecurityException | IOException e) {
            log.error("Read from inputStream error!", e);
            return Collections.emptyList();
        }
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

        try {
            Workbook workbook = createWorkbook(inputStream, password);
            // Read data from Excel
            Sheet sheet = getSheet(workbook);
            if (sheet == null) {
                workbook.close();
                return Collections.emptyList();
            }
            return read(sheet, clazz);
        } catch (NullPointerException | SecurityException | IOException e) {
            log.error("Read from inputStream error!", e);
            return Collections.emptyList();
        }
    }

    /**
     * Read from excel file.
     *
     * @param file  {@link File}
     * @param clazz {@link Class}
     * @return the read data
     */
    public static <T> List<T> read(File file, Class<T> clazz) {
        assert file != null;

        try {
            // create workbook from the given file
            Workbook workbook = createWorkbook(file);
            // Get sheet fro workbook
            Sheet sheet = getSheet(workbook);
            if (sheet == null) {
                workbook.close();
                return Collections.emptyList();
            }
            // read sheet
            return read(sheet, clazz);
        } catch (NullPointerException | SecurityException | IOException e) {
            log.error("Read from file error!", e);
            return Collections.emptyList();
        }
    }


    /**
     * Read from given excel file, with password.
     *
     * @param file     {@link File}
     * @param password file password
     * @param clazz    {@link Class}
     * @return the read data
     */
    public static <T> List<T> read(File file, String password, Class<T> clazz) {
        assert file != null;

        try {
            // create workbook from the given file
            Workbook workbook = createWorkbook(file, password);
            // Get sheet fro workbook
            Sheet sheet = getSheet(workbook);
            if (sheet == null) {
                workbook.close();
                return Collections.emptyList();
            }
            // read sheet
            return read(sheet, clazz);
        } catch (NullPointerException | SecurityException | IOException e) {
            log.error("Read from file error!", e);
            return Collections.emptyList();
        }
    }

    /**
     * Read sheet
     *
     * @param sheet {@link Sheet}
     * @param clazz {@link Class}
     * @param <T>   the type of target
     * @return the read data
     */
    private static <T> List<T> read(Sheet sheet, Class<T> clazz) {
        // 边界判断
        final int firstRowNum = sheet.getFirstRowNum();
        final int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum <= firstRowNum) {
            return Collections.emptyList();
        }

        // read header
//        Row headerRow = sheet.getRow(firstRowNum);
//        List<String> headerValues = readHeader(headerRow);

        // read data
        final List<T> result = new ArrayList<>(lastRowNum - firstRowNum + 1);
        List<Object> cellValues;
        for (int i = firstRowNum; i <= lastRowNum; i++) {
            if (i != firstRowNum) {
                cellValues = readRow(sheet.getRow(i));
                if (CollectionUtils.isNotEmpty(cellValues)) {
                    result.add(mapping(cellValues, clazz));
                }
            }
        }
        return result;
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
        List<String> cellValues = new ArrayList<>(cellSize);

        Cell cell;
        for (int cellNum = 0; cellNum < cellSize; cellNum++) {
            cell = row.getCell(cellNum);
            Object cellValue = readCell(cell);
            cellValues.add(cellValue.toString());
        }
        return cellValues;
    }

    /**
     * Read row data
     *
     * @param row {@link Row}
     * @return the row data
     */
    private static List<Object> readRow(Row row) {
        if (row == null) {
            return Collections.emptyList();
        }
        short cellSize = row.getLastCellNum();
        List<Object> cellValues = new ArrayList<>(cellSize);

        Cell cell;
        for (int cellNum = 0; cellNum < cellSize; cellNum++) {
            cell = row.getCell(cellNum);
            Object cellValue = readCell(cell);
            cellValues.add(cellValue);
        }
        return cellValues;
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
     * Map row data to an object.
     *
     * @param cellValues cell values
     * @param clazz      {@link Class}
     * @return Data object
     */
    private static <T> T mapping(List<Object> cellValues, Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new UnsupportedOperationException("Target object is an interface, cannot execute!");
        }
        T t;
        try {
            t = clazz.getDeclaredConstructor().newInstance();

            Field[] fields = clazz.getDeclaredFields();

            // set value to object
            for (int i = 0; i <= cellValues.size(); i++) {
                PropertyDescriptor descriptor = new PropertyDescriptor(fields[i].getName(), clazz);
                descriptor.getWriteMethod().invoke(t, cellValues.get(i));
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 IntrospectionException e) {
            log.error("Object mapping error!", e);
            return null;
        }
        return t;
    }

}
