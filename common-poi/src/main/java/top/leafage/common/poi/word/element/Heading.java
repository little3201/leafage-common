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

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * doc heading
 *
 * @author wq li
 * @since 0.4.2
 */
public class Heading implements WordElement {

    private final int level;
    private final String text;

    /**
     * Constructor for Heading.
     *
     * @param level a int
     * @param text  a {@link String} object
     */
    public Heading(int level, String text) {
        this.level = level;
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Heading" + level);

        XWPFRun run = p.createRun();
        run.setText(text);
        run.setBold(true);
        int fontSize = switch (level) {
            case 1 -> 22;
            case 2 -> 18;
            case 3 -> 16;
            case 4 -> 14;
            default -> 12;
        };
        run.setFontSize(fontSize);
        run.setFontFamily("微软雅黑");
    }
}
