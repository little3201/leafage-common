/*
 * Copyright (c) 2025.  little3201.
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

package top.leafage.common.poi;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExcelReaderTest {

    @Test
    void read() throws Exception {
        // 1. 创建一个内存中的 Excel 文件
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        // 2. 添加表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("姓名");
        header.createCell(1).setCellValue("年龄");
        header.createCell(2).setCellValue("邮箱");

        // 3. 添加数据行
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("张三");
        row1.createCell(1).setCellValue(28);
        row1.createCell(2).setCellValue("zhangsan@example.com");

        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("李四");
        row2.createCell(1).setCellValue(32);
        row2.createCell(2).setCellValue("lisi@example.com");

        // 4. 写入 ByteArrayOutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());

        // 5. 调用 ExcelReader 进行读取
        List<User> users = ExcelReader.read(input, User.class);

        // 6. 验证读取结果
        assertNotNull(users);
        assertEquals(2, users.size());

        User user1 = users.get(0);
        assertEquals("张三", user1.getName());
        assertEquals(28, user1.getAge());
        assertEquals("zhangsan@example.com", user1.getEmail());

        User user2 = users.get(1);
        assertEquals("李四", user2.getName());
        assertEquals(32, user2.getAge());
        assertEquals("lisi@example.com", user2.getEmail());
    }

    static class User {

        @ExcelColumn("姓名")
        private String name;

        @ExcelColumn("年龄")
        private int age;

        @ExcelColumn("邮箱")
        private String email;

        // Getters & Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}