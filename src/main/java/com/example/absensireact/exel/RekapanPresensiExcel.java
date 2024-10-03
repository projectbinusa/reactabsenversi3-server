package com.example.absensireact.exel;

import com.example.absensireact.model.Absensi;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AbsensiRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RekapanPresensiExcel {

    @Autowired
    private AbsensiRepository absensiRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private String getMonthName(int month) {
        String[] monthNames = new DateFormatSymbols().getMonths();
        int index = month - 1;
        if (index >= 0 && index < monthNames.length) {
            return monthNames[index];
        }
        return "Bulan Tidak Valid";
    }

    public void excelAbsensiHarianByKelas(Date tanggal, Long kelasId, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Presensi-harian");

        // Define cell styles
        CellStyle styleHeader = createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), true);
        CellStyle styleData = createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), false);
        CellStyle styleCheckmark = createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), false);

        Font fontCheckmark = workbook.createFont();
        fontCheckmark.setFontHeightInPoints((short) 12);
        styleCheckmark.setFont(fontCheckmark);

        String checkmark = "\u2714"; // Unicode for checkmark
        String empty = ""; // Empty cell for unchecked

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tanggal);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        List<Absensi> absensiList = absensiRepository.findByKelasIdAndDate(kelasId, day, month, year);

        // Map to get user by username
        Map<String, UserModel> userMap = new HashMap<>();
        for (Absensi absensi : absensiList) {
            UserModel user = absensi.getUser();
            userMap.put(user.getUsername(), user);
        }

        Map<String, List<Absensi>> userAbsensiMap = new HashMap<>();
        for (Absensi absensi : absensiList) {
            userAbsensiMap.computeIfAbsent(absensi.getUser().getUsername(), k -> new ArrayList<>()).add(absensi);
        }

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA PRESENSI HARIAN : " + day + " " + getMonthName(month) + " " + year);
        titleCell.setCellStyle(styleHeader);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8)); // Merging cells for title
        applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8), workbook); // Apply border for merged cells
        rowNum++;

        // Header row (first row for "No", "Nama", "Kelas", and "Presensi")
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama", "Kelas", "Presensi"};
        for (int i = 0; i < 3; i++) { // Loop for "No", "Nama", "Kelas" columns
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

// Merge "No", "Nama", and "Kelas" columns from row 3 to 4
        for (int i = 0; i < 3; i++) {
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum, i, i)); // Merge columns 0 (No), 1 (Nama), and 2 (Kelas)
            applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum, i, i), workbook); // Apply border to merged cells
        }

// Add "Presensi" header with merged cells
        Cell presensiCell = headerRow.createCell(3);
        presensiCell.setCellValue(headers[3]); // Set "Presensi" header
        presensiCell.setCellStyle(styleHeader);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 8)); // Merge Presensi cell for sub-columns
        applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 8), workbook); // Apply border for merged cells

// Second row for Presensi sub-columns (Hadir, Izin, Terlambat, Izin Tengah Hari, Alpha)
        Row presensiRow = sheet.createRow(rowNum++);
        String[] presensiHeaders = {"Hadir", "Izin", "Terlambat", "Izin Tengah Hari", "Alpha" , "Sakit"};
        for (int i = 0; i < presensiHeaders.length; i++) {
            Cell cell = presensiRow.createCell(3 + i); // Starting from 4th column for sub-columns
            cell.setCellValue(presensiHeaders[i]);
            cell.setCellStyle(styleHeader);
        }

