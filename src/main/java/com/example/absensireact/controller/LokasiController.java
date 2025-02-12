package com.example.absensireact.controller;

import com.example.absensireact.dto.AdminDTO;
import com.example.absensireact.dto.LokasiDTO;
import com.example.absensireact.dto.OrganisasiDTO;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Lokasi;
import com.example.absensireact.service.LokasiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/lokasi")
@CrossOrigin(origins = "*")
@Controller
public class LokasiController {

    private static final Logger logger = LoggerFactory.getLogger(LokasiController.class);

    private final LokasiService lokasiService;

    public LokasiController(LokasiService lokasiService) {
        this.lokasiService = lokasiService;
    }

    @PostMapping("/add/superadmin/{idSuperAdmin}")
    public ResponseEntity<Lokasi> tambahLokasiBySuperAdmin(
            @PathVariable("idSuperAdmin") Long idSuperAdmin,
            @RequestParam("idOrganisasi") Long idOrganisasi,
            @RequestBody Lokasi lokasi) {
        try {
            logger.info("Menambahkan lokasi oleh superadmin dengan ID: {}", idSuperAdmin);
            Lokasi savedLokasi = lokasiService.tambahLokasiBySuperAdmin(idSuperAdmin, lokasi, idOrganisasi);
            return ResponseEntity.ok(savedLokasi);
        } catch (Exception e) {
            logger.error("Gagal menambahkan lokasi oleh superadmin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/tambah/{idAdmin}")
    public ResponseEntity<Lokasi> tambahLokasi(@PathVariable("idAdmin") Long idAdmin, @RequestBody Lokasi lokasi, @RequestParam Long idOrganisasi) {
        try {
            logger.info("Menambahkan lokasi oleh admin dengan ID: {}", idAdmin);
            Lokasi lokasiBaru = lokasiService.tambahLokasi(idAdmin, lokasi, idOrganisasi);
            return ResponseEntity.ok(lokasiBaru);
        } catch (Exception e) {
            logger.error("Gagal menambahkan lokasi: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<List<LokasiDTO>> getAllLokasi() {
        logger.info("Mengambil semua data lokasi");
        return ResponseEntity.ok(lokasiService.getAllLokasi());
    }

    @GetMapping("/GetById/{id}")
    public ResponseEntity<LokasiDTO> getLokasiById(@PathVariable Long id) {
        try {
            logger.info("Mengambil lokasi dengan ID: {}", id);
            LokasiDTO lokasiDTO = lokasiService.getLokasiById(id);
            return ResponseEntity.ok(lokasiDTO);
        } catch (Exception e) {
            logger.error("Gagal mengambil lokasi dengan ID: {} - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/editByIdLokasi/{idLokasi}")
    public ResponseEntity<Lokasi> updateLokasiByIdLokasi(@PathVariable Long idLokasi, @RequestBody Lokasi lokasiDetails) {
        try {
            logger.info("Mengupdate lokasi dengan ID: {}", idLokasi);
            Lokasi updatedLokasi = lokasiService.updateLokasiByIdlokasi(idLokasi, lokasiDetails);
            return ResponseEntity.ok(updatedLokasi);
        } catch (NotFoundException e) {
            logger.error("Lokasi dengan ID {} tidak ditemukan", idLokasi);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Gagal mengupdate lokasi dengan ID {}: {}", idLokasi, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getByIdLokasi/{idLokasi}")
    public ResponseEntity<Lokasi> getByIdLokasi(@PathVariable Long idLokasi) {
        try {
            logger.info("Mengambil lokasi dengan ID: {}", idLokasi);
            Optional<Lokasi> lokasi = lokasiService.getByIdLokasi(idLokasi);
            return lokasi.map(ResponseEntity::ok).orElseGet(() -> {
                logger.warn("Lokasi dengan ID {} tidak ditemukan", idLokasi);
                return ResponseEntity.notFound().build();
            });
        } catch (Exception e) {
            logger.error("Gagal mengambil lokasi dengan ID {}: {}", idLokasi, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-admin/{idAdmin}")
    public ResponseEntity<List<Lokasi>>getLokasiByAdmin (@PathVariable Long idAdmin){
        List<Lokasi> lokasi = lokasiService.getAllByAdmin(idAdmin);
        return ResponseEntity.ok(lokasi);

    }

    @GetMapping("/GetOrganisasiById/{id}")
    public ResponseEntity<OrganisasiDTO> getOrganisasiById(@PathVariable Long id) {
        OrganisasiDTO organisasiDTO = lokasiService.getOrganisasiById(id);
        if (organisasiDTO != null) {
            return ResponseEntity.ok(organisasiDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/superadmin/{idSuperAdmin}")
    public List<Lokasi> getLokasiBySuperAdmin(@PathVariable Long idSuperAdmin) {
        return lokasiService.getAllBySuperAdmin(idSuperAdmin);
    }

    @PutMapping("/Update/{id}")
    public ResponseEntity<LokasiDTO> updateLokasi(@PathVariable Long id, @RequestBody LokasiDTO lokasiDTO) {
        LokasiDTO updatedLokasi = lokasiService.updateLokasi(id, lokasiDTO);
        return ResponseEntity.ok(updatedLokasi);
    }

    @DeleteMapping("/delete/{idLokasi}")
    public void deleteLokasi(@PathVariable Long idLokasi) {
        lokasiService.deleteLokasi(idLokasi);
    }

    @DeleteMapping("/delete-sementara/{idLokasi}")
    public ResponseEntity<String> deleteSemenetara(@PathVariable Long idLokasi) {
        try {
            logger.info("Menghapus sementara lokasi dengan ID: {}", idLokasi);
            lokasiService.DeleteLokasiSementara(idLokasi);
            return ResponseEntity.ok("Lokasi berhasil dipindahkan ke sampah");
        } catch (NotFoundException e) {
            logger.warn("Lokasi dengan ID {} tidak ditemukan", idLokasi);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lokasi tidak ditemukan dengan idLokasi: " + idLokasi);
        } catch (Exception e) {
            logger.error("Gagal menghapus sementara lokasi dengan ID {}: {}", idLokasi, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan saat menghapus lokasi");
        }
    }

    @PutMapping("/pemulihan-lokasi/{idLokasi}")
    public ResponseEntity<String> PemulihanLokasi(@PathVariable Long idLokasi) {
        try {
            logger.info("Melakukan pemulihan lokasi dengan ID: {}", idLokasi);
            lokasiService.PemulihanDataLokasi(idLokasi);
            return ResponseEntity.ok("Lokasi berhasil dipulihkan");
        } catch (NotFoundException e) {
            logger.warn("Lokasi dengan ID {} tidak ditemukan", idLokasi);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lokasi tidak ditemukan dengan idLokasi: " + idLokasi);
        } catch (Exception e) {
            logger.error("Gagal memulihkan lokasi dengan ID {}: {}", idLokasi, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan saat memulihkan lokasi");
        }
    }
}