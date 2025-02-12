package com.example.absensireact.controller;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.service.OrganisasiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Controller
public class OrganisasiController {


    private final OrganisasiService organisasiService;

    public OrganisasiController(OrganisasiService organisasiService) {
        this.organisasiService = organisasiService;
    }
    private static final Logger logger = LoggerFactory.getLogger(OrganisasiController.class);

    @GetMapping("/organisasi/all")
    public ResponseEntity<List<Organisasi>> getAllOrganisasi() {
        try {
            logger.info("Fetching all organizations");
            List<Organisasi> organisasiList = organisasiService.getAllOrganisasi();
            return ResponseEntity.ok(organisasiList);
        } catch (Exception e) {
            logger.error("Error fetching all organizations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organisasi/all-by-admin/{idAdmin}")
    public ResponseEntity<List<Organisasi>> getAllOrganisasiByAdmin(@PathVariable Long idAdmin) {
        try {
            logger.info("Fetching organizations for admin ID: {}", idAdmin);
            List<Organisasi> organisasiList = organisasiService.getAllByAdmin(idAdmin);
            return ResponseEntity.ok(organisasiList);
        } catch (Exception e) {
            logger.error("Error fetching organizations for admin ID: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organisasi/getByAdmin/{idAdmin}")
    public ResponseEntity<Organisasi> getAllByAdmin(@PathVariable Long idAdmin) {
        try {
            logger.info("Fetching organization for admin ID: {}", idAdmin);
            Optional<Organisasi> organisasi = organisasiService.GetByIdAdmin(idAdmin);
            return organisasi.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching organization for admin ID: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organisasi/getById/{id}")
    public ResponseEntity<Organisasi> getOrganisasiById(@PathVariable Long id) {
        try {
            logger.info("Fetching organization by ID: {}", id);
            Optional<Organisasi> organisasi = organisasiService.GetOrganisasiById(id);
            return organisasi.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching organization by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organisasi/superadmin/{idSuperAdmin}")
    public ResponseEntity<List<Organisasi>> getAllOrganisasiBySuperAdmin(@PathVariable Long idSuperAdmin) {
        try {
            logger.info("Mengambil semua organisasi untuk superadmin dengan ID: {}", idSuperAdmin);

            List<Organisasi> organisasiList = organisasiService.getAllBySuperAdmin(idSuperAdmin);

            logger.info("Ditemukan {} organisasi untuk superadmin dengan ID: {}", organisasiList.size(), idSuperAdmin);
            return ResponseEntity.ok().body(organisasiList);
        } catch (NotFoundException e) {
            logger.error("Organisasi tidak ditemukan untuk superadmin dengan ID: {}", idSuperAdmin, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil organisasi untuk superadmin dengan ID: {}", idSuperAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/organisasi/tambahByIdAdmin/{idAdmin}/data")
    public ResponseEntity<Organisasi> tambahOrganisasiData(
            @PathVariable Long idAdmin,
            @RequestBody Organisasi organisasi) {
        try {
            logger.info("Adding organization for admin ID: {}", idAdmin);
            Organisasi savedOrganisasi = organisasiService.TambahOrganisasi(idAdmin, organisasi);
            return ResponseEntity.ok(savedOrganisasi);
        } catch (Exception e) {
            logger.error("Error adding organization for admin ID: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/organisasi/tammbahImageByOrg/{idAdmin}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> tambahOrganisasiImage(
            @PathVariable Long idAdmin,
            @RequestParam("organisasiId") Long organisasiId,
            @RequestPart("image") MultipartFile image) {
        try {
            logger.info("Uploading image for organization ID: {}, Admin ID: {}", organisasiId, idAdmin);
            organisasiService.saveOrganisasiImage(idAdmin, organisasiId, image);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error("Error uploading image for organization ID: {}, Admin ID: {}", organisasiId, idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/tambahByIdSuperAdmin/{idSuperAdmin}")
    public ResponseEntity<Organisasi> tambahOrganisasi(
            @PathVariable Long idSuperAdmin,
            @RequestParam Long idAdmin,
            @RequestBody Organisasi organisasi) throws IOException {
        try {
            logger.info("Menambahkan organisasi baru untuk superadmin ID: {} dan admin ID: {}", idSuperAdmin, idAdmin);

            Organisasi savedOrganisasi = organisasiService.TambahOrganisasiBySuperAdmin(idSuperAdmin, idAdmin, organisasi);

            logger.info("Organisasi berhasil ditambahkan dengan ID: {}", savedOrganisasi.getId());
            return ResponseEntity.ok(savedOrganisasi);
        } catch (Exception e) {
            logger.error("Gagal menambahkan organisasi untuk superadmin ID: {} dan admin ID: {}", idSuperAdmin, idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/putByIdAdmin/{idAdmin}")
    public ResponseEntity<Organisasi> ubahDataOrganisasi(
            @PathVariable Long idAdmin,
            @RequestBody Organisasi organisasi,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        try {
            logger.info("Mengubah data organisasi untuk admin ID: {}", idAdmin);

            Organisasi updatedOrganisasi = organisasiService.UbahDataOrgannisasi(idAdmin, organisasi, image);

            logger.info("Organisasi berhasil diperbarui untuk admin ID: {}", idAdmin);
            return ResponseEntity.ok(updatedOrganisasi);
        } catch (Exception e) {
            logger.error("Gagal mengubah data organisasi untuk admin ID: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete-sementara/{id}")
    public ResponseEntity<String> deleteSemenetara(@PathVariable Long id) {
        try {
            logger.info("Menghapus sementara organisasi dengan ID: {}", id);

            organisasiService.DeleteOrganisasiSementara(id);

            logger.info("Organisasi dengan ID {} berhasil dipindahkan ke sampah", id);
            return ResponseEntity.ok("Organisasi berhasil dipindahkan ke sampah");
        } catch (NotFoundException e) {
            logger.error("Organisasi tidak ditemukan dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organisasi tidak ditemukan dengan ID: " + id);
        } catch (Exception e) {
            logger.error("Gagal menghapus sementara organisasi dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan dalam menghapus organisasi.");
        }
    }

    @PutMapping(value = "/editById/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Organisasi> editOrganisasi(
            @PathVariable Long id,
            @RequestParam Long idAdmin,
            @RequestPart(value = "organisasi") String organisasiJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        try {
            logger.info("Mengedit organisasi dengan ID: {} untuk admin ID: {}", id, idAdmin);

            // Deserialize JSON string into Organisasi object
            ObjectMapper objectMapper = new ObjectMapper();
            Organisasi organisasi = objectMapper.readValue(organisasiJson, Organisasi.class);

            // Panggil service untuk mengedit organisasi
            Organisasi updatedOrganisasi = organisasiService.EditByid(id, idAdmin, organisasi, image);

            logger.info("Organisasi dengan ID {} berhasil diperbarui", id);
            return ResponseEntity.ok(updatedOrganisasi);
        } catch (Exception e) {
            logger.error("Gagal mengedit organisasi dengan ID: {} untuk admin ID: {}", id, idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/organisasi/delete/{id}")
    public ResponseEntity<Void> deleteOrganisasi(@PathVariable Long id) {
        try {
            logger.info("Deleting organization by ID: {}", id);
            organisasiService.deleteOrganisasi(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting organization by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/organisasi/ubah-foto/{id}")
    public ResponseEntity<?> EditFotoOrganisasi(@PathVariable Long id, @RequestPart MultipartFile image) {
        try {
            logger.info("Updating organization photo for ID: {}", id);
            Organisasi updatedOrganisasi = organisasiService.uploadImage(id, image);
            return ResponseEntity.ok(updatedOrganisasi);
        } catch (IOException e) {
            logger.error("Error updating photo for organization ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (NotFoundException e) {
            logger.error("Organization not found for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}