// Apply border to the entire row 4
        for (int i = 0; i < 9; i++) { // Ensure borders are applied for the entire row (including merged cells)
            Cell cell = presensiRow.getCell(i);
            if (cell == null) { // Create empty cells where necessary
                cell = presensiRow.createCell(i);
            }
            cell.setCellStyle(styleHeader); // Apply border style to every cell in row 4
        }


        // Initialize counters for summary row (Jumlah)
        int totalHadir = 0;
        int totalIzin = 0;
        int totalTerlambat = 0;
        int totalIzinTengahHari = 0;
        int totalTidakMasuk = 0;
        int totalSakit= 0;
        int totalAlpha = 0 ;

        // Data rows
        int no = 1; // Initialize row number for "No" column
        for (Map.Entry<String, UserModel> entry : userMap.entrySet()) {
            String userName = entry.getKey();
            UserModel userModel = entry.getValue();
            List<Absensi> userAbsensi = userAbsensiMap.getOrDefault(userName, new ArrayList<>());

            // Retrieve kelas from User
            String kelas = userModel.getKelas() != null ? userModel.getKelas().getNamaKelas() : "Unknown";

            // Initialize variables for status
            boolean hadir = false;
            boolean izin = false;
            boolean terlambat = false;
            boolean izinTengahHari = false;
            boolean sakit = false;
            boolean alpha = false ;

            // Determine status based on status_absen
            for (Absensi absensi : userAbsensi) {
                String status = absensi.getStatusAbsen();
                if ("Hadir".equals(status) || "Izin Tengah Hari".equals(status) || "Lebih Awal".equals(status) || "Terlambat".equals(status)) {
                    hadir = true;
                }
                if ("Izin".equals(status)) {
                    izin = true;
                }
                if ("Terlambat".equals(status)) {
                    terlambat = true;
                }
                if ("Izin Tengah Hari".equals(status)) {
                    izinTengahHari = true;
                }
                if ("Izin".equals(status)) {
                    sakit = true;
                }
                if ("Alpha".equals(status)) {
                    alpha = true;
                }
            }

            // If no attendance record found for user, mark as "Tidak Masuk"
            boolean tidakMasuk = userAbsensi.isEmpty();

            // Add data row
            Row dataRow = sheet.createRow(rowNum++);

            // "No" column
            Cell noCell = dataRow.createCell(0);
            noCell.setCellValue(no++);
            noCell.setCellStyle(styleData);  // Apply border style to "No" cell

            // "Nama" column
            Cell namaCell = dataRow.createCell(1);
            namaCell.setCellValue(userName);
            namaCell.setCellStyle(styleData);  // Apply border style to "Nama" cell

            // "Kelas" column
            Cell kelasCell = dataRow.createCell(2);
            kelasCell.setCellValue(kelas);
            kelasCell.setCellStyle(styleData);  // Apply border style to "Kelas" cell

            // "Presensi" sub-columns
            Cell hadirCell = dataRow.createCell(3);
            hadirCell.setCellValue(hadir ? checkmark : empty); // Hadir
            hadirCell.setCellStyle(styleData); // Apply border style to "Hadir" cell
            if (hadir) totalHadir++;

            Cell izinCell = dataRow.createCell(4);
            izinCell.setCellValue(izin ? checkmark : empty); // Izin
            izinCell.setCellStyle(styleData); // Apply border style to "Izin" cell
            if (izin) totalIzin++;

            Cell terlambatCell = dataRow.createCell(5);
            terlambatCell.setCellValue(terlambat ? checkmark : empty); // Terlambat
            terlambatCell.setCellStyle(styleData); // Apply border style to "Terlambat" cell
            if (terlambat) totalTerlambat++;

            Cell izinTengahHariCell = dataRow.createCell(6);
            izinTengahHariCell.setCellValue(izinTengahHari ? checkmark : empty); // Izin Tengah Hari
            izinTengahHariCell.setCellStyle(styleData); // Apply border style to "Izin Tengah Hari" cell
            if (izinTengahHari) totalIzinTengahHari++;

            Cell alphaCell = dataRow.createCell(7);
            alphaCell.setCellValue(alpha ? checkmark : empty); // Tidak Masuk
            alphaCell.setCellStyle(styleData); // Apply border style to "Tidak Masuk" cell
            if (alpha) totalAlpha++;

            Cell sakitCell = dataRow.createCell(8);
            sakitCell.setCellValue(sakit ? checkmark : empty); // Tidak Masuk
            sakitCell.setCellStyle(styleData); // Apply border style to "Tidak Masuk" cell
            if (sakit) totalSakit++;
        }

// Add summary row (Jumlah)
        Row jumlahRow = sheet.createRow(rowNum++);

// Merge cells for "Jumlah" label (from column 0 to column 2)
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
        applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2), workbook);

