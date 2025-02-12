package com.example.absensireact.controller;

import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.impl.KoordinatImpl;
import com.example.absensireact.model.Koordinat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/koordinat")
@CrossOrigin(origins = "*")
@Controller
public class KoordinatController {

    private static final Logger logger = LoggerFactory.getLogger(KoordinatController.class);

    @Autowired
    private KoordinatImpl koordinatService;

    @GetMapping("/getAll-Koordinat")
    public ResponseEntity<List<Koordinat>> getAllKoordinat() {
        try {
            logger.info("Memproses permintaan GET semua koordinat.");
            List<Koordinat> koordinatList = koordinatService.getAllKoordinat();
            logger.info("Berhasil mendapatkan {} koordinat.", koordinatList.size());
            return ResponseEntity.ok(koordinatList);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil semua koordinat.", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Koordinat> getKoordinatById(@PathVariable Long id) {
        try {
            logger.info("Mencari koordinat dengan ID: {}", id);
            Optional<Koordinat> koordinat = koordinatService.getKoordinatById(id);
            if (koordinat.isPresent()) {
                logger.info("Koordinat ditemukan: {}", koordinat.get());
                return ResponseEntity.ok(koordinat.get());
            } else {
                logger.warn("Koordinat dengan ID {} tidak ditemukan.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mencari koordinat dengan ID {}.", id, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/tambahKoordinat/{idOrganisasi}")
    public ResponseEntity<Koordinat> tambah(@PathVariable Long idOrganisasi, @RequestBody Koordinat koordinat) {
        try {
            logger.info("Menambahkan koordinat baru untuk organisasi ID: {}", idOrganisasi);
            Koordinat savedKoordinat = koordinatService.tambahKoordinat(idOrganisasi, koordinat);
            logger.info("Berhasil menambahkan koordinat: {}", savedKoordinat);
            return ResponseEntity.ok(savedKoordinat);
        } catch (IllegalArgumentException e) {
            logger.error("Gagal menambahkan koordinat untuk organisasi ID {}. Kesalahan: {}", idOrganisasi, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menambahkan koordinat untuk organisasi ID {}.", idOrganisasi, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/ubah-koordinat/{id}")
    public ResponseEntity<Koordinat> updateKoordinat(@PathVariable Long id, @RequestBody Koordinat updatedKoordinat) {
        try {
            logger.info("Memperbarui koordinat dengan ID: {}", id);
            Koordinat updated = koordinatService.updateKoordinat(id, updatedKoordinat);
            logger.info("Berhasil memperbarui koordinat: {}", updated);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.warn("Koordinat dengan ID {} tidak ditemukan untuk diperbarui.", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat memperbarui koordinat dengan ID {}.", id, e);
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/delete-koordinat/{id}")
    public ResponseEntity<Void> deleteKoordinat(@PathVariable Long id) {
        try {
            logger.info("Menghapus koordinat dengan ID: {}", id);
            koordinatService.deleteKoordinat(id);
            logger.info("Berhasil menghapus koordinat dengan ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menghapus koordinat dengan ID {}.", id, e);
            return ResponseEntity.status(500).build();
        }
    }
}
