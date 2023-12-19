package com.example.demo.questionTwo;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;


public class ExcelReader {
    static public List<Interviews> getInterviewsList() {
        List<Interviews> interviews = new ArrayList<>();
        try (FileInputStream file = new FileInputStream("C:\\Users\\jyoti1\\Downloads\\AccoliteInterviewData.xlsx")) {
            Workbook workbook = WorkbookFactory.create(file);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // Assuming you are working with the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            int n = sheet.getPhysicalNumberOfRows();
            int k = 0;
            for (Row row : sheet) {
                int i = 0;
                if (k == 0) {
                    k++;
                    continue;
                }
                // Iterate through cells in the row
                java.util.Date utilDate = row.getCell(i++).getDateCellValue();
                java.sql.Date sqlDate=null;
                if(utilDate!=null) {
                    sqlDate = new java.sql.Date(utilDate.getTime());
                }
                Cell cell = row.getCell(i++); // Get the cell containing the formula
                CellValue cellValue = evaluator.evaluate(cell);
                String excelDateString = cellValue.getStringValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yy");
                java.util.Date date = null;
                java.sql.Date sql_Date = null;
                if(excelDateString!=null) {
                    try {
                        date = dateFormat.parse(excelDateString);
                        // Now 'javaDate' can be used in your database insertion logic
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date!=null) {
                        sql_Date = new java.sql.Date(date.getTime());
                    }
                }
                String Team = row.getCell(i++).getStringCellValue();
                String PanelName = row.getCell(i++).getStringCellValue();
                String Round = row.getCell(i++).getStringCellValue();
                String skill = row.getCell(i++).getStringCellValue();
                Cell timeCell = row.getCell(i++);
                Time timeValue = null;
                if (timeCell.getCellType() == CellType.NUMERIC) {
                    // If the cell type is numeric, convert it to LocalTime
                    timeValue = new Time((long) (timeCell.getNumericCellValue() * 24 * 60 * 60 * 1000));
                }
                Cell currcell=row.getCell(i++);
                CellValue currentLocation=evaluator.evaluate(currcell);
                String CurrentLoc="";
                if(currentLocation!=null){
                    CurrentLoc= currentLocation.getStringValue();
                }
                Cell prefcell=row.getCell(i++);
                CellValue preferenceValue=evaluator.evaluate(prefcell);
                String PreferredLoc="";
                if(preferenceValue!=null){
                    PreferredLoc=preferenceValue.getStringValue();
                }
                Cell candicell=row.getCell(i++);
                CellValue candidatevalue=evaluator.evaluate(candicell);
                String CandidateName="";
                if(candidatevalue!=null){
                    CandidateName= candidatevalue.getStringValue();
                }
                Interviews interview = new Interviews(sqlDate,sql_Date, Team, PanelName, Round, skill, timeValue, CurrentLoc, PreferredLoc, CandidateName);
                interviews.add(interview);
            }
            for(Interviews emp: interviews){
                System.out.println(emp);
            }
            workbook.close();
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }
        return interviews;
    }
}