// Set "Jumlah" label in the merged cells
        Cell jumlahLabelCell = jumlahRow.createCell(0); // Now in column 0 after merging
        jumlahLabelCell.setCellValue("Jumlah");
        jumlahLabelCell.setCellStyle(styleHeader);

// Set total values for "Hadir", "Izin", "Terlambat", "Izin Tengah Hari", and "Tidak Masuk"
        jumlahRow.createCell(3).setCellValue(totalHadir); // Total Hadir
        jumlahRow.createCell(4).setCellValue(totalIzin); // Total Izin
        jumlahRow.createCell(5).setCellValue(totalTerlambat); // Total Terlambat
        jumlahRow.createCell(6).setCellValue(totalIzinTengahHari); // Total Izin Tengah Hari
        jumlahRow.createCell(7).setCellValue(totalTidakMasuk); // Total Tidak Masuk
        jumlahRow.createCell(8).setCellValue(totalSakit); // Total Tidak Masuk

// Apply style to summary row cells (total values)
        for (int i = 3; i <= 8; i++) {
            Cell cell = jumlahRow.getCell(i);
            if (cell == null) {
                cell = jumlahRow.createCell(i); // Create cell if not exist
            }
            cell.setCellStyle(styleData); // Apply data style with borders
        }

// Adjust borders for the merged "Jumlah" label
        applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2), workbook);


