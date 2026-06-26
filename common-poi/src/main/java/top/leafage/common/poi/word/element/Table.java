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

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.List;

/**
 * doc table
 *
 * @author wq li
 * @since 0.4.3
 */
public class Table implements WordElement {

    private final List<String> headers;
    private final List<List<String>> rows;


    /**
     * Constructor for Table.
     *
     * @param headers a {@link List} object
     * @param rows a {@link List} object
     */
    public Table(List<String> headers, List<List<String>> rows) {
        this.headers = headers;
        this.rows = rows;
    }

    /** {@inheritDoc} */
    @Override
    public void render(XWPFDocument doc) {
        if (CollectionUtils.isEmpty(headers) && CollectionUtils.isEmpty(rows)) return;

        XWPFTable table = doc.createTable(1, headers.size());

        // 设置宽度
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CTTblWidth tblWidth = tblPr.addNewTblW();
        tblWidth.setType(STTblWidth.DXA);
        tblWidth.setW(BigInteger.valueOf(8500));

        // 表头
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.size(); i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            if (cell == null) cell = headerRow.createCell();
            setCellText(cell, headers.get(i), true);
        }

        // 数据行
        for (List<String> rowData : rows) {
            XWPFTableRow row = table.createRow();
            for (int i = 0; i < rowData.size(); i++) {
                XWPFTableCell cell = row.getCell(i);
                if (cell == null) cell = row.createCell();
                setCellText(cell, rowData.get(i), false);
            }
        }
    }

    private void setCellText(XWPFTableCell cell, String text, boolean isHeader) {
        cell.setText(text);

        CTTcPr tcPr = cell.getCTTc().addNewTcPr();
        CTVerticalJc vAlign = tcPr.addNewVAlign();
        vAlign.setVal(STVerticalJc.CENTER);

        for (XWPFParagraph p : cell.getParagraphs()) {
            p.setAlignment(ParagraphAlignment.CENTER);
            List<XWPFRun> runs = p.getRuns();
            if (runs != null && !runs.isEmpty()) {
                XWPFRun run = runs.getFirst();
                run.setBold(isHeader);
                if (isHeader) {
                    run.setFontSize(12);
                    run.setFontFamily("微软雅黑");
                }
            }
        }
    }
}
