/*
 * Copyright (c) 2026.  little3201.
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

package top.leafage.common.poi.word.element;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * doc paragraph
 *
 * @author wq li
 * @since 0.4.2
 */
public class Paragraph implements WordElement {

    private final String text;
    private final ParagraphStyle style;

    public Paragraph(String text, ParagraphStyle style) {
        this.text = text;
        this.style = style;
    }

    @Override
    public void render(XWPFDocument doc) {
        XWPFParagraph paragraph = doc.createParagraph();
        if (style != null) {
            if (style.getAlignment() != null) paragraph.setAlignment(style.getAlignment());
            if (style.getSpacing() != null) paragraph.setSpacingBetween(style.getSpacing());
        }

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        if (style != null) {
            run.setFontSize(style.getFontSize());
            run.setFontFamily(style.getFontFamily());
            run.setBold(style.isBold());
            run.setItalic(style.isItalic());
            run.setColor(style.getColor());
        } else {
            run.setFontSize(12);
            run.setFontFamily("宋体");
        }
    }

    public static class ParagraphStyle {
        private ParagraphAlignment alignment;
        private Double spacing;
        private int fontSize = 11;
        private String fontFamily = "宋体";
        private boolean bold = false;
        private boolean italic = false;
        private String color = "000000";

        // getters and setters
        public ParagraphAlignment getAlignment() {
            return alignment;
        }

        public void setAlignment(ParagraphAlignment alignment) {
            this.alignment = alignment;
        }

        public Double getSpacing() {
            return spacing;
        }

        public void setSpacing(Double spacing) {
            this.spacing = spacing;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        public String getFontFamily() {
            return fontFamily;
        }

        public void setFontFamily(String fontFamily) {
            this.fontFamily = fontFamily;
        }

        public boolean isBold() {
            return bold;
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public boolean isItalic() {
            return italic;
        }

        public void setItalic(boolean italic) {
            this.italic = italic;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}
