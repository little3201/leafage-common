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

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Utility class for reading and mapping Excel files from an InputStream to the specified type.
 * Supports reading from password-protected files and from specific sheets.
 *
 * @param <T> The type to map the Excel rows to
 * @since 0.3.0
 */
public final class ExcelReader<T> {

    private static final Logger log = StatusLogger.getLogger();

    /**
     * Private constructor to prevent instantiation.
     */
    private ExcelReader() {
        // Prevent instantiation
    }

    /**
     * Reads and maps data from the default sheet "sheet1".
     *
     * @param inputStream The input stream of the Excel file
     * @param clazz       The class to map rows to
     * @param <T>         The type of objects to map the Excel data to
     * @return List of mapped objects
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> clazz) {
        return read(inputStream, clazz, null, null);
    }

    /**
     * Reads and maps data from a specified sheet, with optional password protection.
     *
     * @param inputStream The input stream of the Excel file
     * @param clazz       The class to map rows to
     * @param sheetName   (Optional) The name of the sheet to read
     * @param password    (Optional) The password for protected files
     * @param <T>         The type of objects to map the Excel data to
     * @return List of mapped objects
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> clazz, String sheetName, String password) {
        try (Workbook workbook = createWorkbook(inputStream, password)) {
            Sheet sheet = getSheet(workbook, sheetName);
            if (sheet == null) return Collections.emptyList();
            return readSheet(sheet, clazz);
        } catch (IOException e) {
            log.error("Failed to read from input stream.", e);
            return Collections.emptyList();
        }
    }

    /**
     * Creates a Workbook instance from the input stream, with optional password handling.
     *
     * @param inputStream The input stream of the Excel file
     * @param password    The password for protected files, if any
     * @return Workbook instance
     * @throws IOException If an error occurs while reading the input stream
     */
    private static Workbook createWorkbook(InputStream inputStream, String password) throws IOException {
        return StringUtil.isBlank(password)
                ? WorkbookFactory.create(inputStream)
                : WorkbookFactory.create(inputStream, password);
    }

    /**
     * Retrieves the sheet by name, or defaults to the first sheet if no name is provided.
     *
     * @param workbook  The Workbook instance
     * @param sheetName The sheet name, or null to use the default sheet
     * @return The corresponding Sheet object
     */
    private static Sheet getSheet(Workbook workbook, String sheetName) {
        return StringUtil.isBlank(sheetName) ? workbook.getSheetAt(0) : workbook.getSheet(sheetName);
    }

    /**
     * Reads and maps the rows of a sheet to instances of the specified class.
     *
     * @param sheet The sheet to read data from
     * @param clazz The class to map rows to
     * @param <T>   The type of objects to map the Excel data to
     * @return List of mapped objects
     */
    private static <T> List<T> readSheet(Sheet sheet, Class<T> clazz) {
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        if (lastRowNum <= firstRowNum) return Collections.emptyList();

        List<String> headers = readHeader(sheet.getRow(firstRowNum));
        List<T> dataList = new ArrayList<>(lastRowNum - firstRowNum);

        for (int i = firstRowNum + 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            Map<String, Object> rowData = mapRowToHeaders(row, headers);
            dataList.add(convert(rowData, clazz));
        }
        return dataList;
    }

    /**
     * Reads the header row and extracts column names.
     *
     * @param row The header row
     * @return List of column names
     */
    private static List<String> readHeader(Row row) {
        if (row == null) return Collections.emptyList();
        int numCells = row.getLastCellNum();
        List<String> headers = new ArrayList<>(numCells);

        for (int i = 0; i < numCells; i++) {
            headers.add(readCellAsString(row.getCell(i)));
        }
        return headers;
    }

    /**
     * Maps a row's cell values to their corresponding header names.
     *
     * @param row     The row to read
     * @param headers The list of header names
     * @return A map of column names to cell values
     */
    private static Map<String, Object> mapRowToHeaders(Row row, List<String> headers) {
        Map<String, Object> rowData = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            rowData.put(headers.get(i), readCell(row.getCell(i)));
        }
        return rowData;
    }

    /**
     * Converts the row data map into an instance of the specified class.
     *
     * @param dataMap The map of column names to values
     * @param clazz   The class to instantiate
     * @param <T>     The type of object to create
     * @return An instance of the class populated with the row data
     */
    private static <T> T convert(Map<String, Object> dataMap, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                PropertyDescriptor descriptor = new PropertyDescriptor(entry.getKey(), clazz);
                descriptor.getWriteMethod().invoke(instance, entry.getValue());
            }
            return instance;
        } catch (Exception e) {
            log.error("Failed to convert row data to object.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a cell's value as an Object.
     *
     * @param cell The cell to read
     * @return The cell's value as an Object
     */
    private static Object readCell(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCellFormula();
            case ERROR -> cell.getErrorCellValue();
            default -> null;
        };
    }

    /**
     * Reads a cell's value as a String.
     *
     * @param cell The cell to read
     * @return The cell's value as a String
     */
    private static String readCellAsString(Cell cell) {
        return cell == null ? "" : cell.toString();
    }
}
