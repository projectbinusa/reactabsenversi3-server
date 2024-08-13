package com.example.absensireact.exel;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.SuperAdmin;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.OrganisasiRepository;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.repository.OrangTuaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ExportSuperAdmin {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    @Autowired
    private OrganisasiRepository organisasiRepository;

    @Autowired
    PasswordEncoder encoder;

    public void excelAdmin(Long superadminId, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DATA ADMIN");

        // Cell styles
        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Font for header
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        styleHeader.setFont(headerFont);

        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.LEFT); // Set title alignment to left
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);

        CellStyle styleCenterNumber = workbook.createCellStyle();
        styleCenterNumber.setAlignment(HorizontalAlignment.CENTER);
        styleCenterNumber.setVerticalAlignment(VerticalAlignment.CENTER);
        styleCenterNumber.setBorderTop(BorderStyle.THIN);
        styleCenterNumber.setBorderRight(BorderStyle.THIN);
        styleCenterNumber.setBorderBottom(BorderStyle.THIN);
        styleCenterNumber.setBorderLeft(BorderStyle.THIN);

        List<Admin> adminList = adminRepository.findBySuperAdminId(superadminId);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA ADMIN");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2)); // Merging cells for title
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Email", "Username"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int userRowNum = 1;
        for (Admin admin : adminList) {
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(userRowNum++);
            cell0.setCellStyle(styleCenterNumber);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(admin.getEmail());
            cell1.setCellStyle(styleCenterNumber);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(admin.getUsername());
            cell2.setCellStyle(styleCenterNumber);
        }

        // Adjust column width
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Prepare response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ExportAdmin.xlsx");

        // Write to response
        workbook.write(response.getOutputStream());
        workbook.close();
    }



//    public void importAdmin(MultipartFile file, Long superAdminId) throws IOException {
//        Workbook workbook = new XSSFWorkbook(file.getInputStream());
//        Sheet sheet = workbook.getSheetAt(0);  // Mengambil sheet pertama
//
//        List<Admin> adminList = new ArrayList<>();
//        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
//            Row row = sheet.getRow(i);
//            if (row != null) {
//                Admin admin = new Admin();
//
//
//                Cell emailCell = row.getCell(1);
//                Cell usernameCell = row.getCell(2);
//                Cell passwordCell = row.getCell(3);
//
//                if (emailCell != null) {
//                    admin.setEmail(getCellValue(emailCell));
//                }
//
//                if (usernameCell != null) {
//                    admin.setUsername(getCellValue(usernameCell));
//                }
//
//                 if (passwordCell != null) {
//                     String encodedPassword = encoder.encode(getCellValue(passwordCell));
//                     admin.setPassword(encodedPassword);
//                 }
//
//                admin.setRole("ADMIN");
//                admin.setSuperAdmin(superAdminRepository.findById(superAdminId)
//                        .orElseThrow(() -> new NotFoundException("id super admin tidak ditemukan")));
//
//                adminList.add(admin);
//            }
//        }
//
//        adminRepository.saveAll(adminList);
//        workbook.close();
//    }


    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int)cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public void importAdmin(Long superAdminId, MultipartFile file) throws IOException {
        List<Admin> adminList = new ArrayList<>();

        // Read Excel file
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through rows and columns
            Iterator<Row> rowIterator = sheet.iterator();
            int rowNum = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // Skip the title and header rows
                if (rowNum++ < 1) {
                    continue;
                }

                // Log to check row data
                System.out.println("Processing row " + rowNum);

                Cell namaCell = row.getCell(1);
                Cell emailCell = row.getCell(2);
                Cell passwordCell = row.getCell(3);

                if (emailCell == null || namaCell == null || passwordCell == null) {
                    System.out.println("Skipping row " + rowNum + " due to missing data.");
                    continue;
                }

                Optional<SuperAdmin> superAdminOptional = superAdminRepository.findById(superAdminId);
                if (superAdminOptional.isEmpty()) {
                    System.out.println("SuperAdmin with id " + superAdminId + " not found.");
                    continue;
                }

                SuperAdmin superAdmin = superAdminOptional.get();
                Admin admin = new Admin();
                admin.setSuperAdmin(superAdmin);
                admin.setEmail(emailCell.getStringCellValue());
                admin.setUsername(namaCell.getStringCellValue());
                String encodedPassword = encoder.encode(passwordCell.getStringCellValue());
                admin.setPassword(encodedPassword);

                admin.setRole("ADMIN");

                adminList.add(admin);
            }
            workbook.close();
        }

        if (!adminList.isEmpty()) {
            adminRepository.saveAll(adminList);
            System.out.println("Saving " + adminList.size() + " records to database.");
        } else {
            System.out.println("No records to save.");
        }
    }

    public void templateExcelAdmin(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Excel Admin");

        // Font and cell styles
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex()); // Set text color to white

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFont(headerFont);
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); // Set background color to light blue
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"No", "Username", "Email", "Password"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Adjust column width
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Set response headers and write workbook to output stream
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Template-Import.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }




    public static void generateAdminImportTemplate(OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Template Import Admin");

        // Title style
        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);

        // Header style
        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA ADMIN");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 3)); // Merging cells for title
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Email", "Username", "Password"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(outputStream);
        workbook.close();
    }





}
