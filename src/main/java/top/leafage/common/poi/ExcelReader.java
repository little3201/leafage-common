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
 * Utility to read and parse Excel files from InputStream and map to specified types.
 * Supports reading from password-protected files and specific sheets.
 *
 * @param <T> The target class type for data mapping
 * @author wq lis
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
     * Reads data from the given InputStream, assuming the default sheet "sheet1".
     *
     * @param inputStream The input stream of the Excel file
     * @param clazz       The target class for mapping rows
     * @param <T>         the type of objects to be created from the Excel data
     * @return List of mapped objects
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> clazz) {
        return read(inputStream, clazz, null, null);
    }

    /**
     * Reads data from the given InputStream with an optional sheet name and password.
     *
     * @param inputStream The input stream of the Excel file
     * @param clazz       The target class for mapping rows
     * @param sheetName   (Optional) The name of the sheet to read
     * @param password    (Optional) The password for a protected file
     * @param <T>         the type of objects to be created from the Excel data
     * @return List of mapped objects
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> clazz, String sheetName, String password) {
        try (Workbook workbook = createWorkbook(inputStream, password)) {
            Sheet sheet = getSheet(workbook, sheetName);
            if (sheet == null) return Collections.emptyList();
            return readSheet(sheet, clazz);
        } catch (IOException e) {
            log.error("Error reading from input stream.", e);
            return Collections.emptyList();
        }
    }

    /**
     * Creates an appropriate Workbook instance based on the input stream and password.
     *
     * @param inputStream Input stream of the Excel file
     * @param password    Optional password for protected files
     * @return Workbook instance
     */
    private static Workbook createWorkbook(InputStream inputStream, String password) throws IOException {
        return StringUtil.isBlank(password)
                ? WorkbookFactory.create(inputStream)
                : WorkbookFactory.create(inputStream, password);
    }

    /**
     * Fetches the sheet with the specified name or defaults to "sheet1" if the name is null or empty.
     *
     * @param workbook  Workbook instance
     * @param sheetName Name of the sheet to fetch
     * @return The corresponding Sheet object
     */
    private static Sheet getSheet(Workbook workbook, String sheetName) {
        return StringUtil.isBlank(sheetName) ? workbook.getSheetAt(0) : workbook.getSheet(sheetName);
    }

    /**
     * Reads data from a specified sheet and maps each row to an instance of the specified class.
     *
     * @param sheet The sheet to read data from
     * @param clazz The class type to map each row to
     * @param <T>   the type of objects to be created from the Excel data
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
     * Reads the header row and returns a list of column names.
     *
     * @param row The header row
     * @return List of header names
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
     * Maps the row data to a map keyed by column names.
     *
     * @param row     The row to read
     * @param headers The header names
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
     * Converts the map of row data to an instance of the specified class.
     *
     * @param dataMap A map of column names to cell values
     * @param clazz   The class type to instantiate
     * @param <T>     the type of objects to be created from the Excel data
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
            log.error("Error converting row data to object.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the value of a cell as an Object.
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
     * Reads the value of a cell as a String.
     *
     * @param cell The cell to read
     * @return The cell's value as a String
     */
    private static String readCellAsString(Cell cell) {
        return cell == null ? "" : cell.toString();
    }
}
