package com.example.absensireact.controller;


import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.helper.CutiPDF;
import com.example.absensireact.model.Absensi;
import com.example.absensireact.model.Cuti;
import com.example.absensireact.service.CutiService;
import com.example.absensireact.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@Controller
@RequestMapping("/api")
public class CutiController {
    private static final Logger logger = LoggerFactory.getLogger(CutiController.class);

    private final CutiService cutiService;
    private final UserService userService;
    private final CutiPDF cutiPDF;

    public CutiController(CutiService cutiService, UserService userService, CutiPDF cutiPDF) {
        this.cutiService = cutiService;
        this.userService = userService;
        this.cutiPDF = cutiPDF;
    }

    @GetMapping("/cuti/download-pdf/{id}")
    public void downloadPDF(@PathVariable Long id, HttpServletResponse response) {
        try {
            logger.info("Mencoba mengunduh PDF untuk cuti dengan ID: {}", id);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cutiPDF.generatePDF(id, baos);

            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "attachment; filename=cuti.pdf");
            response.setContentLength(baos.size());

            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();

            logger.info("Berhasil mengunduh PDF untuk cuti ID: {}", id);
        } catch (IOException e) {
            logger.error("Gagal mengunduh PDF untuk cuti ID: {}", id, e);
        }
    }

    @GetMapping("/cuti/getById/{id}")
    public ResponseEntity<Optional<Cuti>> GetCutiById(@PathVariable Long id) {
        try {
            logger.info("Mencari cuti dengan ID: {}", id);
            Optional<Cuti> cutiOptional = cutiService.GetCutiById(id);
            if (cutiOptional.isEmpty()) {
                logger.warn("Cuti tidak ditemukan dengan ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
            }
            return ResponseEntity.ok(cutiOptional);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mencari cuti dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Optional.empty());
        }
    }

    @GetMapping("/cuti/admin/{adminId}")
    public ResponseEntity<List<Cuti>> getAllByAdmin(@PathVariable Long adminId) {
        try {
            logger.info("Mengambil semua cuti untuk admin dengan ID: {}", adminId);
            List<Cuti> absensiList = cutiService.getAllByAdmin(adminId);
            return ResponseEntity.ok(absensiList);
        } catch (NotFoundException e) {
            logger.warn("Admin ID {} tidak ditemukan", adminId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil cuti untuk admin ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/cuti/getByUser/{userId}")
    public ResponseEntity<List<Cuti>> GetCutiByUserId(@PathVariable Long userId) {
        try {
            logger.info("Mengambil cuti untuk user dengan ID: {}", userId);
            List<Cuti> cuti = cutiService.GetCutiByUserId(userId);
            return ResponseEntity.ok(cuti);
        } catch (NotFoundException e) {
            logger.warn("User ID {} tidak ditemukan", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil cuti untuk user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/cuti/getall")
    public ResponseEntity<List<Cuti>> getAllCuti() {
        List<Cuti> cutiList = cutiService.GetCutiAll();
        return new ResponseEntity<>(cutiList, HttpStatus.OK);
    }

    @PostMapping("/cuti/tambahCuti/{userId}")
    public ResponseEntity<Cuti> createCuti(@PathVariable Long userId, @RequestBody Cuti cuti) {
        try {
            logger.info("Menambahkan cuti untuk user dengan ID: {}", userId);
            Cuti createdCuti = cutiService.IzinCuti(userId, cuti);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCuti);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menambahkan cuti untuk user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/cuti/tolak-cuti/{id}")
    public ResponseEntity<Cuti> tolakCuti(@PathVariable Long id, @RequestBody Cuti cuti) {
        try {
            logger.info("Menolak cuti dengan ID: {}", id);
            Cuti updatedCuti = cutiService.TolakCuti(id, cuti);
            if (updatedCuti == null) {
                logger.warn("Cuti dengan ID {} tidak ditemukan untuk ditolak", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(updatedCuti);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menolak cuti ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/cuti/terima-cuti/{id}")
    public ResponseEntity<Cuti> terimaCuti(@PathVariable Long id, @RequestBody Cuti cuti) {
        try {
            logger.info("Menerima cuti dengan ID: {}", id);
            Cuti updatedCuti = cutiService.TerimaCuti(id, cuti);
            if (updatedCuti == null) {
                logger.warn("Cuti dengan ID {} tidak ditemukan untuk diterima", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(updatedCuti);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menerima cuti ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/cuti/update-cuti-user/{id}")
    public ResponseEntity<Cuti> updateCutiById(@PathVariable Long id, @RequestBody Cuti updatedCuti) {
        Cuti updated = cutiService.updateCutiById(id, updatedCuti);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/cuti/delete/{id}")
    public ResponseEntity<Void> deleteCuti(@PathVariable Long id) {
        try {
            logger.info("Menghapus cuti dengan ID: {}", id);
            boolean deleted = cutiService.deleteCuti(id);
            if (!deleted) {
                logger.warn("Cuti dengan ID {} tidak ditemukan untuk dihapus", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menghapus cuti ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}