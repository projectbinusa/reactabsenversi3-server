package com.example.absensireact.controller;


import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exel.AbsensiExportService;
import com.example.absensireact.exel.ExcelAbsensiMingguan;
import com.example.absensireact.exel.ExcelAbsnesiBulanan;
import com.example.absensireact.exel.RekapanPresensiExcel;
import com.example.absensireact.model.Absensi;
import com.example.absensireact.model.Jabatan;
import com.example.absensireact.repository.AbsensiRepository;
import com.example.absensireact.securityNew.JwtTokenUtil;
import com.example.absensireact.service.AbsensiService;
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


    @Autowired
    private AbsensiExportService absensiExportService;

    @Autowired
    private RekapanPresensiExcel rekapanPresensiExcel;

    @Autowired
    private final AbsensiService absensiService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final AbsensiRepository absensiRepository;

    private static final Logger logger = LoggerFactory.getLogger(AbsensiController.class);


    @Autowired
    public AbsensiController(AbsensiService absensiService, AbsensiRepository absensiRepository) {
        this.absensiService = absensiService;

        this.absensiRepository = absensiRepository;
    }

    @Autowired
    private ExcelAbsnesiBulanan excelAbsensiBulanan;

    @Autowired
    private ExcelAbsensiMingguan excelAbsensiMingguan;

    @GetMapping("/absensi/export/absensi-bulanan-simpel")
    public void exportAbsensiBulananSimpel(@RequestParam("month") int month, @RequestParam("year") int year, HttpServletResponse response) throws IOException, ParseException {
        excelAbsensiBulanan.excelAbsensiBulananSimpel(month, year, response);
    }

    @GetMapping("/absensi/export/absensi-rekapan-perkaryawan")
    public void exportAbsensiRekapanPerkaryawan(@RequestParam("userId") Long userId, HttpServletResponse response) throws IOException {
        absensiExportService.excelAbsensiRekapanPerkaryawan(userId, response);
    }

    @GetMapping("/absensi/export/absensi-bulanan")
    public void exportAbsensiBulanan(@RequestParam("month") int month, @RequestParam("year") int year, HttpServletResponse response) throws IOException {
        excelAbsensiBulanan.excelAbsensiBulanan(month, year, response);
    }

    @GetMapping("/absensi/export/absensi-mingguan")
    public void exportAbsensiMingguan(
            @RequestParam("tanggalAwal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAwal,
            @RequestParam("tanggalAkhir") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAkhir,
            HttpServletResponse response) throws IOException {
        if (tanggalAwal == null || tanggalAkhir == null) {
            throw new NotActiveException("Tanggal tidak valid");
        }
        excelAbsensiMingguan.excelAbsensiMingguan(tanggalAwal, tanggalAkhir, response);
    }

    @GetMapping("/absensi/export/mingguan/by-kelas")
    public void excelMingguanPerKelas(
            @RequestParam("tanggalAwal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAwal,
            @RequestParam("tanggalAkhir") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAkhir,
            @RequestParam("kelasId") Long kelasId,
            HttpServletResponse response) throws IOException {
        try {
            excelAbsensiMingguan.excelMingguanPerKelas(tanggalAwal, tanggalAkhir, kelasId, response);
        } catch (IOException e) {
            e.printStackTrace();
            // handle exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/rekap-mingguan")
    public ResponseEntity<Map<String, List<Absensi>>> getAbsensiMingguan(
            @RequestParam("tanggalAwal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAwal,
            @RequestParam("tanggalAkhir") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAkhir) {
        Map<String, List<Absensi>> absensiMingguan = absensiService.getAbsensiByMingguan(tanggalAwal, tanggalAkhir);
        return ResponseEntity.ok(absensiMingguan);
    }

    @GetMapping("/absensi/rekap-mingguan-per-kelas")
    public ResponseEntity<Map<String, List<Absensi>>> getAbsensiMingguanPerKelas(
            @RequestParam("tanggalAwal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAwal,
            @RequestParam("tanggalAkhir") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggalAkhir,
            @RequestParam("kelasId") Long kelasId) {
        Map<String, List<Absensi>> absensiMingguanPerKelas = absensiService.getAbsensiByMingguanPerKelas(tanggalAwal, tanggalAkhir, kelasId);
        return ResponseEntity.ok(absensiMingguanPerKelas);
    }

    @GetMapping("/absensi/rekap-perkaryawan/export")
    public ResponseEntity<?> exportAbsensiToExcel() {
        try {
            ByteArrayInputStream byteArrayInputStream = absensiExportService.RekapPerkaryawan();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=absensi.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(byteArrayInputStream.readAllBytes());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to export data");
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
            List<Absensi> absensiList = absensiService.getAbsensiByBulanSimpel(bulan, idAdmin);
            return ResponseEntity.ok(absensiList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/absensi/get-absensi-bulan")
    public List<Absensi> getAbsensiByBulan(@RequestParam("tanggalAbsen") String tanggalAbsenStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date tanggalAbsen = null;
        try {
            tanggalAbsen = formatter.parse(tanggalAbsenStr);
            logger.info("Parsed date: " + tanggalAbsen);
        } catch (ParseException e) {
            logger.error("Failed to parse date: " + e.getMessage());
            // handle exception, possibly return an error response
        }

        return absensiService.getAbsensiByBulan(tanggalAbsen);
    }

    @GetMapping("/absensi/by-tanggal")
    public List<Absensi> getAbsensiByTanggal(@RequestParam("tanggalAbsen") String tanggalAbsenStr) {
        if (tanggalAbsenStr == null || tanggalAbsenStr.isEmpty()) {
            // Handle empty or null tanggalAbsenStr, perhaps return an error response
            return Collections.emptyList();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date tanggalAbsen = null;
        try {
            tanggalAbsen = formatter.parse(tanggalAbsenStr);
            logger.info("Parsed date: " + tanggalAbsen);
        } catch (ParseException e) {
            logger.error("Failed to parse date: " + e.getMessage());
            return Collections.emptyList();
        }

        return absensiService.getAbsensiByTanggal(tanggalAbsen);
    }

    @GetMapping("/absensi/export/harian")
    public void exportAbsensiHarian(
            @RequestParam("tanggal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tanggal,
            HttpServletResponse response
    ) {
        try {
            excelAbsensiMingguan.excelAbsensiHarian(tanggal, response);
        } catch (IOException e) {
            e.printStackTrace();
            // handle exception
        }
    }

    @GetMapping("/absensi/getByUserId/{userId}")
    public ResponseEntity<List<Absensi>> getAbsensiByUserId(@PathVariable Long userId) {
        List<Absensi> absensi = absensiService.getAbsensiByUserId(userId);
        if (absensi.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(absensi, HttpStatus.OK);
    }

    @GetMapping("/absensi/get")
    public ResponseEntity<?> getAbsensiByToken(@RequestParam String token) {
        String userEmail = jwtTokenUtil.getUsernameFromToken(token);
        List<Absensi> absensi = absensiService.getAbsensiByEmail(userEmail);
        if (absensi.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(absensi, HttpStatus.OK);
    }

    @GetMapping("/absensi/admin/{adminId}")
    public ResponseEntity<List<Absensi>> getAllByAdmin(@PathVariable Long adminId) {
        try {
            List<Absensi> absensiList = absensiService.getAllByAdmin(adminId);
            return new ResponseEntity<>(absensiList, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/checkAbsensi")
    public ResponseEntity<String> checkAbsensiToday(@RequestParam String token) {
        // Dapatkan userId dan email dari token
        Long userId = jwtTokenUtil.getIdFromToken(token);
        String userEmail = jwtTokenUtil.getUsernameFromToken(token);

        boolean alreadyAbsen;
        if (userId != null) {
            alreadyAbsen = absensiService.checkUserAlreadyAbsenToday(userId);
        } else {
            alreadyAbsen = absensiService.checkUserAlreadyAbsenTodayByEmail(userEmail);
        }

        String message = alreadyAbsen ? "Pengguna sudah melakukan absensi hari ini." : "Pengguna belum melakukan absensi hari ini.";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }


    @GetMapping("/absensi/checkIzin/{userId}")
    public ResponseEntity<String> hasTakenLeave(@PathVariable Long userId) {
        if (absensiService.hasTakenLeave(userId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Pengguna sudah melakukan izin.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Pengguna belum melakukan izin.");
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
            Long userId = jwtTokenUtil.getIdFromToken(token);
            Absensi absensi = absensiService.checkUserAlpha(userId);
            return ResponseEntity.ok(absensi);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @GetMapping("/absensi/getAll")
    public ResponseEntity<List<Absensi>> getAllAbsensi() {
        List<Absensi> allAbsensi = absensiService.getAllAbsensi();
        return new ResponseEntity<>(allAbsensi, HttpStatus.OK);
    }

    @GetMapping("/absensi/getizin/{userId}")
    public ResponseEntity<List<Absensi>> getAbsensiByStatusIzin(@PathVariable Long userId) {
        List<Absensi> absensiList = absensiService.getByStatusAbsen(userId, "Izin");
        return new ResponseEntity<>(absensiList, HttpStatus.OK);
    }

    @GetMapping("/absensi/getData/{id}")
    public ResponseEntity<Absensi> getAbsensiById(@PathVariable Long id) {
        Optional<Absensi> absensi = absensiService.getAbsensiById(id);
        return absensi.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/absensi/izin")
    public Absensi izin(@RequestParam String token, @RequestBody Map<String, String> body) {
        String keteranganIzin = body.get("keteranganIzin");
        try {
            Long userId = jwtTokenUtil.getIdFromToken(token);
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            Absensi newIzin;
            if (userId == 0) {
                System.out.println("Email yang diambil dari token: " + userEmail);
                newIzin = absensiService.izinByEmail(userEmail, keteranganIzin);
            } else {
                System.out.println("User ID yang diambil dari token: " + userId);
                newIzin = absensiService.izin(userId, keteranganIzin);
            }
            return newIzin;
//            return absensiService.izin(userId, keteranganIzin);
        } catch (Exception e) {
            throw new RuntimeException("Error during absensi processing: " + e.getMessage(), e);
        }
    }

    @PutMapping("/absensi/izin-tengah-hari")
    public Absensi izinTengahHari(@RequestParam String token, @RequestBody Absensi keterangaPulangAwal) {
        try {
            Long userId = jwtTokenUtil.getIdFromToken(token);
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            Absensi newAbsensi;
            if (userId == 0) {
                System.out.println("Email yang diambil dari token: " + userEmail);
                newAbsensi = absensiService.izinTengahHariByEmail(userEmail, keterangaPulangAwal);
            } else {
                System.out.println("User ID yang diambil dari token: " + userId);
                newAbsensi = absensiService.izinTengahHari(userId, keterangaPulangAwal);
            }
            return newAbsensi;
//            return absensiService.izinTengahHari(userId, keterangaPulangAwal);
        } catch (Exception e) {
            throw new RuntimeException("Error during absensi processing: " + e.getMessage(), e);
        }
    }

    @PostMapping("/absensi/masuk")
    public ResponseEntity<Absensi> postAbsensiMasuk(@RequestParam String token, @RequestBody Absensi absensi) throws IOException, ParseException {
        Long userId = jwtTokenUtil.getIdFromToken(token);
        String userEmail = jwtTokenUtil.getUsernameFromToken(token);
        System.out.println("userid token: " + userId);

        Absensi newAbsensi;
        if (absensi.getJamShift() == null || absensi.getJamShift().isEmpty()) {
            if (userId == 0) {
                System.out.println("Email yang diambil dari token: " + userEmail);
                newAbsensi = absensiService.PostAbsensi(userEmail, absensi);
            } else {
                System.out.println("User ID yang diambil dari token: " + userId);
                newAbsensi = absensiService.PostAbsensiById(userId, absensi);
            }
        } else {
            if (userId == 0) {
                System.out.println("Email yang diambil dari token: " + userEmail);
                newAbsensi = absensiService.PostAbsensiSmart(userEmail, absensi);
            } else {
                System.out.println("User ID yang diambil dari token: " + userId);
                newAbsensi = absensiService.PostAbsensiSmartById(userId, absensi);
            }
        }

        return ResponseEntity.ok(newAbsensi);
    }


    @PostMapping("/absensi/smart/masuk")
    public ResponseEntity<Absensi> postAbsensiSmartMasuk(@RequestParam String token, @RequestBody Absensi absensi) throws IOException, ParseException {
        Long userId = jwtTokenUtil.getIdFromToken(token);
        String userEmail = jwtTokenUtil.getUsernameFromToken(token);
        System.out.println("userid token: " + userId);
        Absensi newAbsensi;
        if (userId == 0) {
            System.out.println("Email yang diambil dari token: " + userEmail);
            newAbsensi = absensiService.PostAbsensiSmart(userEmail, absensi);
        } else {
            System.out.println("User ID yang diambil dari token: " + userId);
            newAbsensi = absensiService.PostAbsensiSmartById(userId, absensi);
        }
        return ResponseEntity.ok(newAbsensi);
    }

    @PutMapping("/absensi/pulang")
    public ResponseEntity<?> putAbsensiPulang(@RequestParam String token, @RequestBody Absensi absensi
    ) {
        try {
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            Absensi newJabatan = absensiService.Pulang(userEmail, absensi);
            return ResponseEntity.ok(newJabatan);
        } catch (IOException | NotFoundException | ParseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/absensi/update/{id}")
    public ResponseEntity<Absensi> updateAbsensi(@PathVariable Long id, @RequestBody Absensi absensi) {
        Absensi updatedAbsensi = absensiService.updateAbsensi(id, absensi);
        return new ResponseEntity<>(updatedAbsensi, HttpStatus.OK);
    }

    @DeleteMapping("/absensi/delete/{id}")
    public ResponseEntity<?> deleteAbsensi(@PathVariable Long id) throws IOException {
        absensiService.deleteAbsensi(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/absensi/by-kelas/{kelasId}")
    public ResponseEntity<List<Absensi>> getAbsensiByKelas(
            @ApiParam(value = "ID of the class", required = true) @RequestParam("kelasId") Long kelasId
    ) {
        List<Absensi> absensiList = absensiService.getAbsensiByKelas(kelasId);
        if (absensiList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(absensiList, HttpStatus.OK);
    }

    @GetMapping("/export/absensi/by-kelas/{kelasId}")
    public void exportAbsensiByKelas(
            @ApiParam(value = "ID of the class", required = true) @PathVariable("kelasId") Long kelasId,
            HttpServletResponse response
    ) {
        try {
            rekapanPresensiExcel.excelAbsensiByKelas(kelasId, response);
        } catch (IOException e) {
            e.printStackTrace();
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
            rekapanPresensiExcel.excelAbsensiHarianByKelas(tanggal, kelasId, response);
        } catch (IOException e) {
            e.printStackTrace();
            // handle exception
        }
    }

    @GetMapping("/absensi/rekap/harian/by-kelas")
    public ResponseEntity<?> getAbsensiPerHari(
            @RequestParam("tanggal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tanggal,
            @RequestParam("kelasId") Long kelasId) {
        try {
            Object hasilRekap = absensiService.getAbsensiPerHari(tanggal, kelasId);

            // Respons sukses
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", hasilRekap
            ));
        } catch (NotFoundException e) {
            // Respons jika kelas tidak ditemukan
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            // Respons untuk error lainnya
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Terjadi kesalahan saat memproses data absensi."
            ));
        }
    }
    @GetMapping("/absensi/rekap/harian/all-kelas/per-hari")
    public ResponseEntity<?> getAbsensiPerHari(@RequestParam("tanggal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tanggal) {
        try {
            // Panggil service untuk mendapatkan data absensi semua kelas
            List<Map<String, Object>> absensiData = absensiService.getAbsensiPerHari(tanggal);

            // Kembalikan respons dengan status 200 OK
            return ResponseEntity.ok(absensiData);
        } catch (Exception e) {
            // Tangani error dan kembalikan status 500 dengan pesan error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
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
        List<Object[]> groupedData = absensiService.getAbsensiDataGroupedByRole();
        return ResponseEntity.ok(groupedData);
    }
    @GetMapping("/absensi/export/bulanan/by-kelas")
    public void exportAbsensiBulananByKelas(
            @RequestParam("bulan") int bulan,
            @RequestParam("tahun") int tahun,
            @RequestParam("kelasId") Long kelasId,
            HttpServletResponse response) {

        try {
            // Call service method to get the data and export to Excel
            excelAbsensiBulanan.excelAbsensiBulananByKelas(bulan, tahun, kelasId, response);
        } catch (IOException e) {
            logger.error("Failed to export Excel: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/absensi/bulanan/kelas/{kelasId}")
    public ResponseEntity<Map<String, List<Absensi>>> getAbsensiByBulananPerKelas(
            @PathVariable Long kelasId,
            @RequestParam int bulan,
            @RequestParam int tahun) {

        Map<String, List<Absensi>> absensiMap = absensiService.getAbsensiByBulananPerKelas(bulan, tahun, kelasId);
        return ResponseEntity.ok(absensiMap);
    }

    @GetMapping("/absensi/harian/by-kelas/{kelasId}")
    public ResponseEntity<Map<String, List<Absensi>>> getAbsensiHarianByKelas(
            @RequestParam("tanggal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tanggal,
            @PathVariable("kelasId") Long kelasId) {

        // Call the service method to get attendance
        Map<String, List<Absensi>> absensiMap = absensiService.getAbsensiHarianByKelas(tanggal, kelasId);

        // Return the result
        return ResponseEntity.ok(absensiMap);
    }

    @GetMapping("/absensi/by-orang-tua/{orangTuaId}")
    public ResponseEntity<List<Absensi>> getAbsensiByOrangTua(@PathVariable Long orangTuaId) {
        List<Absensi> absensiList = absensiService.getAbsensiByOrangTua(orangTuaId);
        return ResponseEntity.ok(absensiList);
    }

    @GetMapping("/absensi/izin/by-orangTua/{idOrangTua}")
    public List<Absensi> getStatusAbsenIzinByOrangTua(@PathVariable Long idOrangTua) {
        return absensiService.getStatusAbsenIzinByOrangTua(idOrangTua);
    }

}
