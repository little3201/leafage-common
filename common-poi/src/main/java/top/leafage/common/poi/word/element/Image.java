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

package top.leafage.common.poi.word.element;

import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * doc image paragraph
 *
 * @author wq li
 * @since 0.4.3
 */
public class Image implements WordElement {

    private final byte[] bytes;
    private final String fileName;
    private final int width;
    private final int height;

    /**
     * Constructor for Image.
     *
     * @param bytes    an array of bytes
     * @param fileName a {@link String} object
     * @param width    a int
     * @param height   a int
     */
    public Image(byte[] bytes, String fileName, int width, int height) {
        this.bytes = bytes;
        this.fileName = fileName;
        this.width = width;
        this.height = height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(XWPFDocument doc) {
        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun run = paragraph.createRun();
        try {
            FileMagic type = FileMagic.valueOf(bytes);
            run.addPicture(new ByteArrayInputStream(bytes), PictureType.valueOf(type),
                    fileName, Units.toEMU(width), Units.toEMU(height));
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
