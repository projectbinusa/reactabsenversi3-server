package com.example.absensireact.exel;

import com.example.absensireact.model.Absensi;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AbsensiRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelAbsnesiBulanan {
    @Autowired
    private AbsensiRepository absensiRepository;

    private String getMonthName(int month) {
        String[] monthNames = new DateFormatSymbols().getMonths();
        int index = month - 1;
        if (index >= 0 && index < monthNames.length) {
            return monthNames[index];
        }
        return "Bulan Tidak Valid";
    }


    public void excelAbsensiBulanan(int month, int year, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Presensi-Bulanan");

        Font fontWhite = workbook.createFont();
        fontWhite.setColor(IndexedColors.WHITE.getIndex()); // Set font color to white

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

        // Fetch data
        List<Absensi> absensiList = absensiRepository.findByMonthAndYear(month, year);

        if (absensiList.isEmpty()) {
            // Handle case when there are no absences for the given month and year
            Row emptyRow = sheet.createRow(0);
            Cell emptyCell = emptyRow.createCell(0);
            emptyCell.setCellValue("Tidak ada data presensi untuk bulan " + getMonthName(month) + " tahun " + year);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4)); // Merge cells for message
        } else {
            // Group by user
            Map<String, List<Absensi>> userAbsensiMap = new HashMap<>();
            for (Absensi absensi : absensiList) {
                userAbsensiMap.computeIfAbsent(absensi.getUser().getUsername(), k -> new ArrayList<>()).add(absensi);
            }

            int rowNum = 0;

            // Title row
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DATA PRESENSI BULAN : " + getMonthName(month) + " - " + year);
            titleCell.setCellStyle(styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4)); // Merging cells for title
            rowNum++;

            for (Map.Entry<String, List<Absensi>> entry : userAbsensiMap.entrySet()) {
                String userName = entry.getKey();
                List<Absensi> userAbsensi = entry.getValue();
                String position = userAbsensi.get(0).getUser().getStatus();

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
                positionCell.setCellValue("Jabatan :   " + position);
                positionCell.setCellStyle(styleHeader);
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
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=PresensiBulanan_" + getMonthName(month) + "_" + year + ".xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void excelAbsensiBulananSimpel(int month, int year, HttpServletResponse response) throws IOException, ParseException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Presensi-Simpel");

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

        // Conditional formatting colors
        CellStyle styleColorLate = workbook.createCellStyle();
        styleColorLate.cloneStyleFrom(styleNumber);
        styleColorLate.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleColorLate.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleColorPermission = workbook.createCellStyle();
        styleColorPermission.cloneStyleFrom(styleNumber);
        styleColorPermission.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleColorPermission.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleColorEarly = workbook.createCellStyle();
        styleColorEarly.cloneStyleFrom(styleNumber);
        styleColorEarly.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        styleColorEarly.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleColorEmpty = workbook.createCellStyle();
        styleColorEmpty.cloneStyleFrom(styleNumber);
        styleColorEmpty.setFillForegroundColor(IndexedColors.BROWN.getIndex());
        styleColorEmpty.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleColorLateColumn = workbook.createCellStyle();
        styleColorLateColumn.cloneStyleFrom(styleNumber);
        styleColorLateColumn.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleColorLateColumn.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleColorPulangColumn = workbook.createCellStyle();
        styleColorPulangColumn.cloneStyleFrom(styleNumber);
        styleColorPulangColumn.setFillForegroundColor(IndexedColors.BROWN.getIndex());
        styleColorPulangColumn.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Fetch data
        List<Absensi> absensiList = absensiRepository.findByMonthAndYear(month, year);

        if (absensiList.isEmpty()) {
            // Handle case when there are no absences for the given month and year
            Row emptyRow = sheet.createRow(0);
            Cell emptyCell = emptyRow.createCell(0);
            emptyCell.setCellValue("Tidak ada data presensi simpel untuk bulan " + getMonthName(month) + "-" + year);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6)); // Merge cells for message
        } else {
            int rowNum = 0;

            // Title row
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DATA PRESENSI SIMPEL : " + getMonthName(month));
            titleCell.setCellStyle(styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 6)); // Merging cells for title
            rowNum++;

            // Header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"No", "Nama", "Tanggal", "Jam Masuk", "Jam Pulang", "Keterangan", "Terlambat"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleHeader);
            }

            // Data rows
            int userRowNum = 1;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            for (Absensi absensi : absensiList) {
                Row row = sheet.createRow(rowNum++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(userRowNum++);
                cell0.setCellStyle(styleNumber);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(absensi.getUser().getUsername());
                cell1.setCellStyle(styleNumber);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(absensi.getTanggalAbsen()));
                cell2.setCellStyle(styleNumber);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(absensi.getJamMasuk());
                cell3.setCellStyle(styleNumber);

                Cell cell4 = row.createCell(4);
                String jamPulang = absensi.getJamPulang();
                cell4.setCellValue(jamPulang);
                if (jamPulang == null || jamPulang.isEmpty() || jamPulang.equals("-")) {
                    cell4.setCellStyle(styleColorPulangColumn);
                } else {
                    cell4.setCellStyle(styleNumber);
                }

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(absensi.getStatusAbsen());
                cell5.setCellStyle(styleNumber);

                Cell cell6 = row.createCell(6);
                String terlambatString = "";
                if (absensi.getStatusAbsen().equals("Terlambat") && jamPulang != null && !jamPulang.isEmpty() && !jamPulang.equals("-")) {
                    // Menghitung durasi keterlambatan dalam menit, detik, dan milidetik
                    Date shiftStartTime = sdf.parse(absensi.getUser().getShift().getWaktuMasuk());
                    Date actualStartTime = sdf.parse(absensi.getJamMasuk());
                    long terlambatMillis = actualStartTime.getTime() - shiftStartTime.getTime();
                    int totalDetik = (int) (terlambatMillis / 1000);
                    int menit = totalDetik / 60;
                    int detik = totalDetik % 60;
                    int milidetik = (int) (terlambatMillis % 1000);
                    terlambatString = String.format("%02d,%02d,%03d", menit, detik, milidetik);
                    cell6.setCellStyle(styleColorLate); // Set yellow background for lateness
                } else if (absensi.getStatusAbsen().equals("Terlambat")) {
                    cell6.setCellStyle(styleColorLate); // Set yellow background for lateness
                } else {
                    cell6.setCellStyle(styleNumber); // Set default style for other cases
                }
                cell6.setCellValue(terlambatString);
            }
        }

        // Auto-size columns for better readability
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Presensi_Simpel_" + getMonthName(month) + "_" + year + ".xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

