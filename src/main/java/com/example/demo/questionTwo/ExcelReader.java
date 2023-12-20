package com.example.demo.questionTwo;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {
    public static List<Interviews> getInterviewsList(String filePath) {
        List<Interviews> interviews = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(file);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // Assuming you are working with the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Skip the header row
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip the header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int i = 0;

                // Process each cell in the row
                java.util.Date utilDate = row.getCell(i++).getDateCellValue();
                java.sql.Date sqlDate = (utilDate != null) ? new java.sql.Date(utilDate.getTime()) : null;

                Cell cell = row.getCell(i++);
                CellValue cellValue = evaluator.evaluate(cell);
                String excelDateString = (cellValue != null) ? cellValue.getStringValue() : null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yy");
                java.util.Date date = null;
                java.sql.Date sql_Date = null;

                if (excelDateString != null) {
                    try {
                        date = dateFormat.parse(excelDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (date != null) {
                        sql_Date = new java.sql.Date(date.getTime());
                    }
                }

                String team = row.getCell(i++).getStringCellValue();
                String panelName = row.getCell(i++).getStringCellValue();
                String round = row.getCell(i++).getStringCellValue();
                String skill = row.getCell(i++).getStringCellValue();

                Cell timeCell = row.getCell(i++);
                Time timeValue = null;

                if (timeCell.getCellType() == CellType.NUMERIC) {
                    // If the cell type is numeric, convert it to LocalTime
                    timeValue = new Time((long) (timeCell.getNumericCellValue() * 24 * 60 * 60 * 1000));
                }

                String currentLoc = getCellValueAsString(row.getCell(i++), evaluator);
                String preferredLoc = getCellValueAsString(row.getCell(i++), evaluator);
                String candidateName = getCellValueAsString(row.getCell(i++), evaluator);

                Interviews interview = new Interviews(sqlDate, sql_Date, team, panelName, round, skill, timeValue, currentLoc, preferredLoc, candidateName);
                interviews.add(interview);
            }

            // Output the interviews (for testing purposes)
            for (Interviews emp : interviews) {
                System.out.println(emp);
            }

            workbook.close();
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }

        return interviews;
    }

    private static String getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
        CellValue cellValue = evaluator.evaluate(cell);
        return (cellValue != null) ? cellValue.getStringValue() : "";
    }
}
