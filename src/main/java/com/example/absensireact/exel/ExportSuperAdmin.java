package com.example.absensireact.exel;

 import com.example.absensireact.exception.NotFoundException;
 import com.example.absensireact.model.Admin;
 import com.example.absensireact.model.OrangTua;
 import com.example.absensireact.model.SuperAdmin;
 import com.example.absensireact.repository.AdminRepository;
 import com.example.absensireact.repository.SuperAdminRepository;
 import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.stereotype.Service;
 import org.springframework.web.multipart.MultipartFile;

 import javax.servlet.http.HttpServletResponse;
 import java.io.*;
 import java.text.SimpleDateFormat;
 import java.util.*;

@Service
public class ExportSuperAdmin {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

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

        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
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

        List<Admin> absensiList = adminRepository.findBySuperAdminId(superadminId);

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
        String[] headers = {"No", "Email", "Nama Admin"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int userRowNum = 1;
        for (Admin admin : absensiList) {
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(userRowNum++);
            cell0.setCellStyle(styleCenterNumber); // Use the centered number style

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(admin.getEmail());
            cell1.setCellStyle(styleCenterNumber);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(admin.getUsername());
            cell2.setCellStyle(styleCenterNumber);
        }

        // Adjust column width
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ExportAdmin.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void importAdmin(MultipartFile file, Long superAdminId) throws IOException {
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

                Cell emailCell = row.getCell(1);
                Cell namaCell = row.getCell(2);
                Cell passwordCell = row.getCell(3);

                if (emailCell == null || namaCell == null || passwordCell == null) {
                    System.out.println("Skipping row " + rowNum + " due to missing data.");
                    continue;
                }

                Optional<SuperAdmin> adminOptional = superAdminRepository.findById(superAdminId);
                if (adminOptional.isEmpty()) {
                    System.out.println("Admin with id " + superAdminId + " not found.");
                    continue;
                }

                SuperAdmin superAdmin = adminOptional.get();
                Admin superAdminn = new Admin();
                superAdminn.setSuperAdmin(superAdmin);
                superAdminn.setEmail(emailCell.getStringCellValue());
                superAdminn.setUsername(namaCell.getStringCellValue());
                String encodedPassword = encoder.encode(passwordCell.getStringCellValue());
                superAdminn.setPassword(encodedPassword);

                superAdminn.setRole("ADMIN");

                adminList.add(superAdminn);
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

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }


    public void generateAdminImportTemplate(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Excel Admin");

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
        String[] headers = {"No", "Email", "Nama Admin", "Password"};
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
        response.setHeader("Content-Disposition", "attachment; filename=HeaderOnly.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