//    public void excelAbsensiBulananSimpel(int month, int year ,  HttpServletResponse response) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Absensi-Simpel");
//
//        Font fontWhite = workbook.createFont();
//        fontWhite.setColor(IndexedColors.WHITE.getIndex());
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
//        List<Absensi> absensiList = absensiRepository.findByMonthAndYear(month , year);
//
//        if (absensiList.isEmpty()) {
//            // Handle case when there are no absences for the given month and year
//            Row emptyRow = sheet.createRow(0);
//            Cell emptyCell = emptyRow.createCell(0);
//            emptyCell.setCellValue("Tidak ada data absensi simpel untuk bulan " + getMonthName(month) + "-" + year );
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
//            titleCell.setCellValue("DATA ABSENSI SIMPEL : " + getMonthName(month) );
//            titleCell.setCellStyle(styleTitle);
//            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4)); // Merging cells for title
//            rowNum++;
//
//            for (Map.Entry<String, List<Absensi>> entry : userAbsensiMap.entrySet()) {
//                String userName = entry.getKey();
//                List<Absensi> userAbsensi = entry.getValue();
//                String position = userAbsensi.get(0).getUser().getJabatan().getNamaJabatan();
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
//                positionCell.setCellValue("Jabatan :   " + position);
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
//        response.setHeader("Content-Disposition", "attachment; filename=AbsensiSimpel" + getMonthName(month) + ".xlsx");
//        workbook.write(response.getOutputStream());
//        workbook.close();
//    }



