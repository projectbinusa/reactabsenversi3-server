package com.example.absensireact.exel;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.*;
import com.example.absensireact.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelDataAdmin {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AbsensiRepository absensiRepository;

    @Autowired
    private OrganisasiRepository organisasiRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KelasRepository kelasRepository;

    //    Guru
//public void exportGuru(Long idAdmin, Long idKelas, int bulan, int tahun, HttpServletResponse response) throws IOException {
//    // Create a new workbook
//    Workbook workbook = new XSSFWorkbook();
//    Sheet sheet = workbook.createSheet("DATA ABSENSI GURU");
//
//    // Define cell styles
//    CellStyle styleTitle = workbook.createCellStyle();
//    styleTitle.setAlignment(HorizontalAlignment.CENTER);
//    styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
//    Font titleFont = workbook.createFont();
//    titleFont.setBold(true);
//    styleTitle.setFont(titleFont);
//
//    CellStyle styleHeader = workbook.createCellStyle();
//    styleHeader.setAlignment(HorizontalAlignment.CENTER);
//    styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
//    styleHeader.setBorderTop(BorderStyle.THIN);
//    styleHeader.setBorderRight(BorderStyle.THIN);
//    styleHeader.setBorderBottom(BorderStyle.THIN);
//    styleHeader.setBorderLeft(BorderStyle.THIN);
//    styleHeader.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
//    styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//    CellStyle styleData = workbook.createCellStyle();
//    styleData.setAlignment(HorizontalAlignment.CENTER);
//    styleData.setVerticalAlignment(VerticalAlignment.CENTER);
//    styleData.setBorderTop(BorderStyle.THIN);
//    styleData.setBorderRight(BorderStyle.THIN);
//    styleData.setBorderBottom(BorderStyle.THIN);
//    styleData.setBorderLeft(BorderStyle.THIN);
//
//    CellStyle styleTotal = workbook.createCellStyle();
//    styleTotal.cloneStyleFrom(styleData);
//    styleTotal.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
//    styleTotal.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//    // Fetch absensi data
//    List<Absensi> absensiList = absensiRepository.findByKelasIdAndBulan(idKelas, bulan, tahun);
//
//    // Group absensi data by user
//    Map<String, List<Absensi>> absensiByUser = absensiList.stream()
//            .collect(Collectors.groupingBy(absensi -> absensi.getUser().getUsername()));
//
//    // Find maximum date in the absensi data
//    int maxTanggal = absensiList.stream()
//            .mapToInt(absensi -> {
//                LocalDate localDate = absensi.getTanggalAbsen().toInstant()
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDate();
//                return localDate.getDayOfMonth();
//            })
//            .max()
//            .orElse(31); // Default to 31 if no data is present
//
//    int rowNum = 0;
//
//    // Title row
//    Row titleRow = sheet.createRow(rowNum++);
//    Cell titleCell = titleRow.createCell(0);
//    titleCell.setCellValue("DATA ABSENSI GURU DAN KARYAWAN SMK BINA NUSANTARA SEMARANG");
//    titleCell.setCellStyle(styleTitle);
//    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxTanggal + 2)); // Adjust for the actual number of days
//
//    // Header row
//    Row headerRow = sheet.createRow(rowNum++);
//    String[] headers = new String[maxTanggal + 3];  // Adjusted for the removal of the Total Kehadiran column
//    headers[0] = "No";
//    headers[1] = "Nama";
//    for (int i = 1; i <= maxTanggal; i++) {
//        headers[i + 1] = String.valueOf(i);
//    }
//    headers[maxTanggal + 2] = "TOTAL";
//    for (int i = 0; i < headers.length; i++) {
//        if (headers[i] != null) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(headers[i]);
//            cell.setCellStyle(styleHeader);
//        }
//    }
//
//    // Data rows
//    int no = 1;
//    for (Map.Entry<String, List<Absensi>> entry : absensiByUser.entrySet()) {
//        String username = entry.getKey();
//        List<Absensi> userAbsensi = entry.getValue();
//
//        Row dataRow = sheet.createRow(rowNum++);
//
//        // No
//        Cell cellNo = dataRow.createCell(0);
//        cellNo.setCellValue(no++);
//        cellNo.setCellStyle(styleData);
//
//        // Nama
//        Cell cellNama = dataRow.createCell(1);
//        cellNama.setCellValue(username);
//        cellNama.setCellStyle(styleData);
//
//        // Kehadiran per tanggal
//        int totalKehadiran = 0;
//        for (int i = 1; i <= maxTanggal; i++) {
//            final int tanggal = i; // Declare as final to be used in lambda expression
//            Cell cellTanggal = dataRow.createCell(i + 1);
//
//            // Find absensi for this date
//            boolean isPresent = userAbsensi.stream()
//                    .anyMatch(absensi -> {
//                        LocalDate localDate = absensi.getTanggalAbsen().toInstant()
//                                .atZone(ZoneId.systemDefault())
//                                .toLocalDate();
//                        return localDate.getDayOfMonth() == tanggal; // Use the final variable
//                    });
//
//            if (isPresent) {
//                totalKehadiran++;
//                cellTanggal.setCellValue("✓");
//            } else {
//                cellTanggal.setCellValue("");
//            }
//            cellTanggal.setCellStyle(styleData);
//        }
//
//        // Total Kehadiran
//        Cell cellTotal = dataRow.createCell(maxTanggal + 2); // Adjusted column for total attendance
//        cellTotal.setCellValue(totalKehadiran);
//        cellTotal.setCellStyle(styleTotal);
//    }
//
//    // Add row for total attendance in column B
//    Row totalRow = sheet.createRow(rowNum++);
//    Cell totalLabelCell = totalRow.createCell(0);
//    totalLabelCell.setCellValue("TOTAL KESELURUHAN");
//    totalLabelCell.setCellStyle(styleTotal);
//
//    // Calculate total attendance for each day and display in each respective column
//    for (int i = 1; i <= maxTanggal; i++) {
//        final int tanggal = i;
//        int totalForDay = 0;
//        for (List<Absensi> userAbsensi : absensiByUser.values()) {
//            // Count attendance for this date
//            boolean isPresent = userAbsensi.stream()
//                    .anyMatch(absensi -> {
//                        LocalDate localDate = absensi.getTanggalAbsen().toInstant()
//                                .atZone(ZoneId.systemDefault())
//                                .toLocalDate();
//                        return localDate.getDayOfMonth() == tanggal;
//                    });
//            if (isPresent) {
//                totalForDay++;
//            }
//        }
//        // Add the total attendance for this day to the total row in the respective column
//        Cell cellForDay = totalRow.createCell(i + 1);
//        cellForDay.setCellValue(totalForDay);
//        cellForDay.setCellStyle(styleTotal);
//    }
//
//    // Add total row for overall attendance
//    int grandTotal = 0;
//    for (int i = 1; i <= maxTanggal; i++) {
//        grandTotal += sheet.getRow(rowNum - 1).getCell(i + 1).getNumericCellValue();
//    }
//
//    Cell grandTotalCell = totalRow.createCell(maxTanggal + 2);
//    grandTotalCell.setCellValue(grandTotal);
//    grandTotalCell.setCellStyle(styleTotal);
//
//    // Adjust column width
//    for (int i = 0; i < headers.length; i++) {
//        sheet.autoSizeColumn(i);
//    }
//
//    // Write to response
//    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//    response.setHeader("Content-Disposition", "attachment; filename=export_absensi_guru.xlsx");
//    try (OutputStream out = response.getOutputStream()) {
//        workbook.write(out);
//    } finally {
//        workbook.close();
//    }
//}
    public void exportGuru(Long idAdmin, Long idKelas, int bulan, int tahun, HttpServletResponse response) throws IOException {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DATA ABSENSI GURU");

        // Define cell styles
        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleData = workbook.createCellStyle();
        styleData.setAlignment(HorizontalAlignment.CENTER);
        styleData.setVerticalAlignment(VerticalAlignment.CENTER);
        styleData.setBorderTop(BorderStyle.THIN);
        styleData.setBorderRight(BorderStyle.THIN);
        styleData.setBorderBottom(BorderStyle.THIN);
        styleData.setBorderLeft(BorderStyle.THIN);

        CellStyle styleTotal = workbook.createCellStyle();
        styleTotal.cloneStyleFrom(styleData);
        styleTotal.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
        styleTotal.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Define orange style for highlighting absence
        CellStyle styleOrange = workbook.createCellStyle();
        styleOrange.setFillForegroundColor(IndexedColors.ORANGE.index);
        styleOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleOrange.setBorderTop(BorderStyle.THIN);
        styleOrange.setBorderRight(BorderStyle.THIN);
        styleOrange.setBorderBottom(BorderStyle.THIN);
        styleOrange.setBorderLeft(BorderStyle.THIN);

        // Fetch absensi data
        List<Absensi> absensiList = absensiRepository.findByKelasIdAndBulan(idKelas, bulan, tahun);

        // Group absensi data by user
        Map<String, List<Absensi>> absensiByUser = absensiList.stream()
                .collect(Collectors.groupingBy(absensi -> absensi.getUser().getUsername()));

        // Find maximum date in the absensi data
        int maxTanggal = absensiList.stream()
                .mapToInt(absensi -> {
                    LocalDate localDate = absensi.getTanggalAbsen().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return localDate.getDayOfMonth();
                })
                .max()
                .orElse(31); // Default to 31 if no data is present

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA ABSENSI GURU DAN KARYAWAN SMK BINA NUSANTARA SEMARANG");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxTanggal + 3)); // Adjust for the actual number of days + new percentage column

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = new String[maxTanggal + 4];  // Adjusted for the new "Persentase Kehadiran" column
        headers[0] = "No";
        headers[1] = "Nama";
        for (int i = 1; i <= maxTanggal; i++) {
            headers[i + 1] = String.valueOf(i);
        }
        headers[maxTanggal + 2] = "TOTAL";
        headers[maxTanggal + 3] = "PERSENTASE KEHADIRAN"; // New column for percentage
        for (int i = 0; i < headers.length; i++) {
            if (headers[i] != null) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleHeader);
            }
        }

        // Data rows
        int no = 1;
        for (Map.Entry<String, List<Absensi>> entry : absensiByUser.entrySet()) {
            String username = entry.getKey();
            List<Absensi> userAbsensi = entry.getValue();

            Row dataRow = sheet.createRow(rowNum++);

            // No
            Cell cellNo = dataRow.createCell(0);
            cellNo.setCellValue(no++);
            cellNo.setCellStyle(styleData);

            // Nama
            Cell cellNama = dataRow.createCell(1);
            cellNama.setCellValue(username);
            cellNama.setCellStyle(styleData);

            // Kehadiran per tanggal
            int totalKehadiran = 0;
            for (int i = 1; i <= maxTanggal; i++) {
                final int tanggal = i; // Declare as final to be used in lambda expression
                Cell cellTanggal = dataRow.createCell(i + 1);

                // Find absensi for this date
                boolean isPresent = userAbsensi.stream()
                        .anyMatch(absensi -> {
                            LocalDate localDate = absensi.getTanggalAbsen().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            return localDate.getDayOfMonth() == tanggal; // Use the final variable
                        });

                if (isPresent) {
                    totalKehadiran++;
                    cellTanggal.setCellValue("✓");
                } else {
                    cellTanggal.setCellValue("");
                }
                cellTanggal.setCellStyle(styleData);
            }
            // Calculate the percentage attendance and display it
            double percentage = (double) totalKehadiran / maxTanggal * 100;
            Cell cellPercentage = dataRow.createCell(maxTanggal + 3);
            cellPercentage.setCellValue(String.format("%.2f %%", percentage)); // Display percentage with 2 decimal places and the '%' symbol
            cellPercentage.setCellStyle(styleData);

            // Total Kehadiran
            Cell cellTotal = dataRow.createCell(maxTanggal + 2); // Adjusted column for total attendance
            cellTotal.setCellValue(totalKehadiran);
            cellTotal.setCellStyle(styleTotal);
        }

        // Add row for total attendance in column B
        Row totalRow = sheet.createRow(rowNum++);  // Create the row for totals
        Cell totalCell = totalRow.createCell(0);  // Cell 0 for "Total"
        totalCell.setCellValue("TOTAL KESELURUHAN");  // Set the label
        totalCell.setCellStyle(styleTitle);  // Apply title style

        // Total values and formatting for the row
        for (int i = 1; i <= maxTanggal + 2; i++) {
            Cell cell = totalRow.createCell(i);
            cell.setCellValue(" ");  // You can calculate and set the total value here if needed
            cell.setCellStyle(styleTotal);  // Apply style for totals
        }

        // Calculate total attendance for each day and display in each respective column
        for (int i = 1; i <= maxTanggal; i++) {
            final int tanggal = i;
            int totalForDay = 0;
            boolean allAbsent = true;
            for (List<Absensi> userAbsensi : absensiByUser.values()) {
                // Count attendance for this date
                boolean isPresent = userAbsensi.stream()
                        .anyMatch(absensi -> {
                            LocalDate localDate = absensi.getTanggalAbsen().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            return localDate.getDayOfMonth() == tanggal;
                        });
                if (isPresent) {
                    totalForDay++;
                    allAbsent = false;
                }
            }

            // Add the total attendance for this day to the total row in the respective column
            Cell cellForDay = totalRow.createCell(i + 1);
            cellForDay.setCellValue(totalForDay);
            cellForDay.setCellStyle(styleTotal);

            // If all users are absent on this date, apply orange color to the column and row
            if (allAbsent) {
                for (int j = 1; j <= absensiByUser.size(); j++) {
                    // Highlight the column for this date
                    sheet.getRow(j).getCell(i + 1).setCellStyle(styleOrange);
                }
                // Highlight the total column for this date
                totalRow.getCell(i + 1).setCellStyle(styleOrange);
            }
        }

        // Set column width based on content
        for (int i = 0; i <= maxTanggal + 3; i++) {
            sheet.autoSizeColumn(i);  // Auto-size columns with data
        }

        // Set a fixed width for empty columns if needed
        sheet.setColumnWidth(maxTanggal + 3, 4000); // Example for fixed width for the percentage column

        // Write the workbook to the HTTP response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=absensi_guru.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void exportOrganisasi(Long idAdmin, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DATA ORGANISASI");

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


        List<Organisasi> organisasiList = organisasiRepository.findByAdmin(idAdmin);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA ORGANISASI");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 5)); // Merging cells for title
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Admin", "Nama Organisasi", "Alamat", "Telepon", "Email"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int userRowNum = 1;
        for (Organisasi organisasi : organisasiList) {
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(userRowNum++);
            cell0.setCellStyle(styleCenterNumber); // Use the centered number style

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(organisasi.getAdmin().getUsername());
            cell1.setCellStyle(styleCenterNumber);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(organisasi.getNamaOrganisasi());
            cell2.setCellStyle(styleCenterNumber);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(organisasi.getAlamat());
            cell3.setCellStyle(styleCenterNumber);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(organisasi.getNomerTelepon());
            cell4.setCellStyle(styleCenterNumber);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(organisasi.getEmailOrganisasi());
            cell5.setCellStyle(styleCenterNumber);
        }

        // Adjust column width
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ExportOrganisasi.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void importOrganisasi(MultipartFile file, Admin admin) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);  // Mengambil sheet pertama

        List<Organisasi> organisasiList = new ArrayList<>();
        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Organisasi organisasi = new Organisasi();


                Cell namaOrganisasiCell = row.getCell(2);
                Cell alamatCell = row.getCell(3);
                Cell noTeleponCell = row.getCell(4);
                Cell emailCell = row.getCell(5);


                if (namaOrganisasiCell != null) {
                    organisasi.setNamaOrganisasi(getCellValue(namaOrganisasiCell));
                }

                if (alamatCell != null) {
                    organisasi.setAlamat(getCellValue(alamatCell));
                }

                if (noTeleponCell != null) {
                    organisasi.setNomerTelepon(getCellValue(noTeleponCell));
                }

                if (emailCell != null) {
                    organisasi.setEmailOrganisasi(getCellValue(emailCell));
                }

                organisasi.setAdmin(admin);


                organisasiList.add(organisasi);
            }
        }

        organisasiRepository.saveAll(organisasiList);
        workbook.close();
    }

    public static void downloadTemplateImportOrganisasi(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Organisasi");

        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);

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
        titleCell.setCellValue("DATA ORGANISASI");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2)); // Merging cells for title
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Nama Organisasi", "Alamat", "Telepon", "Email"};
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
        response.setHeader("Content-Disposition", "attachment; filename=TemplateOrganisasi.xlsx");

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }

        workbook.close();
    }


    //    Kelasss
    public void exportKelas(Long idAdmin, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DATA KELAS");

        // Cell styles
        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.LEFT);  // Align header to the left
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());  // Set font color to white
        styleHeader.setFont(headerFont);

        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.LEFT);  // Align title to the left
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);

        CellStyle styleLeftAlign = workbook.createCellStyle();
        styleLeftAlign.setAlignment(HorizontalAlignment.LEFT);  // Align data to the left
        styleLeftAlign.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLeftAlign.setBorderTop(BorderStyle.THIN);
        styleLeftAlign.setBorderRight(BorderStyle.THIN);
        styleLeftAlign.setBorderBottom(BorderStyle.THIN);
        styleLeftAlign.setBorderLeft(BorderStyle.THIN);

        List<Kelas> kelasList = kelasRepository.findByIdAdmin(idAdmin);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA KELAS");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2)); // Merging cells for title
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama Kelas", "Organisasi"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int userRowNum = 1;
        for (Kelas kelas : kelasList) {
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(userRowNum++);
            cell0.setCellStyle(styleLeftAlign); // Use the left-aligned style

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(kelas.getNamaKelas());
            cell1.setCellStyle(styleLeftAlign);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(kelas.getOrganisasi().getNamaOrganisasi());
            cell2.setCellStyle(styleLeftAlign);
        }

        // Adjust column width
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ExportKelas.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void importKelas(MultipartFile file, Admin admin) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);  // Mengambil sheet pertama

        List<Kelas> kelasList = new ArrayList<>();
        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell firstCell = row.getCell(0);
                if (firstCell != null && "No".equalsIgnoreCase(getCellValue(firstCell))) {
                    continue;
                }

                Kelas kelas = new Kelas();

                Cell namaKelasCell = row.getCell(1);
                Cell organisasiCell = row.getCell(2);

                if (namaKelasCell != null) {
                    boolean namekelasExisting = kelasRepository.existsByNamaKelas(String.valueOf(namaKelasCell));
                    if (namekelasExisting) {
                        throw new NotFoundException("nama kelas sudah terdaftar");
                    }
                    kelas.setNamaKelas(getCellValue(namaKelasCell));
                }

                if (organisasiCell != null) {
                    String namaOrganisasi = getCellValue(organisasiCell);
                    Organisasi organisasi = organisasiRepository.findByNamaOrganisasi(namaOrganisasi)
                            .orElseThrow(() -> new NotFoundException("Organisasi dengan nama " + namaOrganisasi + " tidak ditemukan"));
                    kelas.setOrganisasi(organisasi);
//                    System.out.println("ID Organisasi yang di-set: " + organisasi.getId());

                }

                kelas.setAdmin(admin);
                kelasList.add(kelas);
            }
        }

        kelasRepository.saveAll(kelasList);
        workbook.close();
    }

    public static void downloadTemplateImportKelas(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Kelas");

        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.LEFT);
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
        styleHeader.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());  // Set font color to white
        styleHeader.setFont(headerFont);

        CellStyle styleNote = workbook.createCellStyle();
        styleNote.setAlignment(HorizontalAlignment.LEFT);
        styleNote.setVerticalAlignment(VerticalAlignment.CENTER);

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA KELAS");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2)); // Merging cells for title
        rowNum++;

        // Note row
        Row noteRow = sheet.createRow(rowNum++);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("Catatan: Jangan berikan spasi pada awal kata saat mengisi data.");
        noteCell.setCellStyle(styleNote);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2)); // Merging cells for note
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama Kelas", "Organisasi"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Adjust column width for header and note
        sheet.setColumnWidth(0, 4000); // Adjust the width of the "No" column
        sheet.setColumnWidth(1, 8000); // Adjust the width of the "Nama Kelas" column
        sheet.setColumnWidth(2, 8000); // Adjust the width of the "Organisasi" column

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=TemplateKelas.xlsx");

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }

        workbook.close();
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

}
