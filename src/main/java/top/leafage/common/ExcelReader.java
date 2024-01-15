/*
 *  Copyright 2018-2024 the original author or authors.
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

package top.leafage.common;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
 * Reads an Excel file and parses it into objects of a specified type.
 *
 * @author liwenqiang 2021/8/26 9:37
 * @since 0.1.4
 */
public class ExcelReader {

    private static final Logger log = StatusLogger.getLogger();

    private static final String XLS = ".xls";
    private static final String XLSX = ".xlsx";

    private ExcelReader() {
    }

    /**
     * Gets the corresponding workbook object based on the file extension type.
     *
     * @param inputStream InputStream for reading the file
     * @param type        File extension type (xls or xlsx)
     * @return Workbook object containing file data
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
            log.error("Parsing exception, file type: {}", type, e);
        }
        return workbook;
    }

    /**
     * Reads an Excel file.
     *
     * @param file  File
     * @param clazz Class
     * @param <T>   Instance type
     * @return List of read results; returns an empty list on failure
     */
    public static <T> List<T> read(File file, Class<T> clazz) {
        String filename = file.getName();
        String type = filename.substring(filename.lastIndexOf("."));

        try (FileInputStream inputStream = new FileInputStream(file);
             Workbook workbook = getWorkbook(inputStream, type)
        ) {
            // Read data from Excel
            return parse(workbook, clazz);
        } catch (NullPointerException | SecurityException | IOException e) {
            log.error("File: {} read error!", filename, e);
            return Collections.emptyList();
        }
    }

    /**
     * Parses Excel workbook data.
     *
     * @param workbook Excel Workbook object
     * @param clazz    Class
     * @param <T>      Instance type
     * @return Parsing results
     */
    private static <T> List<T> parse(Workbook workbook, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        // 解析sheet
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);

            // Check if the sheet is valid
            if (sheet == null) {
                log.warn("Sheet is not exist!");
                continue;
            }

            // Get the header
            Row firstRow = sheet.getRow(sheet.getFirstRowNum());
            if (null == firstRow) {
                throw new NoSuchElementException("Header information not found!");
            }

            // Parse data for each row and construct data objects
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
     * Maps row data to an object.
     *
     * @param row   Row data
     * @param clazz Class
     * @param <T>   Instance type
     * @return Data object
     */
    private static <T> T mapping(Row row, Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new UnsupportedOperationException("Target object is an interface, cannot execute!");
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
            log.error("Object mapping error!", e);
            return null;
        }
        return t;
    }

    /**
     * Writes values to properties.
     *
     * @param t          Instance
     * @param cell       Column data
     * @param descriptor Property descriptor
     * @param <T>        Instance type
     */
    private static <T> void writeData(T t, Cell cell, PropertyDescriptor descriptor) {
        try {
            switch (cell.getCellType()) {
                case BLANK: // blank
                case _NONE: // null
                    break;
                case NUMERIC: // numeric
                    descriptor.getWriteMethod().invoke(t, cell.getNumericCellValue());
                    break;
                case STRING: // string
                    descriptor.getWriteMethod().invoke(t, cell.getStringCellValue());
                    break;
                case BOOLEAN: // boolean
                    descriptor.getWriteMethod().invoke(t, cell.getBooleanCellValue());
                    break;
                case FORMULA: // formula
                    descriptor.getWriteMethod().invoke(t, cell.getCellFormula());
                    break;
                default:
                    descriptor.getWriteMethod().invoke(t, cell.getErrorCellValue());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Exception while setting data:", e);
        }
    }

}
