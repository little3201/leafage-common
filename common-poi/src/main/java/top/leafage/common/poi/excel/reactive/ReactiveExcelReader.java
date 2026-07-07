/*
 * Copyright(c) 2019-present the original author or authors.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.common.poi.excel.reactive;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import top.leafage.common.poi.excel.ExcelReader;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Utility class for reading and mapping Excel files from an InputStream to the specified type.
 * Supports reading from password-protected files and from specific sheets.
 *
 * @param <T> The type to map the Excel rows to
 * @author wq li
 */
public class ReactiveExcelReader<T> extends ExcelReader<T> {

    /**
     * Private constructor to prevent instantiation.
     */
    private ReactiveExcelReader() {
        super();
    }

    /**
     * Read from FilePart.
     * Reads and maps data from the default sheet "sheet1".
     *
     * @param filePart a {@link FilePart} object
     * @param clazz    a {@link Class} object
     * @param <T>      The type of target
     * @return a type of {@link Mono} T
     */
    public static <T> Mono<List<T>> read(FilePart filePart, Class<T> clazz) {
        return read(filePart, clazz, null);
    }

    /**
     * Read from FilePart with given sheet name.
     * Reads and maps data from the default sheet "sheet1".
     *
     * @param filePart  a {@link FilePart} object
     * @param clazz     a {@link Class} object
     * @param sheetName a {@link String} object
     * @param <T>       The type of target
     * @return a type of {@link Mono}
     */
    public static <T> Mono<List<T>> read(FilePart filePart, Class<T> clazz, String sheetName) {
        return read(filePart, clazz, sheetName, null);
    }

    /**
     * Read from FilePart with given sheet name and password.
     * Reads and maps data from the default sheet "sheet1".
     *
     * @param filePart  a {@link FilePart} object
     * @param clazz     a {@link Class} object
     * @param sheetName a {@link String} object
     * @param password  a {@link String} object
     * @param <T>       The type of target
     * @return a type of {@link Mono}
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
