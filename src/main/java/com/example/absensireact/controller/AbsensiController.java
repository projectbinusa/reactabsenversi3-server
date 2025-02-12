package com.example.absensireact.controller;


import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exel.*;
import com.example.absensireact.model.Absensi;
import com.example.absensireact.model.Jabatan;
import com.example.absensireact.repository.AbsensiRepository;
import com.example.absensireact.securityNew.JwtTokenUtil;
import com.example.absensireact.service.AbsensiService;
import com.example.absensireact.service.TelegramNotificationService;
import io.swagger.annotations.ApiParam;
import org.apache.tomcat.util.buf.UDecoder;
//import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.threeten.bp.LocalDate;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.NotActiveException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
//import java.util.logging.Logger;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AbsensiController {

    private static final Logger logger = LoggerFactory.getLogger(AbsensiController.class);

    @Autowired
    private AbsensiExportService absensiExportService;

    @Autowired
    private RekapanPresensiExcel rekapanPresensiExcel;

    @Autowired
    private final AbsensiService absensiService;

    @Autowired
    private TelegramNotificationService telegramNotificationService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final AbsensiRepository absensiRepository;

//    private static final Logger logger = LoggerFactory.getLogger(AbsensiController.class);


    @Autowired
    public AbsensiController(AbsensiService absensiService, AbsensiRepository absensiRepository) {
        this.absensiService = absensiService;


        this.absensiRepository = absensiRepository;
    }

    @Autowired
    private ExcelAbsnesiBulanan excelAbsensiBulanan;

    @Autowired
    private ExcelDataAdmin excelDataAdmin;

    @Autowired
    private ExcelAbsensiMingguan excelAbsensiMingguan;


    @GetMapping("/absensi/export/absensi-bulanan-simpel")
    public void exportAbsensiBulananSimpel(@RequestParam("month") int month, @RequestParam("year") int year, HttpServletResponse response) {
        try {
            logger.info("Exporting Absensi Bulanan Simpel for Month: {}, Year: {}", month, year);
            excelAbsensiBulanan.excelAbsensiBulananSimpel(month, year, response);
        } catch (IOException | ParseException e) {
            logger.error("Error exporting Absensi Bulanan Simpel for Month: {}, Year: {}", month, year, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/export/absensi-rekapan-perkaryawan")
    public void exportAbsensiRekapanPerkaryawan(@RequestParam("userId") Long userId, HttpServletResponse response) {
        try {
            logger.info("Exporting Absensi Rekapan Per Karyawan for userId: {}", userId);
            absensiExportService.excelAbsensiRekapanPerkaryawan(userId, response);
        } catch (IOException e) {
            logger.error("Error exporting Absensi Rekapan Per Karyawan for userId: {}", userId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/export/absensi-bulanan")
    public void exportAbsensiBulanan(@RequestParam("month") int month, @RequestParam("year") int year, HttpServletResponse response) {
        try {
            logger.info("Exporting Absensi Bulanan for Month: {}, Year: {}", month, year);
            excelAbsensiBulanan.excelAbsensiBulanan(month, year, response);
        } catch (IOException e) {
            logger.error("Error exporting Absensi Bulanan for Month: {}, Year: {}", month, year, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/export/absensi-mingguan")
    public void exportAbsensiMingguan(
            @RequestParam("tanggalAwal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAwal,
            @RequestParam("tanggalAkhir") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAkhir,
            HttpServletResponse response) {
        try {
            if (tanggalAwal == null || tanggalAkhir == null) {
                throw new IllegalArgumentException("Tanggal tidak valid");
            }
            logger.info("Exporting Absensi Mingguan from {} to {}", tanggalAwal, tanggalAkhir);
            excelAbsensiMingguan.excelAbsensiMingguan(tanggalAwal, tanggalAkhir, response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid date range: {} - {}", tanggalAwal, tanggalAkhir, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            logger.error("Error exporting Absensi Mingguan from {} to {}", tanggalAwal, tanggalAkhir, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/export/mingguan/by-kelas")
    public void excelMingguanPerKelas(
            @RequestParam("tanggalAwal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAwal,
            @RequestParam("tanggalAkhir") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAkhir,
            @RequestParam("kelasId") Long kelasId,
            HttpServletResponse response) {
        try {
            logger.info("Exporting Absensi Mingguan for Kelas ID: {} from {} to {}", kelasId, tanggalAwal, tanggalAkhir);
            excelAbsensiMingguan.excelMingguanPerKelas(tanggalAwal, tanggalAkhir, kelasId, response);
        } catch (IOException e) {
            logger.error("Error exporting Absensi Mingguan for Kelas ID: {}", kelasId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/rekap-mingguan")
    public ResponseEntity<Map<String, List<Absensi>>> getAbsensiMingguan(
            @RequestParam("tanggalAwal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAwal,
            @RequestParam("tanggalAkhir") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAkhir) {
        try {
            logger.info("Fetching Rekap Absensi Mingguan from {} to {}", tanggalAwal, tanggalAkhir);
            Map<String, List<Absensi>> absensiMingguan = absensiService.getAbsensiByMingguan(tanggalAwal, tanggalAkhir);
            return ResponseEntity.ok(absensiMingguan);
        } catch (Exception e) {
            logger.error("Error fetching Rekap Absensi Mingguan from {} to {}", tanggalAwal, tanggalAkhir, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/rekap-mingguan-per-kelas")
    public ResponseEntity<Map<String, List<Absensi>>> getAbsensiMingguanPerKelas(
            @RequestParam("tanggalAwal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAwal,
            @RequestParam("tanggalAkhir") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAkhir,
            @RequestParam("kelasId") Long kelasId) {
        try {
            logger.info("Fetching Rekap Absensi Mingguan for Kelas ID: {} from {} to {}", kelasId, tanggalAwal, tanggalAkhir);
            Map<String, List<Absensi>> absensiMingguanPerKelas = absensiService.getAbsensiByMingguanPerKelas(tanggalAwal, tanggalAkhir, kelasId);
            return ResponseEntity.ok(absensiMingguanPerKelas);
        } catch (Exception e) {
            logger.error("Error fetching Rekap Absensi Mingguan for Kelas ID: {}", kelasId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/rekap-perkaryawan/export")
    public ResponseEntity<?> exportAbsensiToExcel() {
        try {
            logger.info("Exporting Rekap Absensi Per Karyawan");
            ByteArrayInputStream byteArrayInputStream = absensiExportService.RekapPerkaryawan();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=absensi.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(byteArrayInputStream.readAllBytes());
        } catch (IOException e) {
            logger.error("Error exporting Rekap Absensi Per Karyawan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to export data");
        }
    }

    //    @GetMapping("/absensi/rekap/export/{userId}")
//    public ResponseEntity<?> exportAbsensiByUserId(@PathVariable Long userId ,  HttpServletResponse response) {
//        try {
//            absensiExportService.excelAbsensiRekapanPerkaryawan(userId , response);
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Failed to export data");
//        }
//        return null;
//    }
    @GetMapping("/absensi/get-absensi-bulan-simpel")
    public ResponseEntity<List<Absensi>> getAbsensiByBulanSimpel(@RequestParam("bulan") int bulan, @RequestParam Long idAdmin) {
        try {
            logger.info("Fetching absensi for bulan: {} and idAdmin: {}", bulan, idAdmin);
            List<Absensi> absensiList = absensiService.getAbsensiByBulanSimpel(bulan, idAdmin);
            return ResponseEntity.ok(absensiList);
        } catch (Exception e) {
            logger.error("Error fetching absensi: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/get-absensi-bulan")
    public ResponseEntity<List<Absensi>> getAbsensiByBulan(@RequestParam("tanggalAbsen") String tanggalAbsenStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date tanggalAbsen = formatter.parse(tanggalAbsenStr);
            logger.info("Parsed date: {}", tanggalAbsen);
            return ResponseEntity.ok(absensiService.getAbsensiByBulan(tanggalAbsen));
        } catch (ParseException e) {
            logger.error("Failed to parse date: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/absensi/by-tanggal")
    public ResponseEntity<List<Absensi>> getAbsensiByTanggal(@RequestParam("tanggalAbsen") String tanggalAbsenStr) {
        if (tanggalAbsenStr == null || tanggalAbsenStr.isEmpty()) {
            logger.warn("Received empty tanggalAbsenStr");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date tanggalAbsen = formatter.parse(tanggalAbsenStr);
            logger.info("Parsed date: {}", tanggalAbsen);
            return ResponseEntity.ok(absensiService.getAbsensiByTanggal(tanggalAbsen));
        } catch (ParseException e) {
            logger.error("Failed to parse date: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @GetMapping("/absensi/export/harian")
    public ResponseEntity<String> exportAbsensiHarian(@RequestParam("tanggal") Date tanggal, HttpServletResponse response) {
        try {
            logger.info("Exporting absensi for date: {}", tanggal);
            excelAbsensiMingguan.excelAbsensiHarian(tanggal, response);
            return ResponseEntity.ok("Export success");
        } catch (IOException e) {
            logger.error("Error exporting absensi: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Export failed");
        }
    }

    @GetMapping("/absensi/getByUserId/{userId}")
    public ResponseEntity<List<Absensi>> getAbsensiByUserId(@PathVariable Long userId) {
        logger.info("Fetching absensi for userId: {}", userId);
        List<Absensi> absensi = absensiService.getAbsensiByUserId(userId);
        return absensi.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(absensi);
    }

    @GetMapping("/absensi/get")
    public ResponseEntity<?> getAbsensiByToken(@RequestParam String token) {
        try {
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            logger.info("Fetching absensi for userEmail: {}", userEmail);
            List<Absensi> absensi = absensiService.getAbsensiByEmail(userEmail);
            return absensi.isEmpty()
                    ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                    : ResponseEntity.ok(absensi);
        } catch (Exception e) {
            logger.error("Error fetching absensi by token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/admin/{adminId}")
    public ResponseEntity<List<Absensi>> getAllByAdmin(@PathVariable Long adminId) {
        try {
            logger.info("Fetching all absensi for adminId: {}", adminId);
            List<Absensi> absensiList = absensiService.getAllByAdmin(adminId);
            return ResponseEntity.ok(absensiList);
        } catch (NotFoundException e) {
            logger.error("AdminId not found: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching absensi for adminId {}: {}", adminId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/checkAbsensi")
    public ResponseEntity<String> checkAbsensiToday(@RequestParam String token) {
        try {
            Long userId = jwtTokenUtil.getIdFromToken(token);
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            logger.info("Checking absensi for userId: {}, userEmail: {}", userId, userEmail);

            boolean alreadyAbsen = (userId != null)
                    ? absensiService.checkUserAlreadyAbsenToday(userId)
                    : absensiService.checkUserAlreadyAbsenTodayByEmail(userEmail);

            String message = alreadyAbsen
                    ? "Pengguna sudah melakukan absensi hari ini."
                    : "Pengguna belum melakukan absensi hari ini.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            logger.error("Error checking absensi: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/checkIzin/{userId}")
    public ResponseEntity<String> hasTakenLeave(@PathVariable Long userId) {
        try {
            logger.info("Checking izin for userId: {}", userId);
            boolean hasLeave = absensiService.hasTakenLeave(userId);
            return ResponseEntity.ok(hasLeave ? "Pengguna sudah melakukan izin." : "Pengguna belum melakukan izin.");
        } catch (Exception e) {
            logger.error("Error checking izin for userId {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
//    @PostMapping("/absensi/check-alpha")
//    public ResponseEntity<?> checkUserAlpha(@RequestParam String token) {
//        try {
//            Long userId = jwtTokenUtil.getIdFromToken(token);
//            Absensi absensi = absensiService.checkUserAlpha(userId);
//            return ResponseEntity.ok().body(Map.of(
//                    "message", "User belum melakukan absen hari ini. Status Alpha berhasil ditambahkan.",
//                    "absensi", absensi
//            ));
//
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "message", "User dengan ID tersebut tidak ditemukan.",
//                    "error", e.getMessage()
//            ));
//
//        } catch (BadRequestException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
//                    "message", e.getMessage(),
//                    "error", "Bad Request"
//            ));
//        }
//    }

    @PostMapping("/absensi/check-alpha")
    public ResponseEntity<Absensi> checkUserAlpha(@RequestParam String token) {
        try {
            logger.info("Memproses pengecekan absensi alpha untuk token: {}", token);
            Long userId = jwtTokenUtil.getIdFromToken(token);
            Absensi absensi = absensiService.checkUserAlpha(userId);
            logger.info("Absensi alpha ditemukan untuk userId: {}", userId);
            return ResponseEntity.ok(absensi);
        } catch (NotFoundException e) {
            logger.error("Absensi alpha tidak ditemukan untuk token: {}", token, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (BadRequestException e) {
            logger.error("Permintaan tidak valid saat mengecek absensi alpha untuk token: {}", token, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Terjadi error saat mengecek absensi alpha untuk token: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/absensi/getAll")
    public ResponseEntity<List<Absensi>> getAllAbsensi() {
        try {
            logger.info("Mengambil semua data absensi...");
            List<Absensi> allAbsensi = absensiService.getAllAbsensi();
            logger.info("Ditemukan {} data absensi", allAbsensi.size());
            return new ResponseEntity<>(allAbsensi, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil semua data absensi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/absensi/getizin/{userId}")
    public ResponseEntity<List<Absensi>> getAbsensiByStatusIzin(@PathVariable Long userId) {
        try {
            logger.info("Mengambil data absensi dengan status izin untuk userId: {}", userId);
            List<Absensi> absensiList = absensiService.getByStatusAbsen(userId, "Izin");
            logger.info("Ditemukan {} data absensi izin untuk userId: {}", absensiList.size(), userId);
            return new ResponseEntity<>(absensiList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil absensi izin untuk userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/absensi/getData/{id}")
    public ResponseEntity<Absensi> getAbsensiById(@PathVariable Long id) {
        try {
            logger.info("Mengambil data absensi berdasarkan ID: {}", id);
            Optional<Absensi> absensi = absensiService.getAbsensiById(id);
            return absensi.map(value -> {
                logger.info("Data absensi ditemukan untuk ID: {}", id);
                return new ResponseEntity<>(value, HttpStatus.OK);
            }).orElseGet(() -> {
                logger.warn("Data absensi TIDAK ditemukan untuk ID: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            });
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil absensi berdasarkan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/absensi/izin")
    public ResponseEntity<Absensi> izin(@RequestParam String token, @RequestBody Map<String, String> body) {
        try {
            String keteranganIzin = body.get("keteranganIzin");
            logger.info("Memproses absensi izin untuk token: {}", token);
            Long userId = jwtTokenUtil.getIdFromToken(token);
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);

            Absensi newIzin;
            if (userId == 0) {
                logger.info("Menggunakan email dari token: {}", userEmail);
                newIzin = absensiService.izinByEmail(userEmail, keteranganIzin);
            } else {
                logger.info("Menggunakan userId dari token: {}", userId);
                newIzin = absensiService.izin(userId, keteranganIzin);
            }

            logger.info("Absensi izin berhasil dibuat untuk userId/email: {}", userId == 0 ? userEmail : userId);
            return ResponseEntity.ok(newIzin);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat memproses absensi izin untuk token: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/absensi/izin-tengah-hari")
    public ResponseEntity<Absensi> izinTengahHari(@RequestParam String token, @RequestBody Absensi keterangaPulangAwal) {
        try {
            logger.info("Memproses absensi izin tengah hari untuk token: {}", token);
            Long userId = jwtTokenUtil.getIdFromToken(token);
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);

            Absensi newAbsensi;
            if (userId == 0) {
                logger.info("Menggunakan email dari token: {}", userEmail);
                newAbsensi = absensiService.izinTengahHariByEmail(userEmail, keterangaPulangAwal);
            } else {
                logger.info("Menggunakan userId dari token: {}", userId);
                newAbsensi = absensiService.izinTengahHari(userId, keterangaPulangAwal);
            }

            logger.info("Absensi izin tengah hari berhasil dibuat untuk userId/email: {}", userId == 0 ? userEmail : userId);
            return ResponseEntity.ok(newAbsensi);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat memproses absensi izin tengah hari untuk token: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/absensi/masuk")
    public ResponseEntity<?> postAbsensiMasuk(@RequestParam String token, @RequestBody Absensi absensi) {
        try {
            Long userId = jwtTokenUtil.getIdFromToken(token);
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            logger.info("User ID dari token: {}", userId);

            Absensi newAbsensi;
            boolean isJamShiftEmpty = absensi.getJamShift() == null || absensi.getJamShift().trim().isEmpty();

            if (isJamShiftEmpty) {
                if (userId == 0) {
                    logger.info("Email dari token: {}", userEmail);
                    newAbsensi = absensiService.PostAbsensi(userEmail, absensi);
                } else {
                    logger.info("User ID dari token: {}", userId);
                    newAbsensi = absensiService.PostAbsensiById(userId, absensi);
                }
            } else {
                if (userId == 0) {
                    logger.info("Email dari token: {}", userEmail);
                    newAbsensi = absensiService.PostAbsensiSmart(userEmail, absensi);
                } else {
                    logger.info("User ID dari token: {}", userId);
                    newAbsensi = absensiService.PostAbsensiSmartById(userId, absensi);
                }
            }

            return ResponseEntity.ok(newAbsensi);

        } catch (EntityNotFoundException e) {
            logger.error("User tidak ditemukan: {}", e.getMessage());
            telegramNotificationService.sendErrorNotification("/api/absensi/masuk", absensi.toString(), token, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User tidak ditemukan: " + e.getMessage()));

        } catch (IOException | ParseException e) {
            logger.error("Kesalahan pemrosesan data: {}", e.getMessage());
            telegramNotificationService.sendErrorNotification("/api/absensi/masuk", absensi.toString(), token, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Kesalahan pemrosesan data: " + e.getMessage()));

        } catch (Exception e) {
            logger.error("Terjadi kesalahan umum: {}", e.getMessage());
            telegramNotificationService.sendErrorNotification("/api/absensi/masuk", absensi.toString(), token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @PutMapping("/absensi/pulang")
    public ResponseEntity<?> putAbsensiPulang(@RequestParam String token, @RequestBody Absensi absensi) {
        try {
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            logger.info("Proses absensi pulang untuk user: {}", userEmail);

            Absensi newJabatan = absensiService.Pulang(userEmail, absensi);
            return ResponseEntity.ok(newJabatan);
        } catch (EntityNotFoundException e) {
            logger.error("User tidak ditemukan: {}", e.getMessage());
            telegramNotificationService.sendErrorNotification("/api/absensi/pulang", absensi.toString(), token, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User tidak ditemukan: " + e.getMessage()));

        } catch (IOException | ParseException e) {
            logger.error("Kesalahan pemrosesan data: {}", e.getMessage());
            telegramNotificationService.sendErrorNotification("/api/absensi/pulang", absensi.toString(), token, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Kesalahan pemrosesan data: " + e.getMessage()));

        } catch (Exception e) {
            logger.error("Terjadi kesalahan umum: {}", e.getMessage());
            telegramNotificationService.sendErrorNotification("/api/absensi/pulang", absensi.toString(), token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @DeleteMapping("/absensi/delete/{id}")
    public ResponseEntity<?> deleteAbsensi(@PathVariable Long id) {
        try {
            logger.info("Menghapus absensi dengan ID: {}", id);
            absensiService.deleteAbsensi(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Gagal menghapus absensi: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Gagal menghapus absensi: " + e.getMessage()));
        }
    }

    @GetMapping("/export/absensi/by-kelas/{kelasId}")
    public void exportAbsensiByKelas(
            @ApiParam(value = "ID of the class", required = true) @PathVariable("kelasId") Long kelasId,
            HttpServletResponse response
    ) {
        try {
            logger.info("Memulai export absensi untuk kelasId: {}", kelasId);
            rekapanPresensiExcel.excelAbsensiByKelas(kelasId, response);
            logger.info("Berhasil melakukan export absensi untuk kelasId: {}", kelasId);
        } catch (IOException e) {
            logger.error("Gagal melakukan export absensi untuk kelasId: {}", kelasId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to export data");
        }
    }

    @GetMapping("/absensi/export/harian/by-kelas")
    public void exportAbsensiHarianByKelas(
            @RequestParam("tanggal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tanggal,
            @RequestParam("kelasId") Long kelasId,
            HttpServletResponse response
    ) {
        try {
            logger.info("Memulai export absensi harian untuk kelasId: {} pada tanggal: {}", kelasId, tanggal);
            rekapanPresensiExcel.excelAbsensiHarianByKelas(tanggal, kelasId, response);
            logger.info("Berhasil melakukan export absensi harian untuk kelasId: {} pada tanggal: {}", kelasId, tanggal);
        } catch (IOException e) {
            logger.error("Gagal melakukan export absensi harian untuk kelasId: {} pada tanggal: {}", kelasId, tanggal, e);
        }
    }

    @GetMapping("/absensi/rekap/harian/by-kelas")
    public ResponseEntity<?> getAbsensiPerHari(
            @RequestParam("tanggal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tanggal,
            @RequestParam("kelasId") Long kelasId) {
        try {
            logger.info("Mengambil data absensi harian untuk kelasId: {} pada tanggal: {}", kelasId, tanggal);
            Object hasilRekap = absensiService.getAbsensiPerHari(tanggal, kelasId);
            logger.info("Berhasil mendapatkan data absensi harian untuk kelasId: {} pada tanggal: {}", kelasId, tanggal);

            return ResponseEntity.ok(Map.of("status", "success", "data", hasilRekap));
        } catch (NotFoundException e) {
            logger.warn("Data absensi tidak ditemukan untuk kelasId: {} pada tanggal: {}", kelasId, tanggal);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat memproses absensi harian untuk kelasId: {} pada tanggal: {}", kelasId, tanggal, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", "Terjadi kesalahan saat memproses data absensi."));
        }
    }

    @GetMapping("/absensi/rekap/harian/all-kelas/per-hari")
    public ResponseEntity<?> getAbsensiPerHariKelas(
            @RequestParam("tanggal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tanggal,
            @RequestParam("idAdmin") Long idAdmin) {
        try {
            logger.info("Mengambil data absensi harian untuk semua kelas oleh adminId: {} pada tanggal: {}", idAdmin, tanggal);
            List<Map<String, Object>> absensiData = absensiService.getAbsensiPerHariKelas(tanggal, idAdmin);
            logger.info("Berhasil mendapatkan data absensi harian untuk semua kelas oleh adminId: {} pada tanggal: {}", idAdmin, tanggal);

            return ResponseEntity.ok(absensiData);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat memproses absensi harian untuk semua kelas oleh adminId: {} pada tanggal: {}", idAdmin, tanggal, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    @GetMapping("/absensi/export/harian/all-kelas/per-hari")
    public ResponseEntity<byte[]> exportAbsensiToExcel(
            @RequestParam("tanggal") String tanggal,
            @RequestParam("idAdmin") Long idAdmin
    ) {
        try {
            logger.info("Memulai export absensi ke Excel untuk semua kelas oleh adminId: {} pada tanggal: {}", idAdmin, tanggal);
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(tanggal);
            byte[] excelFile = absensiService.exportAbsensiPerHariKelasToExcel(parsedDate, idAdmin);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=absensi.xlsx");
            logger.info("Berhasil melakukan export absensi ke Excel untuk adminId: {} pada tanggal: {}", idAdmin, tanggal);

            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Gagal melakukan export absensi ke Excel untuk adminId: {} pada tanggal: {}", idAdmin, tanggal, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    @GetMapping("/absensi/rekap/harian/all-kelas/per-hari")
//    public ResponseEntity<?> getAbsensiPerHariByGrup(@RequestParam("tanggal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggal) {
//        try {
//            // Panggil service untuk mendapatkan data absensi semua kelas
//            List<Map<String, Object>> absensiData = absensiService.getAbsensiPerHari(tanggal);
//
//            // Kembalikan respons dengan status 200 OK
//            return ResponseEntity.ok(absensiData);
//        } catch (Exception e) {
//            // Tangani error dan kembalikan status 500 dengan pesan error
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
//        }
//    }
    @GetMapping("/absensi/get-data/group-by-role")
    public ResponseEntity<List<Object[]>> getAbsensiGroupedByRole() {
        try {
            logger.info("Mengambil data absensi yang dikelompokkan berdasarkan peran");
            List<Object[]> groupedData = absensiService.getAbsensiDataGroupedByRole();
            logger.info("Berhasil mendapatkan {} data absensi", groupedData.size());
            return ResponseEntity.ok(groupedData);
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi yang dikelompokkan berdasarkan peran", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/export/bulanan/by-kelas")
    public void exportAbsensiBulananByKelas(
            @RequestParam("bulan") int bulan,
            @RequestParam("tahun") int tahun,
            @RequestParam("kelasId") Long kelasId,
            HttpServletResponse response) {
        try {
            logger.info("Menjalankan ekspor absensi bulanan untuk kelas {} bulan {} tahun {}", kelasId, bulan, tahun);
            excelAbsensiBulanan.excelAbsensiBulananByKelas(bulan, tahun, kelasId, response);
            logger.info("Berhasil mengekspor absensi bulanan untuk kelas {}", kelasId);
        } catch (IOException e) {
            logger.error("Gagal mengekspor absensi bulanan untuk kelas {} bulan {} tahun {}", kelasId, bulan, tahun, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/export/bulanan-guru/by-kelas")
    public void exportAbsensiGuruBulananByKelas(
            @RequestParam("kelasId") Long kelasId,
            @RequestParam("idAdmin") Long idAdmin,
            @RequestParam("bulan") int bulan,
            @RequestParam("tahun") int tahun,
            HttpServletResponse response) {
        try {
            logger.info("Menjalankan ekspor absensi guru bulanan untuk kelas {} bulan {} tahun {} oleh admin {}", kelasId, bulan, tahun, idAdmin);
            excelDataAdmin.exportGuru(idAdmin, kelasId, bulan, tahun, response);
            logger.info("Berhasil mengekspor absensi guru bulanan untuk kelas {}", kelasId);
        } catch (IOException e) {
            logger.error("Gagal mengekspor absensi guru bulanan untuk kelas {} bulan {} tahun {} oleh admin {}", kelasId, bulan, tahun, idAdmin, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/bulanan/kelas/{kelasId}")
    public ResponseEntity<Map<String, List<Absensi>>> getAbsensiByBulananPerKelas(
            @PathVariable Long kelasId,
            @RequestParam int bulan,
            @RequestParam int tahun) {
        try {
            logger.info("Mengambil data absensi bulanan untuk kelas {} bulan {} tahun {}", kelasId, bulan, tahun);
            Map<String, List<Absensi>> absensiMap = absensiService.getAbsensiByBulananPerKelas(bulan, tahun, kelasId);
            logger.info("Berhasil mendapatkan absensi bulanan untuk kelas {}", kelasId);
            return ResponseEntity.ok(absensiMap);
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi bulanan untuk kelas {}", kelasId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/harian/by-kelas/{kelasId}")
    public ResponseEntity<Map<String, List<Absensi>>> getAbsensiHarianByKelas(
            @RequestParam("tanggal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tanggal,
            @PathVariable("kelasId") Long kelasId) {
        try {
            logger.info("Mengambil data absensi harian untuk kelas {} pada tanggal {}", kelasId, tanggal);
            Map<String, List<Absensi>> absensiMap = absensiService.getAbsensiHarianByKelas(tanggal, kelasId);
            logger.info("Berhasil mendapatkan absensi harian untuk kelas {}", kelasId);
            return ResponseEntity.ok(absensiMap);
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi harian untuk kelas {} pada tanggal {}", kelasId, tanggal, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/by-orang-tua/{orangTuaId}")
    public ResponseEntity<List<Absensi>> getAbsensiByOrangTua(@PathVariable Long orangTuaId) {
        try {
            logger.info("Mengambil data absensi untuk orang tua dengan ID {}", orangTuaId);
            List<Absensi> absensiList = absensiService.getAbsensiByOrangTua(orangTuaId);
            logger.info("Berhasil mendapatkan {} data absensi untuk orang tua dengan ID {}", absensiList.size(), orangTuaId);
            return ResponseEntity.ok(absensiList);
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi untuk orang tua dengan ID {}", orangTuaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/izin/by-orangTua/{idOrangTua}")
    public ResponseEntity<?> getStatusAbsenIzinByOrangTua(@PathVariable Long idOrangTua) {
        try {
            logger.info("Memproses permintaan absensi izin untuk idOrangTua: {}", idOrangTua);

            List<Absensi> absensiList = absensiService.getStatusAbsenIzinByOrangTua(idOrangTua);

            logger.info("Ditemukan {} data absensi izin untuk idOrangTua: {}", absensiList.size(), idOrangTua);

            return ResponseEntity.ok(absensiList);
        } catch (Exception e) {
            logger.error("Terjadi error saat mengambil data absensi izin untuk idOrangTua: {}", idOrangTua, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Terjadi kesalahan dalam mengambil data.");
        }
    }


}
