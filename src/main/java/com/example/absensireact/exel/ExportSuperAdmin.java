package com.example.absensireact.exel;

 import com.example.absensireact.exception.NotFoundException;
 import com.example.absensireact.model.Admin;
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
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String[] headers = {"No", "Email", "Username"};
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


    public void importAdmin(MultipartFile file, SuperAdmin superAdmin) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<Admin> adminList = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {  // Start from row 1 to skip headers
            Row row = sheet.getRow(i);
            if (row != null) {
                Admin admin = new Admin();

                Cell emailCell = row.getCell(0);
                Cell usernameCell = row.getCell(1);
                Cell passwordCell = row.getCell(2);

                if (emailCell != null) {
                    admin.setEmail(getCellValue(emailCell));
                }

                if (usernameCell != null) {
                    admin.setUsername(getCellValue(usernameCell));
                }

                if (passwordCell != null) {
                    String encodedPassword = encoder.encode(getCellValue(passwordCell));
                    admin.setPassword(encodedPassword);
                }

                admin.setRole("ADMIN");
                admin.setSuperAdmin(superAdmin);

                adminList.add(admin);
            }
        }

        adminRepository.saveAll(adminList);
        workbook.close();
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


    public static void generateAdminImportTemplate(OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Import Admin");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Email", "Wali Murid", "Password"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);

            // Apply header style
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        // Adjust column width
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to the provided OutputStream
        workbook.write(outputStream);
        workbook.close();
    }



}
