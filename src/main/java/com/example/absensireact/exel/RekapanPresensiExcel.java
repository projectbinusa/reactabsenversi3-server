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
import java.util.stream.Collectors;


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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tanggal);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        // Fetch all users in the class
        List<UserModel> allUsers = absensiRepository.findAllUsersByKelasId(kelasId);

        // Fetch absences for the specific date
        List<Absensi> absensiList = absensiRepository.findByKelasIdAndDate(kelasId, day, month, year);

        // Map absences by user ID for quick lookup
        Map<Long, List<Absensi>> absensiMap = absensiList.stream()
                .collect(Collectors.groupingBy(absensi -> absensi.getUser().getId()));

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA PRESENSI HARIAN : " + day + " " + getMonthName(month) + " " + year);
        titleCell.setCellStyle(styleHeader);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8));
        applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8), workbook);
        rowNum++;

        // Header row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama", "Kelas", "Presensi"};
        for (int i = 0; i < 3; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum, 0, 2));
        applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum, 0, 2), workbook);

        Cell presensiCell = headerRow.createCell(3);
        presensiCell.setCellValue(headers[3]);
        presensiCell.setCellStyle(styleHeader);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 8));
        applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 8), workbook);

        Row presensiRow = sheet.createRow(rowNum++);
        String[] presensiHeaders = {"Hadir", "Izin", "Terlambat", "Izin Tengah Hari", "Alpha", "Sakit"};
        for (int i = 0; i < presensiHeaders.length; i++) {
            Cell cell = presensiRow.createCell(3 + i);
            cell.setCellValue(presensiHeaders[i]);
            cell.setCellStyle(styleHeader);
        }

        // Counters for summary row
        int totalHadir = 0, totalIzin = 0, totalTerlambat = 0, totalIzinTengahHari = 0, totalAlpha = 0, totalSakit = 0;

        // Data rows
        int no = 1;
        for (UserModel user : allUsers) {
            Row dataRow = sheet.createRow(rowNum++);

            // User data
            Cell noCell = dataRow.createCell(0);
            noCell.setCellValue(no++);
            noCell.setCellStyle(styleData);

            Cell namaCell = dataRow.createCell(1);
            namaCell.setCellValue(user.getUsername());
            namaCell.setCellStyle(styleData);

            Cell kelasCell = dataRow.createCell(2);
            kelasCell.setCellValue(user.getKelas() != null ? user.getKelas().getNamaKelas() : "Unknown");
            kelasCell.setCellStyle(styleData);

            // Absence data
            List<Absensi> userAbsensi = absensiMap.getOrDefault(user.getId(), Collections.emptyList());
            boolean hadir = userAbsensi.stream().anyMatch(a -> "Hadir".equals(a.getStatusAbsen()));
            boolean izin = userAbsensi.stream().anyMatch(a -> "Izin".equals(a.getStatusAbsen()));
            boolean terlambat = userAbsensi.stream().anyMatch(a -> "Terlambat".equals(a.getStatusAbsen()));
            boolean izinTengahHari = userAbsensi.stream().anyMatch(a -> "Izin Tengah Hari".equals(a.getStatusAbsen()));
            boolean alpha = userAbsensi.isEmpty();
            boolean sakit = userAbsensi.stream().anyMatch(a -> "Sakit".equals(a.getStatusAbsen()));

            // Fill attendance columns and update totals
            Cell hadirCell = dataRow.createCell(3);
            if (hadir) {
                hadirCell.setCellValue("✔");
                totalHadir++;
            }
            hadirCell.setCellStyle(styleData);

            Cell izinCell = dataRow.createCell(4);
            if (izin) {
                izinCell.setCellValue("✔");
                totalIzin++;
            }
            izinCell.setCellStyle(styleData);

            Cell terlambatCell = dataRow.createCell(5);
            if (terlambat) {
                terlambatCell.setCellValue("✔");
                totalTerlambat++;
            }
            terlambatCell.setCellStyle(styleData);

            Cell izinTengahHariCell = dataRow.createCell(6);
            if (izinTengahHari) {
                izinTengahHariCell.setCellValue("✔");
                totalIzinTengahHari++;
            }
            izinTengahHariCell.setCellStyle(styleData);

            Cell alphaCell = dataRow.createCell(7);
            if (alpha) {
                alphaCell.setCellValue("✔");
                totalAlpha++;
            }
            alphaCell.setCellStyle(styleData);

            Cell sakitCell = dataRow.createCell(8);
            if (sakit) {
                sakitCell.setCellValue("✔");
                totalSakit++;
            }
            sakitCell.setCellStyle(styleData);
        }

        // Add summary row
        Row jumlahRow = sheet.createRow(rowNum++);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
        applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2), workbook);

        Cell jumlahLabelCell = jumlahRow.createCell(0);
        jumlahLabelCell.setCellValue("Jumlah");
        jumlahLabelCell.setCellStyle(styleHeader);

        jumlahRow.createCell(3).setCellValue(totalHadir);
        jumlahRow.createCell(4).setCellValue(totalIzin);
        jumlahRow.createCell(5).setCellValue(totalTerlambat);
        jumlahRow.createCell(6).setCellValue(totalIzinTengahHari);
        jumlahRow.createCell(7).setCellValue(totalAlpha);
        jumlahRow.createCell(8).setCellValue(totalSakit);

        for (int i = 3; i <= 8; i++) {
            Cell cell = jumlahRow.getCell(i);
            if (cell == null) {
                cell = jumlahRow.createCell(i);
            }
            cell.setCellStyle(styleData);
        }

        // Adjust column widths
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }

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
