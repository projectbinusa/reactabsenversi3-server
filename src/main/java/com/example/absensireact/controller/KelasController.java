package com.example.absensireact.controller;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Jabatan;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.model.Shift;
import com.example.absensireact.service.KelasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/kelas")
@CrossOrigin(origins = "*")
@Controller
public class KelasController {

    private static final Logger logger = LoggerFactory.getLogger(KelasController.class);

    private final KelasService kelasService;

    public KelasController(KelasService kelasService) {
        this.kelasService = kelasService;
    }

    @GetMapping("/kelas/all")
    public ResponseEntity<List<Kelas>> getAllKelas() {
        try {
            logger.info("Mengambil semua data kelas.");
            List<Kelas> kelasList = kelasService.getAllKelas();
            return ResponseEntity.ok(kelasList);
        } catch (Exception e) {
            logger.error("Gagal mengambil semua data kelas.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<Kelas> getKelasById(@PathVariable Long id) {
        try {
            logger.info("Mengambil data kelas dengan ID: {}", id);
            Optional<Kelas> kelas = kelasService.getKelasById(id);
            return kelas.map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        logger.warn("Kelas dengan ID {} tidak ditemukan.", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Gagal mengambil data kelas dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getALlByOrganisasi/{idOrganisasi}")
    public ResponseEntity<List<Kelas>> getALlByOrganisasi(@PathVariable Long idOrganisasi) {
        try {
            logger.info("Mengambil semua data kelas berdasarkan organisasi ID: {}", idOrganisasi);
            List<Kelas> kelasList = kelasService.getALlByOrganisasi(idOrganisasi);
            return ResponseEntity.ok(kelasList);
        } catch (Exception e) {
            logger.error("Gagal mengambil kelas berdasarkan organisasi ID: {}", idOrganisasi, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAllByAdmin/{idAdmin}")
    public ResponseEntity<List<Kelas>> getALlByAdmin(@PathVariable Long idAdmin) {
        try {
            logger.info("Mengambil semua data kelas berdasarkan admin ID: {}", idAdmin);
            List<Kelas> kelasList = kelasService.getAllByIdAdmin(idAdmin);
            return ResponseEntity.ok(kelasList);
        } catch (Exception e) {
            logger.error("Gagal mengambil kelas berdasarkan admin ID: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/editKelasById/{id}")
    public ResponseEntity<Kelas> editJabatanById(@PathVariable("id") Long id, @RequestBody Kelas kelas) {
        try {
            logger.info("Mengedit kelas dengan ID: {}", id);
            Kelas updatedKelas = kelasService.editKelasById(id, kelas);
            return ResponseEntity.ok(updatedKelas);
        } catch (Exception e) {
            logger.error("Gagal mengedit kelas dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/tambah")
    public ResponseEntity<Kelas> tambahKelas(@RequestBody Kelas kelas, @RequestParam Long idOrganisasi, @RequestParam Long idAdmin) {
        try {
            logger.info("Menambahkan kelas baru dengan Organisasi ID: {} dan Admin ID: {}", idOrganisasi, idAdmin);
            Kelas kelasBaru = kelasService.tambahKelas(kelas, idOrganisasi, idAdmin);
            return new ResponseEntity<>(kelasBaru, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Gagal menambahkan kelas baru.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/hasRelations/{id}")
    public ResponseEntity<Boolean> hasRelations(@PathVariable Long id) {
        try {
            logger.info("Memeriksa apakah kelas ID: {} memiliki relasi.", id);
            boolean hasRelations = kelasService.checkIfHasRelations(id);
            return ResponseEntity.ok(hasRelations);
        } catch (Exception e) {
            logger.error("Gagal memeriksa relasi untuk kelas ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteKelas/{id}")
    public ResponseEntity<Void> deleteKelas(@PathVariable Long id) {
        try {
            logger.info("Menghapus kelas dengan ID: {}", id);
            kelasService.deleteKelas(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Gagal menghapus kelas dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete-sementara/{id}")
    public ResponseEntity<String> deleteSemenetara(@PathVariable Long id) {
        try {
            logger.info("Menghapus sementara kelas dengan ID: {}", id);
            kelasService.DeleteKelasSementara(id);
            return ResponseEntity.ok("Kelas berhasil dipindahkan ke sampah");
        } catch (NotFoundException e) {
            logger.warn("Kelas tidak ditemukan dengan ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kelas tidak ditemukan dengan ID: " + id);
        } catch (Exception e) {
            logger.error("Gagal menghapus sementara kelas dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/pemulihan-kelas/{id}")
    public ResponseEntity<String> PemulihanKelas(@PathVariable Long id) {
        try {
            logger.info("Memulihkan kelas dengan ID: {}", id);
            kelasService.PemulihanDataKelas(id);
            return ResponseEntity.ok("Kelas berhasil Dipulihkan");
        } catch (NotFoundException e) {
            logger.warn("Kelas tidak ditemukan dengan ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kelas tidak ditemukan dengan ID: " + id);
        } catch (Exception e) {
            logger.error("Gagal memulihkan kelas dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

