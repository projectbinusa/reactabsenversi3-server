package com.example.absensireact.controller;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.helper.LemburPDF;
import com.example.absensireact.model.Cuti;
import com.example.absensireact.model.Lembur;
import com.example.absensireact.service.LemburService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Controller
public class LemburController {

    private static final Logger logger = LoggerFactory.getLogger(LemburController.class);

    @Autowired
    private LemburService lemburService;

    @Autowired
    private LemburPDF lemburPDF;

    @GetMapping("/lembur/download-pdf/{id}")
    public void downloadPDF(@PathVariable Long id, HttpServletResponse response) {
        try {
            logger.info("Memproses permintaan unduh PDF untuk Lembur ID: {}", id);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            lemburPDF.generatePDF(id, baos);

            // Set response headers
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "attachment; filename=lembur.pdf");
            response.setContentLength(baos.size());

            // Write PDF content to response output stream
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();

            logger.info("PDF berhasil dibuat untuk Lembur ID: {}", id);
        } catch (IOException e) {
            logger.error("Gagal mengunduh PDF untuk Lembur ID: {}", id, e);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengunduh PDF untuk Lembur ID: {}", id, e);
        }
    }

    @GetMapping("/lembur/getall")
    public ResponseEntity<List<Lembur>> getAllLembur() {
        try {
            logger.info("Mengambil semua data Lembur");
            List<Lembur> lemburList = lemburService.getAllLembur();
            return ResponseEntity.ok(lemburList);
        } catch (Exception e) {
            logger.error("Gagal mengambil data lembur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lembur/admin/{adminId}")
    public ResponseEntity<List<Lembur>> getAllByAdmin(@PathVariable Long adminId) {
        try {
            logger.info("Mengambil data lembur untuk Admin ID: {}", adminId);
            List<Lembur> lemburList = lemburService.getAllByAdmin(adminId);
            return ResponseEntity.ok(lemburList);
        } catch (NotFoundException e) {
            logger.error("Data lembur tidak ditemukan untuk Admin ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Gagal mengambil data lembur untuk Admin ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lembur/getByid/{id}")
    public ResponseEntity<Lembur> getLemburById(@PathVariable Long id) {
        try {
            logger.info("Mengambil data lembur untuk ID: {}", id);
            Lembur lembur = lemburService.getLemburById(id);
            return ResponseEntity.ok(lembur);
        } catch (NotFoundException e) {
            logger.error("Data lembur tidak ditemukan untuk ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Gagal mengambil data lembur untuk ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lembur/getByuserId/{userId}")
    public ResponseEntity<List<Lembur>> getLemburByUserId(@PathVariable Long userId) {
        try {
            logger.info("Mengambil data lembur untuk User ID: {}", userId);
            List<Lembur> lemburList = lemburService.getLemburByUserId(userId);
            return ResponseEntity.ok(lemburList);
        } catch (Exception e) {
            logger.error("Gagal mengambil data lembur untuk User ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/lembur/tambahLembur/{userId}")
    public ResponseEntity<Lembur> createLembur(@PathVariable Long userId, @RequestBody Lembur lembur) {
        try {
            logger.info("Menambahkan lembur untuk User ID: {}", userId);
            Lembur newLembur = lemburService.IzinLembur(userId, lembur);
            return ResponseEntity.status(HttpStatus.CREATED).body(newLembur);
        } catch (Exception e) {
            logger.error("Gagal menambahkan lembur untuk User ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/lembur/ubahLembur/{id}")
    public ResponseEntity<Lembur> updateLembur(@PathVariable Long id, @RequestBody Lembur lembur) {
        try {
            logger.info("Mengupdate lembur dengan ID: {}", id);
            Lembur updatedLembur = lemburService.updateLembur(id, lembur);
            return ResponseEntity.ok(updatedLembur);
        } catch (NotFoundException e) {
            logger.error("Data lembur tidak ditemukan untuk ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Gagal mengupdate lembur untuk ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/lembur/delete/{id}")
    public ResponseEntity<Void> deleteLembur(@PathVariable Long id) {
        try {
            logger.info("Menghapus lembur dengan ID: {}", id);
            lemburService.deleteLembur(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            logger.error("Data lembur tidak ditemukan untuk ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Gagal menghapus lembur untuk ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

