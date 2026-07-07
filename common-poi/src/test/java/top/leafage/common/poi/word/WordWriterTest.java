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

package top.leafage.common.poi.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import top.leafage.common.poi.word.element.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordWriterTest {

    @Test
    void write() {
        // 1. 构建文档元素
        List<WordElement> elements = new ArrayList<>();
        elements.add(new Heading(1, "接口文档"));
        elements.add(new Paragraph("这是测试段落", null));

        Table table = new Table(
                List.of("字段", "类型", "说明"),
                List.of(List.of("id", "Long", "唯一标识"),
                        List.of("name", "String", "名称"))
        );
        elements.add(table);

        // 使用一张示例图片（1x1像素 PNG）
        byte[] imgBytes = new byte[]{
                (byte) 137, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
                0, 0, 0, 1, 0, 0, 0, 1, 8, 6, 0, 0, 0, 31, (byte) 21, (byte) 196, (byte) 137,
                0, 0, 0, 12, 73, 68, 65, 84, 8, (byte) 153, 99, 0, 1, 0, 0, 5, 0, 1,
                (byte) 0x1D, (byte) 0x0A, (byte) 0x2D, (byte) 0xB4, 0, 0, 0, 0, 73, 69, 78, 68, (byte) 174, 66, 96, (byte) 130
        };
        elements.add(new Image(imgBytes, "1x1.png", 50, 50));

        // 2. 渲染文档
        byte[] bytes = WordWriter.write(elements);

        // 3. 基本断言
        assertNotNull(bytes, "生成的 byte[] 不应为 null");
        assertTrue(bytes.length > 0, "生成的 Word 文档应非空");

        // 4. 尝试用 POI 打开，验证格式正确
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            assertEquals(4, doc.getBodyElements().size(), "文档元素数量应为 4");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}