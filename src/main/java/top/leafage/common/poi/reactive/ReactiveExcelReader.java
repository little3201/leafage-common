/*
 *  Copyright 2018-2025 little3201.
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

package top.leafage.common.poi.reactive;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import top.leafage.common.poi.ExcelReader;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Utility class for reading and mapping Excel files from an InputStream to the specified type.
 * Supports reading from password-protected files and from specific sheets.
 *
 * @param <T> The type to map the Excel rows to
 * @author wq li
 * @since 0.3.7
 */
public class ReactiveExcelReader<T> extends ExcelReader<T> {

    /**
     * Private constructor to prevent instantiation.
     */
    private ReactiveExcelReader() {
        super();
        // Prevent instantiation
    }

    /**
     * Reads and maps data from the default sheet "sheet1".
     *
     * @param filePart {@code FilePart}.
     * @param clazz    The class to map rows to.
     * @param <T>      The type of objects to map the Excel data to.
     * @return List of mapped objects.
     * @since 0.3.7
     */
    public static <T> Mono<List<T>> read(FilePart filePart, Class<T> clazz) {
        return read(filePart, clazz, null);
    }

    /**
     * Reads and maps data from the default sheet "sheet1".
     *
     * @param filePart  {@code FilePart}.
     * @param clazz     The class to map rows to.
     * @param sheetName (Optional) The name of the sheet to read.
     * @param <T>       The type of objects to map the Excel data to.
     * @return List of mapped objects.
     * @since 0.3.7
     */
    public static <T> Mono<List<T>> read(FilePart filePart, Class<T> clazz, String sheetName) {
        return read(filePart, clazz, sheetName, null);
    }

    /**
     * Reads and maps data from the default sheet "sheet1".
     *
     * @param filePart  {@code FilePart}.
     * @param clazz     The class to map rows to.
     * @param sheetName (Optional) The name of the sheet to read.
     * @param password  (Optional) The password for protected files.
     * @param <T>       The type of objects to map the Excel data to.
     * @return List of mapped objects.
     * @since 0.3.7
     */
    public static <T> Mono<List<T>> read(FilePart filePart, Class<T> clazz, String sheetName, String password) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        return read(new ByteArrayInputStream(bytes), clazz, sheetName, password);
                    } finally {
                        DataBufferUtils.release(dataBuffer);
                    }
                });
    }
}
