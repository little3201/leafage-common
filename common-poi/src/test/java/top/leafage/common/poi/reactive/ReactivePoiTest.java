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

package top.leafage.common.poi.reactive;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.poi.excel.reactive.ReactiveExcelReader;
import top.leafage.common.poi.word.element.Paragraph;
import top.leafage.common.poi.word.reactive.ReactiveWordWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReactivePoiTest {

    @Test
    void readsExcelFromFilePart() throws IOException {
        byte[] bytes = workbookBytes();

        List<User> users = ReactiveExcelReader.read(filePart(bytes), User.class).block();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("alice", users.getFirst().name);
        assertEquals(1, ReactiveExcelReader.read(filePart(bytes), User.class, "sheet1").block().size());
        assertEquals(1, ReactiveExcelReader.read(filePart(bytes), User.class, "sheet1", null).block().size());
        assertThrows(NullPointerException.class, () -> ReactiveExcelReader.read(null, User.class));
        assertThrows(NullPointerException.class, () -> ReactiveExcelReader.read(filePart(bytes), null));
    }

    @Test
    void writesWordOnReactivePipeline() {
        byte[] bytes = ReactiveWordWriter.write(List.of(new Paragraph("Body", null))).block();

        assertNotNull(bytes);
        assertThrows(NullPointerException.class, () -> ReactiveWordWriter.write(null).block());
    }

    private static byte[] workbookBytes() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("sheet1");
            sheet.createRow(0).createCell(0).setCellValue("name");
            sheet.createRow(1).createCell(0).setCellValue("alice");
            workbook.write(output);
            return output.toByteArray();
        }
    }

    private static FilePart filePart(byte[] bytes) {
        return new FilePart() {
            @Override
            public String filename() {
                return "users.xlsx";
            }

            @Override
            public Mono<Void> transferTo(Path destination) {
                return Mono.empty();
            }

            @Override
            public String name() {
                return "file";
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.EMPTY;
            }

            @Override
            public Flux<DataBuffer> content() {
                return Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(bytes));
            }
        };
    }

    public static class User {
        private String name;

        public void setName(String name) {
            this.name = name;
        }
    }
}
