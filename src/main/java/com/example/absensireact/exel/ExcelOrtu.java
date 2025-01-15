package com.example.absensireact.exel;

import com.example.absensireact.model.OrangTua;
import com.example.absensireact.service.OrangTuaService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelOrtu {

    @Autowired
    private OrangTuaService orangTuaService;

    public void excelOrangTua(Long idAdmin, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data Orang Tua");

        // Font and cell styles
        Font fontWhite = workbook.createFont();
        fontWhite.setColor(IndexedColors.WHITE.getIndex());

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.LEFT); // Diubah menjadi kiri
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        styleHeader.setFont(headerFont);

        CellStyle styleLeft = workbook.createCellStyle(); // Mengubah nama menjadi styleLeft
        styleLeft.setAlignment(HorizontalAlignment.LEFT); // Diubah menjadi kiri
        styleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLeft.setBorderTop(BorderStyle.THIN);
        styleLeft.setBorderRight(BorderStyle.THIN);
        styleLeft.setBorderBottom(BorderStyle.THIN);
        styleLeft.setBorderLeft(BorderStyle.THIN);

        // Fetch data from service
        List<OrangTua> orangTuaList = orangTuaService.getAllByAdmin(idAdmin);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA ORANG TUA");
        CellStyle styleTitle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);
        titleCell.setCellStyle(styleTitle);

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Email", "Nama Wali Murid"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int no = 1;
        for (OrangTua orangTua : orangTuaList) {
            Row row = sheet.createRow(rowNum++);
//            row.createCell(0).setCellValue(no++);
//            row.getCell(0).setCellStyle(styleLeft); // Menggunakan styleLeft

            row.createCell(0).setCellValue(orangTua.getId());
            row.getCell(0).setCellStyle(styleLeft);

            row.createCell(1).setCellValue(orangTua.getEmail());
            row.getCell(1).setCellStyle(styleLeft); // Menggunakan styleLeft

            row.createCell(2).setCellValue(orangTua.getNama());
            row.getCell(2).setCellStyle(styleLeft); // Menggunakan styleLeft
        }

        // Adjust column width
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=DataOrangTua.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }



    public void templateExcelWaliMurid(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Excel Wali Murid");

        // Deklarasi header
        String[] headers = {"No", "Nama Wali Murid", "Email", "Password"};

        // Font and cell styles for header
        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.LEFT);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        styleHeader.setFont(headerFont);

        // Catatan di atas header
        Row noteRow = sheet.createRow(0);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("Catatan: Jangan berikan spasi pada awal kata saat mengisi data.");

        // Menggunakan wrap text untuk catatan
        CellStyle noteStyle = workbook.createCellStyle();
        noteStyle.setAlignment(HorizontalAlignment.LEFT);
        noteStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        noteStyle.setWrapText(true); // Mengaktifkan wrap text
        noteCell.setCellStyle(noteStyle);

        // Mengatur lebar kolom pertama agar cukup untuk catatan
        sheet.setColumnWidth(0, 15000); // Atur lebar kolom pertama agar catatan terlihat jelas

        // Merge cells for the note
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        // Header row
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Adjust column width untuk memperbesar kolom
        sheet.setColumnWidth(0, 3000);  // No
        sheet.setColumnWidth(1, 10000); // Nama Wali Murid
        sheet.setColumnWidth(2, 10000); // Email
        sheet.setColumnWidth(3, 8000);  // Password

        // Set response headers and write workbook
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=TemplateExcelWaliMurid.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
