package com.example.timesheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExcelExporter {

    DatabaseController databaseController;
    String excelFilePath = "timesheet-export.xlsx";

    public ExcelExporter() {
        databaseController = DatabaseController.getInstance();
    }

    /*
     * Exports logs of all users into 1 excel file
     */
    public void exportAll(int periodId) {
        List<ExcelRow> rows = databaseController.getAllLogsInPeriod(periodId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Timesheet");
        createHeaders(sheet);
        createRows(rows, workbook, sheet);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createHeaders(XSSFSheet sheet) {
        Row headerRow = sheet.createRow(0);
        List<String> headerNames = Arrays.asList("Zaměstnavatel", "Zaměstnanec", "Projekt", "Příjemce služby",
                "Služba", "Detail", "Počet hodin");

        for (int i = 0; i < headerNames.size(); i++) {
            String headerName = headerNames.get(i);
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headerName);
        }
    }

    private void createRows(List<ExcelRow> rows, XSSFWorkbook workbook, XSSFSheet sheet) {
        int rowCount = 1;

        for(ExcelRow dataRow : rows) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            Cell cell = row.createCell(columnCount++);
            cell.setCellValue("EP Brno Project, s.r.o.");

            cell = row.createCell(columnCount++);
            cell.setCellValue(dataRow.getEmployeeName());

            cell = row.createCell(columnCount++);
            cell.setCellValue(dataRow.getProject());

            cell = row.createCell(columnCount++);
            cell.setCellValue(dataRow.getCompany());

            cell = row.createCell(columnCount++);
            cell.setCellValue(dataRow.getService());

            cell = row.createCell(columnCount++);
            cell.setCellValue(dataRow.getDetails());

            cell = row.createCell(columnCount);
            cell.setCellValue(dataRow.getHoursWorked());
        }
    }
}