// Adjust column widths
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }

        // Adjust column widths
        sheet.setColumnWidth(0, 2000); // No column width
        sheet.setColumnWidth(1, 4000); // Nama column width
        sheet.setColumnWidth(2, 4000); // Kelas column width
        sheet.setColumnWidth(3, 4000); // Hadir column width
        sheet.setColumnWidth(4, 4000); // Izin column width
        sheet.setColumnWidth(5, 4000); // Terlambat column width
        sheet.setColumnWidth(6, 4000); // Izin Tengah Hari column width
        sheet.setColumnWidth(7, 4000); // Tidak Masuk column width
        sheet.setColumnWidth(8, 4000); // Tidak Masuk column width

        // Write output to response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=PresensiHarian.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }




    // Apply border to merged cells
    private void applyBorderToMergedCells(Sheet sheet, CellRangeAddress region, Workbook workbook) {
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
    }

    private CellStyle createCellStyle(Workbook workbook, HorizontalAlignment hAlign, VerticalAlignment vAlign, BorderStyle borderStyle, short fontColor, boolean isBold) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(hAlign);
        style.setVerticalAlignment(vAlign);
        style.setBorderTop(borderStyle);
        style.setBorderRight(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        Font font = workbook.createFont();
        font.setColor(fontColor);
        if (isBold) {
            font.setBold(true);
        }
        style.setFont(font);
        return style;
    }



    public void excelAbsensiByKelas(Long kelasId, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Presensi-harian");

        Font fontWhite = workbook.createFont();
        fontWhite.setColor(IndexedColors.WHITE.getIndex());

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

        CellStyle styleNumber = workbook.createCellStyle();
        styleNumber.setAlignment(HorizontalAlignment.CENTER);
        styleNumber.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNumber.setBorderTop(BorderStyle.THIN);
        styleNumber.setBorderRight(BorderStyle.THIN);
        styleNumber.setBorderBottom(BorderStyle.THIN);
        styleNumber.setBorderLeft(BorderStyle.THIN);
        styleNumber.setFont(fontWhite);

        CellStyle styleCenterNumber = workbook.createCellStyle();
        styleCenterNumber.setAlignment(HorizontalAlignment.CENTER);
        styleCenterNumber.setVerticalAlignment(VerticalAlignment.CENTER);
        styleCenterNumber.setBorderTop(BorderStyle.THIN);
        styleCenterNumber.setBorderRight(BorderStyle.THIN);
        styleCenterNumber.setBorderBottom(BorderStyle.THIN);
        styleCenterNumber.setBorderLeft(BorderStyle.THIN);

        // Conditional formatting colors
        CellStyle styleColorLate = workbook.createCellStyle();
        styleColorLate.cloneStyleFrom(styleNumber);
        styleColorLate.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleColorLate.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleColorPermission = workbook.createCellStyle();
        styleColorPermission.cloneStyleFrom(styleNumber);
        styleColorPermission.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
        styleColorPermission.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleColorEarly = workbook.createCellStyle();
        styleColorEarly.cloneStyleFrom(styleNumber);
        styleColorEarly.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        styleColorEarly.setFillPattern(FillPatternType.SOLID_FOREGROUND);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(tanggal);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
//        int year = calendar.get(Calendar.YEAR);

        List<Absensi> absensiList = absensiRepository.findAbsensiByKelasId(kelasId);

        Map<String, List<Absensi>> userAbsensiMap = new HashMap<>();
        for (Absensi absensi : absensiList) {
            userAbsensiMap.computeIfAbsent(absensi.getUser().getUsername(), k -> new ArrayList<>()).add(absensi);
        }

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA PRESSENSI PERKELAS");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4)); // Merging cells for title
        rowNum++;

        for (Map.Entry<String, List<Absensi>> entry : userAbsensiMap.entrySet()) {
            String userName = entry.getKey();
            List<Absensi> userAbsensi = entry.getValue();
//            String position = userAbsensi.get(0).getUser().getJabatan().getNamaJabatan();

            // Variables to count absences for each user
            int userTotalLate = 0;
            int userTotalPermission = 0;
            int userTotalEarly = 0;

            // Name and Position row
            Row nameRow = sheet.createRow(rowNum++);
            Cell nameCell = nameRow.createCell(0);
            nameCell.setCellValue("Nama :  " + userName);
            nameCell.setCellStyle(styleHeader);

            Row positionRow = sheet.createRow(rowNum++);
            Cell positionCell = positionRow.createCell(0);
//            positionCell.setCellValue("Jabatan :   " + position);
            positionCell.setCellStyle(styleHeader);

            // Add a blank row between header and data
            rowNum++;

            // Header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"No", "Tanggal Absen", "Jam Masuk", "Jam Pulang", "Keterangan"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleHeader);
            }

            // Data rows
            int userRowNum = 1;
            for (Absensi absensi : userAbsensi) {
                Row row = sheet.createRow(rowNum++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(userRowNum++);
                cell0.setCellStyle(styleCenterNumber); // Use the centered number style

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(absensi.getTanggalAbsen()));
                cell1.setCellStyle(styleNumber);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(absensi.getJamMasuk());
                cell2.setCellStyle(styleNumber);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(absensi.getJamPulang());
                cell3.setCellStyle(styleNumber);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(absensi.getStatusAbsen());
                cell4.setCellStyle(styleNumber);

                CellStyle styleColor = null;
                switch (absensi.getStatusAbsen()) {
                    case "Terlambat":
                        styleColor = styleColorLate;
                        userTotalLate++; // Increment late count
                        break;
                    case "Izin":
                        styleColor = styleColorPermission;
                        userTotalPermission++; // Increment permission count
                        break;
                    case "Lebih Awal":
                        styleColor = styleColorEarly;
                        userTotalEarly++; // Increment early count
                        break;
                }
                if (styleColor != null) {
                    for (int i = 0; i < headers.length; i++) {
                        row.getCell(i).setCellStyle(styleColor);
                    }
                }
            }

            // Adding summary row for each user
            Row lateRow = sheet.createRow(rowNum++);
            Cell lateCell = lateRow.createCell(0);
            lateCell.setCellValue("Terlambat: " + userTotalLate);
            lateCell.setCellStyle(styleHeader);

            Row permissionRow = sheet.createRow(rowNum++);
            Cell permissionCell = permissionRow.createCell(0);
            permissionCell.setCellValue("Izin: " + userTotalPermission);
            permissionCell.setCellStyle(styleHeader);

            Row earlyRow = sheet.createRow(rowNum++);
            Cell earlyCell = earlyRow.createCell(0);
            earlyCell.setCellValue("Lebih Awal: " + userTotalEarly);
            earlyCell.setCellStyle(styleHeader);

            // Add a blank row between each user's table for readability
            rowNum++;
        }

        // Adjust column width outside the loop for better performance
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=PresensiPerkelas.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

