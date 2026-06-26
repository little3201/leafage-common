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
 * @since 0.4.3
 */
public class Paragraph implements WordElement {

    private final String text;
    private final ParagraphStyle style;

    /**
     * Constructor for Paragraph.
     *
     * @param text  a {@link String} object
     * @param style a {@link Paragraph.ParagraphStyle} object
     */
    public Paragraph(String text, ParagraphStyle style) {
        this.text = text;
        this.style = style;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Paragraph style.
     */
    public static class ParagraphStyle {
        private ParagraphAlignment alignment;
        private Double spacing;
        private int fontSize = 11;
        private String fontFamily = "宋体";
        private boolean bold = false;
        private boolean italic = false;
        private String color = "000000";

        /**
         * Getter for the field <code>alignment</code>.
         *
         * @return a {@link ParagraphAlignment} object
         */
        public ParagraphAlignment getAlignment() {
            return alignment;
        }

        /**
         * Setter for the field <code>alignment</code>.
         *
         * @param alignment a {@link ParagraphAlignment} object
         */
        public void setAlignment(ParagraphAlignment alignment) {
            this.alignment = alignment;
        }

        /**
         * Getter for the field <code>spacing</code>.
         *
         * @return a {@link Double} object
         */
        public Double getSpacing() {
            return spacing;
        }

        /**
         * Setter for the field <code>spacing</code>.
         *
         * @param spacing a {@link Double} object
         */
        public void setSpacing(Double spacing) {
            this.spacing = spacing;
        }

        /**
         * Getter for the field <code>fontSize</code>.
         *
         * @return a int
         */
        public int getFontSize() {
            return fontSize;
        }

        /**
         * Setter for the field <code>fontSize</code>.
         *
         * @param fontSize a int
         */
        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        /**
         * Getter for the field <code>fontFamily</code>.
         *
         * @return a {@link String} object
         */
        public String getFontFamily() {
            return fontFamily;
        }

        /**
         * Setter for the field <code>fontFamily</code>.
         *
         * @param fontFamily a {@link String} object
         */
        public void setFontFamily(String fontFamily) {
            this.fontFamily = fontFamily;
        }

        /**
         * Getter for the field <code>bold</code>.
         *
         * @return a boolean
         */
        public boolean isBold() {
            return bold;
        }

        /**
         * Setter for the field <code>bold</code>.
         *
         * @param bold a boolean
         */
        public void setBold(boolean bold) {
            this.bold = bold;
        }

        /**
         * Getter for the field <code>italic</code>.
         *
         * @return a boolean
         */
        public boolean isItalic() {
            return italic;
        }

        /**
         * Setter for the field <code>italic</code>.
         *
         * @param italic a boolean
         */
        public void setItalic(boolean italic) {
            this.italic = italic;
        }

        /**
         * Getter for the field <code>color</code>.
         *
         * @return a {@link Double} object
         */
        public String getColor() {
            return color;
        }

        /**
         * Setter for the field <code>color</code>.
         *
         * @param color a {@link String} object
         */
        public void setColor(String color) {
            this.color = color;
        }
    }
}
