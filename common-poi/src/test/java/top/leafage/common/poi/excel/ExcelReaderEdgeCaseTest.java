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

package top.leafage.common.poi.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcelReaderEdgeCaseTest {

    @Test
    void readsNamedSheetAndKeepsDefaultForBlankPrimitiveCell() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            workbook.createSheet("ignored");
            var sheet = workbook.createSheet("users");
            sheet.createRow(0).createCell(0).setCellValue("name");
            sheet.getRow(0).createCell(1).setCellValue("age");
            sheet.createRow(1).createCell(0).setCellValue("alice");
            workbook.write(output);

            List<User> users = ExcelReader.read(new ByteArrayInputStream(output.toByteArray()), User.class, "users");

            assertEquals(1, users.size());
            assertEquals("alice", users.getFirst().name);
            assertEquals(0, users.getFirst().age);
            assertTrue(ExcelReader.read(new ByteArrayInputStream(output.toByteArray()), User.class, "missing").isEmpty());
        }
    }

    @Test
    void returnsEmptyListForWorkbookWithoutSheetsAndValidatesArguments() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            workbook.write(output);

            assertTrue(ExcelReader.read(new ByteArrayInputStream(output.toByteArray()), User.class).isEmpty());
        }

        assertThrows(NullPointerException.class, () -> ExcelReader.read(null, User.class));
        assertThrows(NullPointerException.class,
                () -> ExcelReader.read(new ByteArrayInputStream(new byte[0]), null));
    }

    @Test
    void exposesExcelColumnAtRuntime() throws NoSuchFieldException {
        ExcelColumn column = AnnotatedUser.class.getDeclaredField("name").getAnnotation(ExcelColumn.class);

        assertEquals("display name", column.value());
    }

    static class User {
        private String name;
        private int age;

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    static class AnnotatedUser {
        @ExcelColumn("display name")
        private String name;
    }
}
