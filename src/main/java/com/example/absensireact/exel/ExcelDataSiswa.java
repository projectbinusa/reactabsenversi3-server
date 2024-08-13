package com.example.absensireact.exel;

import com.example.absensireact.model.User;
import com.example.absensireact.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelDataSiswa {

    @Autowired
    private UserService userService;

    public void exportDataSiswa(Long idAdmin, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data Siswa");

        // Font and cell styles
        Font fontWhite = workbook.createFont();
        fontWhite.setColor(IndexedColors.WHITE.getIndex());

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        styleHeader.setFont(headerFont);

        CellStyle styleCenter = workbook.createCellStyle();
        styleCenter.setAlignment(HorizontalAlignment.CENTER);
        styleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        styleCenter.setBorderTop(BorderStyle.THIN);
        styleCenter.setBorderRight(BorderStyle.THIN);
        styleCenter.setBorderBottom(BorderStyle.THIN);
        styleCenter.setBorderLeft(BorderStyle.THIN);

        // Fetch data from service
        List<User> siswaList = userService.getAll();

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA SISWA");
        titleCell.setCellStyle(styleHeader);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4));

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama Siswa", "Email", "Start Belajar", "Status Belajar"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int no = 1;
        for (User siswa : siswaList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(no++);
            row.getCell(0).setCellStyle(styleCenter);

            row.createCell(1).setCellValue(siswa.getUsername());
            row.getCell(1).setCellStyle(styleCenter);

            row.createCell(2).setCellValue(siswa.getEmail());
            row.getCell(2).setCellStyle(styleCenter);

            row.createCell(3).setCellValue(siswa.getStartKerja());
            row.getCell(3).setCellStyle(styleCenter);

            row.createCell(4).setCellValue(siswa.getStatusKerja());
            row.getCell(4).setCellStyle(styleCenter);
        }

        // Adjust column width
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=DataSiswa.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void templateExcelSiswa(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Excel Siswa");

        // Font and cell styles
        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        styleHeader.setFont(headerFont);

        // Header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"No", "Nama Siswa", "Email",  "Password", "idJabatan", "idOrangTua", "idShift", "idOrganisasi"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Adjust column width
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=TemplateSiswa.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
