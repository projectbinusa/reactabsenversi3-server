package com.example.absensireact.controller;


import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exel.AbsensiExportService;
import com.example.absensireact.exel.ExcelAbsensiMingguan;
import com.example.absensireact.exel.ExcelAbsnesiBulanan;
import com.example.absensireact.exel.RekapanPresensiExcel;
import com.example.absensireact.model.Absensi;
import com.example.absensireact.repository.AbsensiRepository;
import com.example.absensireact.securityNew.JwtTokenUtil;
import com.example.absensireact.service.AbsensiService;
import io.swagger.annotations.ApiParam;
import org.apache.tomcat.util.buf.UDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.logging.Logger;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AbsensiController {


    @Autowired
    private AbsensiExportService absensiExportService;

    @Autowired
    private RekapanPresensiExcel rekapanPresensiExcel;

    @Autowired
    private ExcelAbsnesiBulanan excelAbsnesiBulanan;

    @Autowired
    private final AbsensiService absensiService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final AbsensiRepository absensiRepository;

    private static final Logger logger = Logger.getLogger(AbsensiController.class.getName());


    @Autowired
    public AbsensiController(AbsensiService absensiService , AbsensiRepository absensiRepository) {
        this.absensiService = absensiService;

        this.absensiRepository = absensiRepository;
    }

    @Autowired
    private ExcelAbsnesiBulanan excelAbsensiBulanan;

    @Autowired
    private ExcelAbsensiMingguan excelAbsensiMingguan;

    @GetMapping("/absensi/export/absensi-bulanan-simpel")
    public void exportAbsensiBulananSimpel(@RequestParam("month") int month,@RequestParam("year") int year ,HttpServletResponse response) throws IOException, ParseException {
        excelAbsensiBulanan.excelAbsensiBulananSimpel(month, year,response);
    }
   @GetMapping("/absensi/export/absensi-rekapan-perkaryawan")
    public void exportAbsensiRekapanPerkaryawan(@RequestParam("userId") Long userId, HttpServletResponse response) throws IOException {
        absensiExportService.excelAbsensiRekapanPerkaryawan(userId,response);
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
    public ResponseEntity<List<Absensi>> getAbsensiByBulanSimpel(@RequestParam("bulan") int bulan , @RequestParam Long idAdmin) {
        try {
            List<Absensi> absensiList = absensiService.getAbsensiByBulanSimpel(bulan , idAdmin);
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
            logger.severe("Failed to parse date: " + e.getMessage());
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
            logger.severe("Failed to parse date: " + e.getMessage());
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

    @GetMapping("/absensi/checkAbsensi/{userId}")
    public ResponseEntity<String> checkAbsensiToday(@PathVariable Long userId) {
        if (absensiService.checkUserAlreadyAbsenToday(userId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Pengguna sudah melakukan absensi hari ini.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Pengguna belum melakukan absensi hari ini.");
        }
    }
    @GetMapping("/absensi/checkIzin/{userId}")
    public ResponseEntity<String> hasTakenLeave(@PathVariable Long userId) {
        if (absensiService.hasTakenLeave(userId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Pengguna sudah melakukan izin.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Pengguna belum melakukan izin.");
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
    public Absensi izin(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String keteranganIzin = body.get("keteranganIzin");
        return absensiService.izin(userId, keteranganIzin);
    }
    @PutMapping("/absensi/izin-tengah-hari")
    public Absensi izinTengahHari(@RequestParam Long userId ,@RequestBody Absensi keteranganPulangAwal)  {

        return absensiService.izinTengahHari(userId , keteranganPulangAwal );
    }


    @PostMapping("/absensi/masuk")
    public ResponseEntity<?> postAbsensiMasuk(@RequestParam String token,
                                              @RequestParam("image") String image,
                                              @RequestParam("lokasiMasuk") String lokasiMasuk,
                                              @RequestParam("keteranganTerlambat") String keteranganTerlambat) {
        try {
            Long userId = jwtTokenUtil.getIdFromToken(token);

            // Gunakan userId ini untuk membuat absensi
            Absensi absensi = absensiService.PostAbsensi(userId, image, lokasiMasuk, keteranganTerlambat);

            return ResponseEntity.ok().body(absensi);
        } catch (IOException | EntityNotFoundException | NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
     @PutMapping("/absensi/pulang")
    public ResponseEntity<?> putAbsensiPulang(@RequestParam String token,
                                              @RequestParam("image") String image,
                                              @RequestParam("lokasiPulang") String lokasiPulang,
                                              @RequestParam("keteranganPulangAwal") String keteranganPulangAwal
     ) {
        try {
            Long userId = jwtTokenUtil.getIdFromToken(token);

            Absensi absensi = absensiService.PostAbsensi(userId, image, lokasiPulang, keteranganPulangAwal);
            return ResponseEntity.ok().body(absensi);
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
            logger.severe("Failed to export Excel: " + e.getMessage());
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