//    public void excelAbsensiBulananByKelas(Long kelasId, int month, int year, HttpServletResponse response) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Absensi-Bulanan");
//
//        Font fontWhite = workbook.createFont();
//        fontWhite.setColor(IndexedColors.WHITE.getIndex()); // Set font color to white
//
//        // Cell styles
//        CellStyle styleHeader = workbook.createCellStyle();
//        styleHeader.setAlignment(HorizontalAlignment.CENTER);
//        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
//        styleHeader.setBorderTop(BorderStyle.THIN);
//        styleHeader.setBorderRight(BorderStyle.THIN);
//        styleHeader.setBorderBottom(BorderStyle.THIN);
//        styleHeader.setBorderLeft(BorderStyle.THIN);
//
//        CellStyle styleTitle = workbook.createCellStyle();
//        styleTitle.setAlignment(HorizontalAlignment.CENTER);
//        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
//        Font titleFont = workbook.createFont();
//        titleFont.setBold(true);
//        styleTitle.setFont(titleFont);
//
//        CellStyle styleNumber = workbook.createCellStyle();
//        styleNumber.setAlignment(HorizontalAlignment.CENTER);
//        styleNumber.setVerticalAlignment(VerticalAlignment.CENTER);
//        styleNumber.setBorderTop(BorderStyle.THIN);
//        styleNumber.setBorderRight(BorderStyle.THIN);
//        styleNumber.setBorderBottom(BorderStyle.THIN);
//        styleNumber.setBorderLeft(BorderStyle.THIN);
//        styleNumber.setFont(fontWhite);
//
//        CellStyle styleCenterNumber = workbook.createCellStyle();
//        styleCenterNumber.setAlignment(HorizontalAlignment.CENTER);
//        styleCenterNumber.setVerticalAlignment(VerticalAlignment.CENTER);
//        styleCenterNumber.setBorderTop(BorderStyle.THIN);
//        styleCenterNumber.setBorderRight(BorderStyle.THIN);
//        styleCenterNumber.setBorderBottom(BorderStyle.THIN);
//        styleCenterNumber.setBorderLeft(BorderStyle.THIN);
//
//        // Conditional formatting colors
//        CellStyle styleColorLate = workbook.createCellStyle();
//        styleColorLate.cloneStyleFrom(styleNumber);
//        styleColorLate.setFillForegroundColor(IndexedColors.RED.getIndex());
//        styleColorLate.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//        CellStyle styleColorPermission = workbook.createCellStyle();
//        styleColorPermission.cloneStyleFrom(styleNumber);
//        styleColorPermission.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
//        styleColorPermission.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//        CellStyle styleColorEarly = workbook.createCellStyle();
//        styleColorEarly.cloneStyleFrom(styleNumber);
//        styleColorEarly.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//        styleColorEarly.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//        // Fetch data
//        List<Absensi> absensiList = absensiRepository.findByKelasIdAndBulan(kelasId, month, year);
//
//        if (absensiList.isEmpty()) {
//            // Handle case when there are no absences for the given month and year
//            Row emptyRow = sheet.createRow(0);
//            Cell emptyCell = emptyRow.createCell(0);
//            emptyCell.setCellValue("Tidak ada data absensi untuk bulan " + getMonthName(month) + " tahun " + year);
//            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4)); // Merge cells for message
//        } else {
//            // Group by user
//            Map<String, List<Absensi>> userAbsensiMap = new HashMap<>();
//            for (Absensi absensi : absensiList) {
//                userAbsensiMap.computeIfAbsent(absensi.getUser().getUsername(), k -> new ArrayList<>()).add(absensi);
//            }
//
//            int rowNum = 0;
//
//            // Title row
//            Row titleRow = sheet.createRow(rowNum++);
//            Cell titleCell = titleRow.createCell(0);
//            titleCell.setCellValue("DATA ABSENSI BULAN : " + getMonthName(month) + " - " + year);
//            titleCell.setCellStyle(styleTitle);
//            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4)); // Merging cells for title
//            rowNum++;
//
//            for (Map.Entry<String, List<Absensi>> entry : userAbsensiMap.entrySet()) {
//                String userName = entry.getKey();
//                List<Absensi> userAbsensi = entry.getValue();
//                // String position = userAbsensi.get(0).getUser().getJabatan().getNamaJabatan();
//
//                // Variables to count absences for each user
//                int userTotalLate = 0;
//                int userTotalPermission = 0;
//                int userTotalEarly = 0;
//
//                // Name and Position row
//                Row nameRow = sheet.createRow(rowNum++);
//                Cell nameCell = nameRow.createCell(0);
//                nameCell.setCellValue("Nama :  " + userName);
//                nameCell.setCellStyle(styleHeader);
//
//                Row positionRow = sheet.createRow(rowNum++);
//                Cell positionCell = positionRow.createCell(0);
//                // positionCell.setCellValue("Jabatan :   " + position);
//                positionCell.setCellStyle(styleHeader);
//                rowNum++;
//
//                // Header row
//                Row headerRow = sheet.createRow(rowNum++);
//                String[] headers = {"No", "Tanggal Absen", "Jam Masuk", "Jam Pulang", "Keterangan"};
//                for (int i = 0; i < headers.length; i++) {
//                    Cell cell = headerRow.createCell(i);
//                    cell.setCellValue(headers[i]);
//                    cell.setCellStyle(styleHeader);
//                }
//
//                // Data rows
//                int userRowNum = 1;
//                for (Absensi absensi : userAbsensi) {
//                    Row row = sheet.createRow(rowNum++);
//                    Cell cell0 = row.createCell(0);
//                    cell0.setCellValue(userRowNum++);
//                    cell0.setCellStyle(styleCenterNumber); // Use the centered number style
//
//                    Cell cell1 = row.createCell(1);
//                    cell1.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(absensi.getTanggalAbsen()));
//                    cell1.setCellStyle(styleNumber);
//
//                    Cell cell2 = row.createCell(2);
//                    cell2.setCellValue(absensi.getJamMasuk());
//                    cell2.setCellStyle(styleNumber);
//
//                    Cell cell3 = row.createCell(3);
//                    cell3.setCellValue(absensi.getJamPulang());
//                    cell3.setCellStyle(styleNumber);
//
//                    Cell cell4 = row.createCell(4);
//                    cell4.setCellValue(absensi.getStatusAbsen());
//                    cell4.setCellStyle(styleNumber);
//
//                    CellStyle styleColor = null;
//                    switch (absensi.getStatusAbsen()) {
//                        case "Terlambat":
//                            styleColor = styleColorLate;
//                            userTotalLate++; // Increment late count
//                            break;
//                        case "Izin":
//                            styleColor = styleColorPermission;
//                            userTotalPermission++; // Increment permission count
//                            break;
//                        case "Lebih Awal":
//                            styleColor = styleColorEarly;
//                            userTotalEarly++; // Increment early count
//                            break;
//                    }
//                    if (styleColor != null) {
//                        for (int i = 0; i < headers.length; i++) {
//                            row.getCell(i).setCellStyle(styleColor);
//                        }
//                    }
//                }
//
//                // Adding summary row for each user
//                Row lateRow = sheet.createRow(rowNum++);
//                Cell lateCell = lateRow.createCell(0);
//                lateCell.setCellValue("Terlambat: " + userTotalLate);
//                lateCell.setCellStyle(styleHeader);
//
//                Row permissionRow = sheet.createRow(rowNum++);
//                Cell permissionCell = permissionRow.createCell(0);
//                permissionCell.setCellValue("Izin: " + userTotalPermission);
//                permissionCell.setCellStyle(styleHeader);
//
//                Row earlyRow = sheet.createRow(rowNum++);
//                Cell earlyCell = earlyRow.createCell(0);
//                earlyCell.setCellValue("Lebih Awal: " + userTotalEarly);
//                earlyCell.setCellStyle(styleHeader);
//
//                // Add a blank row between each user's table for readability
//                rowNum++;
//            }
//        }
//
//        for (int i = 0; i < 5; i++) {
//            sheet.autoSizeColumn(i);
//        }
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=AbsensiBulanan_" + getMonthName(month) + "_" + year + ".xlsx");
//        workbook.write(response.getOutputStream());
//        workbook.close();
//    }

}