//    bulanann
//public void excelAbsensiBulananByKelas(int bulan, int tahun, Long kelasId, HttpServletResponse response) throws IOException {
//    Workbook workbook = new XSSFWorkbook();
//    Sheet sheet = workbook.createSheet("Presensi-Bulanan");
//
//    // Define cell styles
//    CellStyle styleHeader = createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), true);
//    CellStyle styleData = createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), false);
//    CellStyle styleCheckmark = createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), false);
//
//    Font fontCheckmark = workbook.createFont();
//    fontCheckmark.setFontHeightInPoints((short) 12);
//    styleCheckmark.setFont(fontCheckmark);
//
//    String checkmark = "\u2714"; // Unicode for checkmark
//    String empty = ""; // Empty cell for unchecked
//
////        Calendar calendar = Calendar.getInstance();
////        calendar.setTime(bulan);
////        int day = calendar.get(Calendar.DAY_OF_MONTH);
////        int month = calendar.get(Calendar.MONTH) + 1;
////        int year = calendar.get(Calendar.YEAR);
//
//    List<Absensi> absensiList = absensiRepository.findByKelasIdAndBulan(kelasId, bulan, tahun);
//
//    // Map to get user by username
//    Map<String, UserModel> userMap = new HashMap<>();
//    for (Absensi absensi : absensiList) {
//        UserModel user = absensi.getUser();
//        userMap.put(user.getUsername(), user);
//    }
//
//    Map<String, List<Absensi>> userAbsensiMap = new HashMap<>();
//    for (Absensi absensi : absensiList) {
//        userAbsensiMap.computeIfAbsent(absensi.getUser().getUsername(), k -> new ArrayList<>()).add(absensi);
//    }
//
//    int rowNum = 0;
//
//    // Title row
//    Row titleRow = sheet.createRow(rowNum++);
//    Cell titleCell = titleRow.createCell(0);
//    titleCell.setCellValue("DATA PRESENSI BULANAN : " + getMonthName(bulan) + " " + tahun);
//    titleCell.setCellStyle(styleHeader);
//    sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8)); // Merging cells for title
//    applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8), workbook); // Apply border for merged cells
//    rowNum++;
//
//    // Header row (first row for "No", "Nama", "Kelas", "Keterangan", and "Presensi")
//    Row headerRow = sheet.createRow(rowNum++);
//    String[] headers = {"No", "Nama", "Kelas", "Keterangan", "Presensi"};
//    for (int i = 0; i < headers.length; i++) {
//        Cell cell = headerRow.createCell(i);
//        cell.setCellValue(headers[i]);
//        cell.setCellStyle(styleHeader);
//    }
//    sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 4, 8)); // Merge Presensi cell for sub-columns
//    applyBorderToMergedCells(sheet, new CellRangeAddress(rowNum - 1, rowNum - 1, 4, 8), workbook); // Apply border for merged cells
//
//    // Second row for Presensi sub-columns (Hadir, Izin, Terlambat, Izin Tengah Hari, Tidak Masuk)
//    Row presensiRow = sheet.createRow(rowNum++);
//    String[] presensiHeaders = {"Hadir", "Izin", "Terlambat", "Izin Tengah Hari", "Tidak Masuk" ,"Sakit" };
//    for (int i = 0; i < presensiHeaders.length; i++) {
//        Cell cell = presensiRow.createCell(4 + i); // Starting from 5th column for sub-columns
//        cell.setCellValue(presensiHeaders[i]);
//        cell.setCellStyle(styleHeader);
//    }
//
//    // Initialize counters for summary row (Jumlah)
//    int totalHadir = 0;
//    int totalIzin = 0;
//    int totalTerlambat = 0;
//    int totalIzinTengahHari = 0;
//    int totalTidakMasuk = 0;
//    int totalSakit = 0 ;
//
//    // Data rows
//    // Data rows
//    int no = 1; // Initialize row number for "No" column
//    for (Map.Entry<String, List<Absensi>> entry : userAbsensiMap.entrySet()) {
//        String userName = entry.getKey();
//        List<Absensi> userAbsensi = entry.getValue();
//        UserModel userModel = userMap.get(userName);
//
//        // Retrieve kelas from User
//        String kelas = userModel != null ? userModel.getKelas().getNamaKelas() : "Unknown";
//
//        // Initialize counters for each status for this user
//        int userHadir = 0;
//        int userIzin = 0;
//        int userTerlambat = 0;
//        int userIzinTengahHari = 0;
//        int userTidakMasuk = 0;
//        int userSakit = 0 ;
//
//        String keterangan = "";
//
//        for (int i = 1; i <= daysInMonth(bulan, tahun); i++) {
//            final int day = i; // Final variable for lambda
//
//            // Find absensi for the specific day
//            Absensi absensiForDay = userAbsensi.stream()
//                    .filter(absensi -> {
//                        LocalDate localDate = absensi.getTanggalAbsen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                        return localDate.getDayOfMonth() == day;
//                    })
//                    .findFirst()
//                    .orElse(null);
//
//            if (absensiForDay == null) {
//                // If no absensi for this day, increment "Tidak Masuk"
//                userTidakMasuk++;
//            } else {
//                // If there is absensi, process status
//                String status = absensiForDay.getStatusAbsen().toLowerCase();
//                if (status.equals("lebih awal")) {
//                    userTerlambat++;
//                } else if (status.equals("izin")) {
//                    userIzin++;
//                    keterangan = absensiForDay.getKeteranganIzin();
//                } else if (status.equals("terlambat")) {
//                    userTerlambat++;
//                    keterangan = absensiForDay.getKeteranganTerlambat();
//                } else if (status.equals("izin tengah hari")) {
//                    userIzinTengahHari++;
//                    keterangan = absensiForDay.getKeteranganPulangAwal();
//                } else if (status.equals("Alpha")) {
//                    userTidakMasuk++;
//                }else if (status.equals("Izin")) {
//                    userSakit++;
//                }
//            }
//        }
//
//
//        // Jumlahkan status terlambat dan lebih awal untuk kolom hadir
//        userHadir = userTerlambat;
//
//        // Add data row
//        Row dataRow = sheet.createRow(rowNum++);
//
//        // "No" column
//        Cell noCell = dataRow.createCell(0);
//        noCell.setCellValue(no++);
//        noCell.setCellStyle(styleData);
//
//        // "Nama" column
//        Cell namaCell = dataRow.createCell(1);
//        namaCell.setCellValue(userName);
//        namaCell.setCellStyle(styleData);
//
//        // "Kelas" column
//        Cell kelasCell = dataRow.createCell(2);
//        kelasCell.setCellValue(kelas);
//        kelasCell.setCellStyle(styleData);
//
//        // "Keterangan" column
//        Cell keteranganCell = dataRow.createCell(3);
//        keteranganCell.setCellValue(keterangan);
//        keteranganCell.setCellStyle(styleData);
//
//        // "Presensi" sub-columns
//        Cell hadirCell = dataRow.createCell(4);
//        hadirCell.setCellValue(userHadir); // Jumlah Hadir
//        hadirCell.setCellStyle(styleData); // Apply border style to "Hadir" cell
//        totalHadir += userHadir;
//
//        Cell izinCell = dataRow.createCell(5);
//        izinCell.setCellValue(userIzin); // Jumlah Izin
//        izinCell.setCellStyle(styleData); // Apply border style to "Izin" cell
//        totalIzin += userIzin;
//
//        Cell terlambatCell = dataRow.createCell(6);
//        terlambatCell.setCellValue(userTerlambat); // Jumlah Terlambat
//        terlambatCell.setCellStyle(styleData); // Apply border style to "Terlambat" cell
//        totalTerlambat += userTerlambat;
//
//        Cell izinTengahHariCell = dataRow.createCell(7);
//        izinTengahHariCell.setCellValue(userIzinTengahHari); // Jumlah Izin Tengah Hari
//        izinTengahHariCell.setCellStyle(styleData); // Apply border style to "Izin Tengah Hari" cell
//        totalIzinTengahHari += userIzinTengahHari;
//
//        Cell tidakMasukCell = dataRow.createCell(8);
//        tidakMasukCell.setCellValue(userTidakMasuk); // Jumlah Tidak Masuk
//        tidakMasukCell.setCellStyle(styleData); // Apply border style to "Tidak Masuk" cell
//        totalTidakMasuk += userTidakMasuk;
//
//        Cell sakitCell = dataRow.createCell(9);
//        sakitCell.setCellValue(userSakit); // Jumlah sakit
//        sakitCell.setCellStyle(styleData); // Apply border style to "sakit" cell
//        totalSakit += userSakit;
//
//
//    }
//
//
//
//    // Add summary row (Jumlah)
//    Row jumlahRow = sheet.createRow(rowNum++);
//    Cell jumlahLabelCell = jumlahRow.createCell(3); // Start from "Keterangan" column
//    jumlahLabelCell.setCellValue("Jumlah");
//    jumlahLabelCell.setCellStyle(styleHeader);
//
//    jumlahRow.createCell(4).setCellValue(totalHadir); // Total Hadir
//    jumlahRow.createCell(5).setCellValue(totalIzin); // Total Izin
//    jumlahRow.createCell(6).setCellValue(totalTerlambat); // Total Terlambat
//    jumlahRow.createCell(7).setCellValue(totalIzinTengahHari); // Total Izin Tengah Hari
//    jumlahRow.createCell(8).setCellValue(totalTidakMasuk); // Total Tidak Masuk
//
//    // Apply style to summary row cells
//    for (int i = 4; i <= 9 ; i++) {
//        jumlahRow.getCell(i).setCellStyle(styleData);
//    }
//
//    // Adjust column widths
//    for (int i = 0; i < 9; i++) {
//        sheet.autoSizeColumn(i);
//    }
//
//    // Write output to response
//    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//    response.setHeader("Content-Disposition", "attachment; filename=PresensiBulanan.xlsx");
//    workbook.write(response.getOutputStream());
//    workbook.close();
//}

    public void excelAbsensiBulananByKelas(int bulan, int tahun, Long kelasId, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Presensi-Bulanan");

        // Define cell styles
        CellStyle styleHeader = createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), true);
        CellStyle styleData = createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BorderStyle.THIN, IndexedColors.BLACK.getIndex(), false);

        String checkmark = "\u2714"; // Unicode for checkmark

        // Get data
        List<Absensi> absensiList = absensiRepository.findByKelasIdAndBulan(kelasId, bulan, tahun);

        // Map users and absensi
        Map<String, UserModel> userMap = absensiList.stream()
                .collect(Collectors.toMap(absensi -> absensi.getUser().getUsername(), Absensi::getUser, (u1, u2) -> u1));

        Map<String, List<Absensi>> userAbsensiMap = absensiList.stream()
                .collect(Collectors.groupingBy(absensi -> absensi.getUser().getUsername()));

        int rowNum = 0;

        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA PRESENSI BULANAN : " + getMonthName(bulan) + " " + tahun);
        titleCell.setCellStyle(styleHeader);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 9));

        // Header rows
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"No", "Nama", "Kelas", "Keterangan", "Hadir", "Izin", "Terlambat", "Izin Tengah Hari", "Tidak Masuk", "Sakit"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int no = 1;
        int totalHadir = 0, totalIzin = 0, totalTerlambat = 0, totalIzinTengahHari = 0, totalTidakMasuk = 0, totalSakit = 0;

        for (Map.Entry<String, List<Absensi>> entry : userAbsensiMap.entrySet()) {
            String userName = entry.getKey();
            List<Absensi> userAbsensi = entry.getValue();
            UserModel userModel = userMap.get(userName);
            String kelas = userModel != null ? userModel.getKelas().getNamaKelas() : "Unknown";

            int userHadir = 0, userIzin = 0, userTerlambat = 0, userIzinTengahHari = 0, userTidakMasuk = 0, userSakit = 0;
            StringBuilder keterangan = new StringBuilder();

            for (int i = 1; i <= daysInMonth(bulan, tahun); i++) {
                int day = i;
                Absensi absensiForDay = userAbsensi.stream()
                        .filter(absensi -> absensi.getTanggalAbsen().toInstant().atZone(ZoneId.systemDefault()).getDayOfMonth() == day)
                        .findFirst()
                        .orElse(null);

                if (absensiForDay == null) {
                    userTidakMasuk++;
                } else {
                    String status = absensiForDay.getStatusAbsen().toLowerCase();
                    switch (status) {
                        case "hadir":
                            userHadir++;
                            break;
                        case "izin":
                            userIzin++;
                            break;
                        case "terlambat":
                            userTerlambat++;
                            break;
                        case "izin tengah hari":
                            userIzinTengahHari++;
                            break;
                        case "sakit":
                            userSakit++;
                            break;
                        default:
                            userTidakMasuk++;
                            break;
                    }
                    if (!absensiForDay.getStatusAbsen().isEmpty()) {
                        keterangan.append(absensiForDay.getStatusAbsen()).append(", ");
                    }
                }
            }

            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(no++);
            dataRow.createCell(1).setCellValue(userName);
            dataRow.createCell(2).setCellValue(kelas);
            dataRow.createCell(3).setCellValue(keterangan.toString().trim());
            dataRow.createCell(4).setCellValue(userHadir);
            dataRow.createCell(5).setCellValue(userIzin);
            dataRow.createCell(6).setCellValue(userTerlambat);
            dataRow.createCell(7).setCellValue(userIzinTengahHari);
            dataRow.createCell(8).setCellValue(userTidakMasuk);
            dataRow.createCell(9).setCellValue(userSakit);

            totalHadir += userHadir;
            totalIzin += userIzin;
            totalTerlambat += userTerlambat;
            totalIzinTengahHari += userIzinTengahHari;
            totalTidakMasuk += userTidakMasuk;
            totalSakit += userSakit;
        }

        // Summary row
        Row summaryRow = sheet.createRow(rowNum++);
        summaryRow.createCell(3).setCellValue("Jumlah");
        summaryRow.createCell(4).setCellValue(totalHadir);
        summaryRow.createCell(5).setCellValue(totalIzin);
        summaryRow.createCell(6).setCellValue(totalTerlambat);
        summaryRow.createCell(7).setCellValue(totalIzinTengahHari);
        summaryRow.createCell(8).setCellValue(totalTidakMasuk);
        summaryRow.createCell(9).setCellValue(totalSakit);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Presensi-Bulanan.xlsx");
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

    private int daysInMonth(int month, int year) {
        return YearMonth.of(year, month).lengthOfMonth();
    }


