package com.example.absensireact.controller;

import com.example.absensireact.model.Jabatan;
import com.example.absensireact.service.JabatanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Controller
public class JabatanController {

    private static final Logger logger = LoggerFactory.getLogger(JabatanController.class);
    private final JabatanService jabatanService;

    public JabatanController(JabatanService jabatanService) {
        this.jabatanService = jabatanService;
    }

    @GetMapping("/jabatan/all")
    public ResponseEntity<List<Jabatan>> getAllJabatan() {
        try {
            logger.info("Mengambil semua data Jabatan.");
            List<Jabatan> jabatans = jabatanService.getAllJabatan();
            logger.info("Berhasil mengambil {} data Jabatan.", jabatans.size());
            return ResponseEntity.ok(jabatans);
        } catch (Exception e) {
            logger.error("Gagal mengambil data Jabatan.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/jabatan/getbyid/{idJabatan}")
    public ResponseEntity<Jabatan> getJabatanById(@PathVariable("idJabatan") Long idJabatan) {
        try {
            logger.info("Mencari Jabatan dengan id: {}", idJabatan);
            Optional<Jabatan> jabatan = jabatanService.getJabatanById(idJabatan);
            return jabatan.map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        logger.warn("Jabatan dengan id {} tidak ditemukan.", idJabatan);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Gagal mencari Jabatan dengan id: {}", idJabatan, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/jabatan/getByAdmin/{adminId}")
    public ResponseEntity<List<Jabatan>> getJabatanByAdminId(@PathVariable Long adminId) {
        try {
            logger.info("Mencari Jabatan berdasarkan Admin ID: {}", adminId);
            List<Jabatan> jabatans = jabatanService.getJabatanByAdminId(adminId);
            logger.info("Ditemukan {} Jabatan untuk Admin ID: {}", jabatans.size(), adminId);
            return ResponseEntity.ok(jabatans);
        } catch (Exception e) {
            logger.error("Gagal mencari Jabatan berdasarkan Admin ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/jabatan/add/{adminId}")
    public ResponseEntity<Jabatan> addJabatan(@PathVariable Long adminId, @RequestBody Jabatan jabatan) {
        try {
            logger.info("Menambahkan Jabatan baru untuk Admin ID: {}", adminId);
            Jabatan newJabatan = jabatanService.addJabatan(adminId, jabatan);
            logger.info("Berhasil menambahkan Jabatan baru dengan ID: {}", newJabatan.getIdJabatan());
            return ResponseEntity.ok(newJabatan);
        } catch (Exception e) {
            logger.error("Gagal menambahkan Jabatan untuk Admin ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/jabatan/getBySuper/{idSuperAdmin}")
    public ResponseEntity<List<Jabatan>> getJabatanBySuperAdminId(@PathVariable Long idSuperAdmin) {
        try {
            logger.info("Mencari Jabatan berdasarkan Super Admin ID: {}", idSuperAdmin);
            List<Jabatan> jabatans = jabatanService.getJabatanBySuperAdminId(idSuperAdmin);
            logger.info("Ditemukan {} Jabatan untuk Super Admin ID: {}", jabatans.size(), idSuperAdmin);
            return ResponseEntity.ok(jabatans);
        } catch (Exception e) {
            logger.error("Gagal mencari Jabatan berdasarkan Super Admin ID: {}", idSuperAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/jabatan/edit/{adminId}")
    public ResponseEntity<Jabatan> editJabatan(@PathVariable Long adminId, @RequestBody Jabatan jabatan) {
        try {
            logger.info("Mengedit Jabatan untuk Admin ID: {}", adminId);
            Jabatan updatedJabatan = jabatanService.editJabatan(adminId, jabatan);
            logger.info("Berhasil mengedit Jabatan dengan ID: {}", updatedJabatan.getIdJabatan());
            return ResponseEntity.ok(updatedJabatan);
        } catch (Exception e) {
            logger.error("Gagal mengedit Jabatan untuk Admin ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/jabatan/editById/{idJabatan}")
    public ResponseEntity<Jabatan> editJabatanById(@PathVariable("idJabatan") Long idJabatan, @RequestBody Jabatan jabatan) {
        try {
            logger.info("Mengedit Jabatan dengan ID: {}", idJabatan);
            Jabatan updatedJabatan = jabatanService.editJabatanById(idJabatan, jabatan);
            logger.info("Berhasil mengedit Jabatan dengan ID: {}", updatedJabatan.getIdJabatan());
            return ResponseEntity.ok(updatedJabatan);
        } catch (Exception e) {
            logger.error("Gagal mengedit Jabatan dengan ID: {}", idJabatan, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/jabatan/delete/{idJabatan}")
    public ResponseEntity<Map<String, Boolean>> deleteJabatan(@PathVariable("idJabatan") Long idJabatan) {
        try {
            logger.info("Menghapus Jabatan dengan ID: {}", idJabatan);
            Map<String, Boolean> response = jabatanService.deleteJabatan(idJabatan);
            if (response.get("Deleted")) {
                logger.info("Berhasil menghapus Jabatan dengan ID: {}", idJabatan);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Gagal menghapus Jabatan dengan ID: {}", idJabatan);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menghapus Jabatan dengan ID: {}", idJabatan, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
