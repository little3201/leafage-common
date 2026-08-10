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

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import top.leafage.common.poi.word.element.Heading;
import top.leafage.common.poi.word.element.Image;
import top.leafage.common.poi.word.element.Paragraph;
import top.leafage.common.poi.word.element.Table;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordElementTest {

    @Test
    void rendersHeadingParagraphAndTable() throws Exception {
        Paragraph.ParagraphStyle style = new Paragraph.ParagraphStyle();
        style.setAlignment(ParagraphAlignment.RIGHT);
        style.setSpacing(1.5D);
        style.setFontSize(14);
        style.setFontFamily("Arial");
        style.setBold(true);
        style.setItalic(true);
        style.setColor("FF0000");

        byte[] bytes = WordWriter.write(List.of(
                new Heading(2, "Title"), new Paragraph("Body", style),
                new Table(List.of("A", "B"), List.of(List.of("1")))));

        try (XWPFDocument document = new XWPFDocument(new java.io.ByteArrayInputStream(bytes))) {
            assertEquals("Heading2", document.getParagraphs().getFirst().getStyle());
            assertEquals("Title", document.getParagraphs().getFirst().getText());
            assertEquals(ParagraphAlignment.RIGHT, document.getParagraphs().get(1).getAlignment());
            assertEquals(2, document.getTables().getFirst().getNumberOfRows());
        }
        assertEquals(ParagraphAlignment.RIGHT, style.getAlignment());
        assertEquals(1.5D, style.getSpacing());
        assertEquals(14, style.getFontSize());
        assertEquals("Arial", style.getFontFamily());
        assertTrue(style.isBold());
        assertTrue(style.isItalic());
        assertEquals("FF0000", style.getColor());
    }

    @Test
    void validatesWriterAndElements() {
        assertThrows(NullPointerException.class, () -> WordWriter.write(null));
        assertThrows(NullPointerException.class, () -> WordWriter.write(java.util.Collections.singletonList(null)));
        assertThrows(IllegalArgumentException.class, () -> new Heading(0, "Title"));
        assertThrows(NullPointerException.class, () -> new Heading(1, null));
        assertThrows(NullPointerException.class, () -> new Paragraph(null, null));
        assertThrows(IllegalArgumentException.class, () -> new Table(List.of(), List.of(List.of("value"))));
        assertThrows(IllegalArgumentException.class,
                () -> new Table(List.of("A"), List.of(List.of("1", "2"))));
        assertThrows(IllegalArgumentException.class, () -> new Image(new byte[] {1}, "image.png", 0, 1));
    }

    @Test
    void rendersEmptyTableAsNoOp() throws Exception {
        byte[] bytes = WordWriter.write(List.of(new Table(List.of(), List.of())));

        try (XWPFDocument document = new XWPFDocument(new java.io.ByteArrayInputStream(bytes))) {
            assertTrue(document.getTables().isEmpty());
        }
    }
}
