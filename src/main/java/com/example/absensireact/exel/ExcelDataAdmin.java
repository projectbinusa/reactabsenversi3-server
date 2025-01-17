package com.example.absensireact.exel;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.*;
import com.example.absensireact.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.poi.hssf.record.aggregates.RowRecordsAggregate.createRow;
import static org.apache.poi.ss.util.CellUtil.createCell;

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

    public void exportGuru(Long idAdmin, Long idKelas, int bulan, int tahun, HttpServletResponse response) throws IOException {
        // Membuat workbook baru
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DATA ABSENSI GURU");

        // Menyusun style untuk header, data, dan total
        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        styleTitle.setFont(titleFont);

        CellStyle styleTitleTotal = workbook.createCellStyle();
        styleTitleTotal.setAlignment(HorizontalAlignment.CENTER);
        styleTitleTotal.setVerticalAlignment(VerticalAlignment.CENTER);
        styleTitleTotal.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleTitleTotal.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font titleFontTotal = workbook.createFont();
        titleFontTotal.setBold(true);
        styleTitleTotal.setFont(titleFontTotal);

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleData1 = workbook.createCellStyle();
        styleData1.setAlignment(HorizontalAlignment.LEFT);
        styleData1.setVerticalAlignment(VerticalAlignment.CENTER);
        styleData1 .setBorderTop(BorderStyle.THIN);
        styleData1.setBorderRight(BorderStyle.THIN);
        styleData1.setBorderBottom(BorderStyle.THIN);
        styleData1.setBorderLeft(BorderStyle.THIN);

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

        // Mengambil data absensi
        List<Absensi> absensiList = absensiRepository.findByKelasIdAndBulan(idKelas, bulan, tahun);

        // Mengelompokkan absensi berdasarkan user
        Map<String, List<Absensi>> absensiByUser  = absensiList.stream()
                .collect(Collectors.groupingBy(absensi -> absensi.getUser ().getUsername()));

        // Mencari tanggal maksimum dalam data absensi
        int maxTanggal = absensiList.stream()
                .mapToInt(absensi -> {
                    LocalDate localDate = absensi.getTanggalAbsen().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return localDate.getDayOfMonth();
                })
                .max()
                .orElse(31); // Default ke 31 jika tidak ada data

        int rowNum = 0;

        // Baris judul
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DATA ABSENSI GURU DAN KARYAWAN SMK BINA NUSANTARA SEMARANG");
        titleCell.setCellStyle(styleTitle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxTanggal + 3)); // Menyesuaikan untuk kolom tanggal, total, dan tidak hadir

        // Baris header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = new String[maxTanggal + 4];  // Menyesuaikan untuk kolom "Tidak Hadir" dan persentase
        headers[0] = "No";
        headers[1] = "Nama";
        for (int i = 1; i <= maxTanggal; i++) {
            headers[i + 1] = String.valueOf(i);
        }
        headers[maxTanggal + 2] = "HADIR";
        headers[maxTanggal + 3] = "TIDAK HADIR";

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styleHeader);
        }

        // Data rows
        int no = 1;
        int[] totalPerHari = new int[maxTanggal];
        int totalKeseluruhanHadir = 0;  // Untuk menghitung total kehadiran keseluruhan
        int totalKeseluruhanTidakHadir = 0; // Untuk menghitung total ketidakhadiran keseluruhan
        for (Map.Entry<String, List<Absensi>> entry : absensiByUser .entrySet()) {
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
            cellNama.setCellStyle(styleData1);

            // Kehadiran per tanggal
            int totalKehadiran = 0;
            int totalTidakHadir = 0; // Variabel untuk menghitung ketidakhadiran
            for (int i = 1; i <= maxTanggal; i++) {
                final int tanggal = i; // Variabel final agar bisa digunakan di lambda
                Cell cellTanggal = dataRow.createCell(i + 1);

                // Mencari absensi untuk tanggal ini
                boolean isPresent = userAbsensi.stream()
                        .anyMatch(absensi -> {
                            LocalDate localDate = absensi.getTanggalAbsen().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            return localDate.getDayOfMonth() == tanggal;
                        });

                LocalDate currentDate = LocalDate.of(tahun, bulan, tanggal);
                boolean isHoliday = false; // Ganti dengan logika untuk memeriksa hari libur
                boolean isWeekend = currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY;
                List<LocalDate> holidays = getHolidays(tahun);
                if (holidays.contains(currentDate)) {
                    isHoliday = true;
                }

                if (isPresent) {
                    totalKehadiran++;
                    totalPerHari[i - 1]++;
                    cellTanggal.setCellValue("✓");
                } else if (!isHoliday && !isWeekend) { // Hanya hitung tidak hadir jika bukan hari libur atau akhir pekan
                    totalTidakHadir++; // Menambah jumlah tidak hadir
                    cellTanggal.setCellValue("-");
                } else {
                    cellTanggal.setCellValue("S/M"); // Menandai hari libur atau akhir pekan
                }
                cellTanggal.setCellStyle(styleData);
            }

            // Total Kehadiran dan Tidak Hadir per User
            Cell cellTotalHadir = dataRow.createCell(maxTanggal + 2);
            cellTotalHadir.setCellValue(totalKehadiran);
            cellTotalHadir.setCellStyle(styleData);

            Cell cellTotalTidakHadir = dataRow.createCell(maxTanggal + 3);
            cellTotalTidakHadir.setCellValue(totalTidakHadir);
            cellTotalTidakHadir.setCellStyle(styleData);

            totalKeseluruhanHadir += totalKehadiran;  // Menambahkan total hadir per user ke total keseluruhan
            totalKeseluruhanTidakHadir += totalTidakHadir; // Menambahkan total tidak hadir per user ke total keseluruhan
        }

        // Baris total kehadiran
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("TOTAL KEHADIRAN");
        totalCell.setCellStyle(styleTitleTotal);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1)); // Gabungkan kolom A dan B

        for (int i = 1; i <= maxTanggal; i++) {
            Cell cellTotalPerHari = totalRow.createCell(i + 1);
            int totalTidakHadirPerHari = absensiByUser .size() - totalPerHari[i - 1];
            String hadirTidakHadir = totalPerHari[i - 1] + "/" + totalTidakHadirPerHari; // Format total hadir/tidak hadir
            cellTotalPerHari.setCellValue(hadirTidakHadir);
            cellTotalPerHari.setCellStyle(styleTotal);

            // Menghitung persentase kehadiran per hari
            double persentaseKehadiran = (totalPerHari[i - 1] * 100.0) / (absensiByUser .size() > 0 ? absensiByUser .size() : 1);
            persentaseKehadiran = Math.min(persentaseKehadiran, 100.0); // Pastikan persentase tidak melebihi 100%
            String persentaseString = String.format("%.0f%%", persentaseKehadiran); // Format persentase dengan dua angka desimal
            cellTotalPerHari.setCellValue(totalPerHari[i - 1] + " / " + persentaseString);
        }

        // Baris total tidak hadir
        Row tidakHadirRow = sheet.createRow(rowNum++);
        Cell tidakHadirCell = tidakHadirRow.createCell(0);
        tidakHadirCell.setCellValue("TOTAL TIDAK HADIR");
        tidakHadirCell.setCellStyle(styleTitleTotal);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1)); // Merge columns A and B

        for (int i = 1; i <= maxTanggal; i++) {
            Cell cellTotalTidakHadirPerHari = tidakHadirRow.createCell(i + 1);
            int totalTidakHadirPerHari = absensiByUser .size() - totalPerHari[i - 1];
            cellTotalTidakHadirPerHari.setCellValue(totalTidakHadirPerHari); // Display the number of absentees per day

            double persentaseKetidakHadiran = (totalTidakHadirPerHari * 100.0) / (absensiByUser .size() > 0 ? absensiByUser .size() : 1);
            persentaseKetidakHadiran = Math.min(persentaseKetidakHadiran, 100.0);
            String persentaseString = String.format("%.0f%%", persentaseKetidakHadiran); // Format percentage with no decimal places
            cellTotalTidakHadirPerHari.setCellValue(totalTidakHadirPerHari + " / " + persentaseString);
        }

        Cell cellTotalTidakHadirKeseluruhan = tidakHadirRow.createCell(maxTanggal + 2);
        cellTotalTidakHadirKeseluruhan.setCellValue(totalKeseluruhanTidakHadir + " / " + String.format("%.0f%%", Math.min((totalKeseluruhanTidakHadir * 100.0) / (absensiByUser .size() > 0 ? absensiByUser .size() : 1), 100.0))); // Display the overall total absences with percentage
        cellTotalTidakHadirKeseluruhan.setCellStyle(styleTotal);

        sheet.autoSizeColumn(0);
        sheet.setColumnWidth(1, 9000);
        for (int i = 2; i <= maxTanggal + 2; i++) {
            sheet.setColumnWidth(i, 2000);
        }

        // Menyimpan file
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Data_Absensi_Guru.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private List<LocalDate> getHolidays(int year) {
        List<LocalDate> holidays = new ArrayList<>();
        holidays.add(LocalDate.of(year, Month.JANUARY, 1));
        holidays.add(LocalDate.of(year, Month.JANUARY, 20));
        holidays.add(LocalDate.of(year, Month.MARCH, 21));
        holidays.add(LocalDate.of(year, Month.MAY, 1));
        holidays.add(LocalDate.of(year, Month.MAY, 18));
        holidays.add(LocalDate.of(year, Month.MAY, 25));
        holidays.add(LocalDate.of(year, Month.JUNE, 1));
        holidays.add(LocalDate.of(year, Month.JUNE, 4));
        holidays.add(LocalDate.of(year, Month.JULY, 17));
        holidays.add(LocalDate.of(year, Month.AUGUST, 17));
        holidays.add(LocalDate.of(year, Month.SEPTEMBER, 29));
        holidays.add(LocalDate.of(year, Month.DECEMBER, 25));
        // Tambahkan hari libur lainnya sesuai kebutuhan
        return holidays;
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
        styleTitle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
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