//    public void excelAbsensiBulananByKelas(int bulan, int tahun, Long kelasId, HttpServletResponse response) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Presensi-Bulanan");
//
//        Font fontWhite = workbook.createFont();
//        fontWhite.setColor(IndexedColors.WHITE.getIndex()); // Set font color to white
//
//        // Cell styles
//        CellStyle styleHeader = workbook.createCellStyle();
//        styleHeader.setAlignment(HorizontalAlignment.CENTER);
//        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
//        styleHeader.setBorderTop(BorderStylx`e.THIN);
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
//        styleColorPermission.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
//        styleColorPermission.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//        CellStyle styleColorEarly = workbook.createCellStyle();
//        styleColorEarly.cloneStyleFrom(styleNumber);
//        styleColorEarly.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//        styleColorEarly.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//        // Fetch data
//        List<Absensi> absensiList = absensiRepository.findByKelasIdAndBulan(kelasId, bulan, tahun);
//
//        if (absensiList.isEmpty()) {
//            // Handle case when there are no absences for the given month and year
//            Row emptyRow = sheet.createRow(0);
//            Cell emptyCell = emptyRow.createCell(0);
//            emptyCell.setCellValue("Tidak ada data presensi untuk bulan " + getMonthName(bulan) + " tahun " + tahun);
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
//            titleCell.setCellValue("DATA PRESENSI BULAN : " + getMonthName(bulan) + " - " + tahun);
//            titleCell.setCellStyle(styleTitle);
//            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4)); // Merging cells for title
//            rowNum++;
//
//            for (Map.Entry<String, List<Absensi>> entry : userAbsensiMap.entrySet()) {
//                String userName = entry.getKey();
//                List<Absensi> userAbsensi = entry.getValue();
//
//                // Variables to count absences for each user
//                int userTotalLate = 0;
//                int userTotalPermission = 0;
//                int userTotalEarly = 0;
//
//                // Name row
//                Row nameRow = sheet.createRow(rowNum++);
//                Cell nameCell = nameRow.createCell(0);
//                nameCell.setCellValue("Nama: " + userName);
//                nameCell.setCellStyle(styleHeader);
//
//                Row positionRow = sheet.createRow(rowNum++);
//                Cell positionCell = positionRow.createCell(0);
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
//                    cell0.setCellStyle(styleCenterNumber);
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
//                            userTotalLate++;
//                            break;
//                        case "Izin":
//                            styleColor = styleColorPermission;
//                            userTotalPermission++;
//                            break;
//                        case "Lebih Awal":
//                            styleColor = styleColorEarly;
//                            userTotalEarly++;
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
//        response.setHeader("Content-Disposition", "attachment; filename=PresensiBulanan" + getMonthName(bulan) + "_" + tahun + ".xlsx");
//        workbook.write(response.getOutputStream());
//        workbook.close();
//    }




}
