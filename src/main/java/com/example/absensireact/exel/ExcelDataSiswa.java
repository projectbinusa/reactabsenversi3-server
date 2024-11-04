package com.example.absensireact.exel;

import com.example.absensireact.model.UserModel;
import com.example.absensireact.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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
        styleHeader.setAlignment(HorizontalAlignment.LEFT);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);

        // Set background color to the header style
        styleHeader.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        styleHeader.setFont(headerFont);

        CellStyle styleLeft = workbook.createCellStyle();
        styleLeft.setAlignment(HorizontalAlignment.LEFT);
        styleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLeft.setBorderTop(BorderStyle.THIN);
        styleLeft.setBorderRight(BorderStyle.THIN);
        styleLeft.setBorderBottom(BorderStyle.THIN);
        styleLeft.setBorderLeft(BorderStyle.THIN);

        // Fetch data from service
        List<UserModel> siswaList = userService.getAllByAdmin(idAdmin);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA SISWA");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4));

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama Siswa", "Email", "Start Belajar", "Status Belajar", "Kelas"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int no = 1;
        for (UserModel siswa : siswaList) {
            Row row = sheet.createRow(rowNum++);

            // Handle null values to ensure export continues even with null data
            row.createCell(0).setCellValue(no++);
            row.getCell(0).setCellStyle(styleLeft);

            row.createCell(1).setCellValue(siswa.getUsername() != null ? siswa.getUsername() : "");
            row.getCell(1).setCellStyle(styleLeft);

            row.createCell(2).setCellValue(siswa.getEmail() != null ? siswa.getEmail() : "");
            row.getCell(2).setCellStyle(styleLeft);

            row.createCell(3).setCellValue(siswa.getStartKerja() != null ? siswa.getStartKerja() : "");
            row.getCell(3).setCellStyle(styleLeft);

            row.createCell(4).setCellValue(siswa.getStatusKerja() != null ? siswa.getStatusKerja() : "");
            row.getCell(4).setCellStyle(styleLeft);

            row.createCell(5).setCellValue(siswa.getKelas() != null && siswa.getKelas().getNamaKelas() != null ? siswa.getKelas().getNamaKelas() : "");
            row.getCell(5).setCellStyle(styleLeft);
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


    public void exportDataSiswaperKelas(Long idAdmin, Long KlasId, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data Siswa");

        // Font and cell styles
        Font fontWhite = workbook.createFont();
        fontWhite.setColor(IndexedColors.WHITE.getIndex());

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.LEFT);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);

        // Set background color to the header style
        styleHeader.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());  // Set font color to white
        styleHeader.setFont(headerFont);

        CellStyle styleLeft = workbook.createCellStyle(); // Mengubah nama menjadi styleLeft
        styleLeft.setAlignment(HorizontalAlignment.LEFT); // Diubah menjadi kiri
        styleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLeft.setBorderTop(BorderStyle.THIN);
        styleLeft.setBorderRight(BorderStyle.THIN);
        styleLeft.setBorderBottom(BorderStyle.THIN);
        styleLeft.setBorderLeft(BorderStyle.THIN);

        // Fetch data from service
        List<UserModel> siswaList = userService.getAllByAdminandKelas(idAdmin, KlasId);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA SISWA PERKELAS");
//        titleCell.setCellStyle(styleHeader);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4));

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama Siswa", "Email", "Start Belajar", "Status Belajar", "Kelas"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int no = 1;
        for (UserModel siswa : siswaList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(no++);
            row.getCell(0).setCellStyle(styleLeft);

            row.createCell(1).setCellValue(siswa.getUsername() != null ? siswa.getUsername() : "");
            row.getCell(1).setCellStyle(styleLeft);

            row.createCell(2).setCellValue(siswa.getEmail() != null ? siswa.getEmail() : "");
            row.getCell(2).setCellStyle(styleLeft);

            row.createCell(3).setCellValue(siswa.getStartKerja() != null ? siswa.getStartKerja() : "");
            row.getCell(3).setCellStyle(styleLeft);

            row.createCell(4).setCellValue(siswa.getStatusKerja() != null ? siswa.getStatusKerja() : "");
            row.getCell(4).setCellStyle(styleLeft);

            row.createCell(5).setCellValue(siswa.getKelas().getNamaKelas() != null ? siswa.getKelas().getNamaKelas() : "");
            row.getCell(5).setCellStyle(styleLeft);
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


    public static void templateExcelSiswa(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Siswa");

        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.LEFT);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleNote = workbook.createCellStyle();
        styleNote.setAlignment(HorizontalAlignment.LEFT);
        styleNote.setVerticalAlignment(VerticalAlignment.CENTER);
        Font noteFont = workbook.createFont();
        noteFont.setItalic(true);
        styleNote.setFont(noteFont);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA Siswa");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7)); // Adjusted for new "Kelas" column
        rowNum++;

        // Note row
        Row noteRow = sheet.createRow(rowNum++);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("Catatan: Jangan berikan spasi pada awal kata saat mengisi data.");
        noteCell.setCellStyle(styleNote);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7)); // Adjusted for new "Kelas" column
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama Siswa", "Email", "Password", "Nama Wali Murid", "Nama Waktu Pembelajaran", "Nama Organisasi", "Kelas"};
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

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }

        workbook.close();
    }


    public static void templateExcelSiswaPerKelas(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Siswa");

        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.LEFT);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleNote = workbook.createCellStyle();
        styleNote.setAlignment(HorizontalAlignment.LEFT);
        styleNote.setVerticalAlignment(VerticalAlignment.CENTER);
        Font noteFont = workbook.createFont();
        noteFont.setItalic(true);
        styleNote.setFont(noteFont);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Data Siswa");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7)); // Adjusted for new "Kelas" column
        rowNum++;

        // Note row
        Row noteRow = sheet.createRow(rowNum++);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("Catatan: Jangan berikan spasi pada awal kata saat mengisi data.");
        noteCell.setCellStyle(styleNote);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7)); // Adjusted for new "Kelas" column
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama Siswa", "Email", "Password", "Nama Wali Murid", "Nama Waktu Pembelajaran", "Nama Organisasi"};
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

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }

        workbook.close();
    }


}
