package com.example.absensireact.controller;

import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.exception.CommonResponse;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exception.ResponseHelper;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.*;
import com.example.absensireact.service.OrangTuaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orang-tua")
@CrossOrigin(origins = "*")
@Controller
public class OrangTuaController {

    private static final Logger log = LoggerFactory.getLogger(OrangTuaController.class);

    @Autowired
    OrangTuaService orangTuaImpl;

    @Autowired
    com.example.absensireact.exel.ExcelOrtu excelOrtu;

    @Autowired
    private com.example.absensireact.exel.ImportOrtu importOrtu;

    @Autowired
    private OrangTuaService orangTuaService;

    @GetMapping("/{id}/admin")
    public ResponseEntity<?> getAdminByOrangTuaId(@PathVariable Long id) {
        try {
            log.info("Fetching admin for OrangTua with id: {}", id);
            Admin admin = orangTuaService.getAdminByOrangTuaId(id);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            log.error("Error fetching admin for OrangTua with id: {} - {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving data.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrangTua() {
        try {
            log.info("Fetching all OrangTua data.");
            List<OrangTua> list = orangTuaService.getAllOrangTua();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("Error fetching all OrangTua data - {}", e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving data.");
        }
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<?> getOrangTuaById(@PathVariable Long id) {
        try {
            log.info("Fetching OrangTua with id: {}", id);
            Optional<OrangTua> orangTua = orangTuaService.getOrangTuaById(id);
            return orangTua.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching OrangTua with id: {} - {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving data.");
        }
    }

    @GetMapping("/getALlBySuperAdmin/{idAdmin}")
    public ResponseEntity<List<OrangTua>> getALlBySuperAdmin(@PathVariable Long idAdmin) {
        List<OrangTua> orangtuaList =  orangTuaService.getAllByAdmin(idAdmin);
        return ResponseEntity.ok(orangtuaList);
    }

    @PostMapping("/tambah/{idAdmin}")
    public ResponseEntity<?> tambahOrangtua(@PathVariable Long idAdmin, @RequestBody OrangTua orangTua) {
        try {
            log.info("Adding new OrangTua for Admin id: {}", idAdmin);
            orangTua.setId(null);
            OrangTua orangTuaBaru = orangTuaService.tambahOrangTua(idAdmin, orangTua);
            return ResponseEntity.status(201).body(orangTuaBaru);
        } catch (Exception e) {
            log.error("Error adding OrangTua for Admin id: {} - {}", idAdmin, e.getMessage());
            return ResponseEntity.status(500).body("Error adding OrangTua.");
        }
    }

    @PutMapping("/editOrtuById/{id}/{idAdmin}")
    public ResponseEntity<?> editOrangTua(@PathVariable Long id, @PathVariable Long idAdmin, @RequestBody OrangTua orangTua) {
        try {
            log.info("Editing OrangTua with id: {}, Admin id: {}", id, idAdmin);
            OrangTua updatedOrangtua = orangTuaService.editOrangTuaById(id, idAdmin, orangTua);
            return ResponseEntity.ok(updatedOrangtua);
        } catch (Exception e) {
            log.error("Error editing OrangTua with id: {}, Admin id: {} - {}", id, idAdmin, e.getMessage());
            return ResponseEntity.status(500).body("Error updating OrangTua.");
        }
    }

    @PutMapping(path = "/edit-password/{id}")
    public CommonResponse<OrangTua> putPassword(@RequestBody PasswordDTO password, @PathVariable Long id) {
        try {
            log.info("Memproses perubahan password untuk idOrangTua: {}", id);
            OrangTua updatedOrangTua = orangTuaService.putPasswordOrangTua(password, id);
            log.info("Password berhasil diperbarui untuk idOrangTua: {}", id);
            return ResponseHelper.ok(updatedOrangTua);
        } catch (Exception e) {
            log.error("Terjadi kesalahan saat memperbarui password untuk idOrangTua: {}", id, e);
            throw new RuntimeException("Gagal memperbarui password", e);
        }
    }

    @DeleteMapping("/deleteOrangTua/{id}")
    public ResponseEntity<?> deleteOrangTua(@PathVariable Long id) {
        try {
            log.info("Deleting OrangTua with id: {}", id);
            orangTuaService.deleteOrangTua(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting OrangTua with id: {} - {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Error deleting OrangTua.");
        }
    }

    @PutMapping("/orang-tua/ubah-foto/{id}")
    public ResponseEntity<?> editFotoOrangTua(@PathVariable Long id, @RequestPart MultipartFile image) {
        try {
            log.info("Updating photo for OrangTua id: {}", id);
            OrangTua updatedOrangTua = orangTuaService.uploadImage(id, image);
            return ResponseEntity.ok(updatedOrangTua);
        } catch (IOException e) {
            log.error("IO error updating photo for OrangTua id: {} - {}", id, e.getMessage());
            return ResponseEntity.status(500).body("File processing error.");
        } catch (NotFoundException e) {
            log.error("OrangTua not found for id: {}", id);
            return ResponseEntity.status(404).body("OrangTua not found.");
        }
    }

    @PutMapping("/edit-email-username/{id}")
    public ResponseEntity<OrangTua> editemailusername(@PathVariable Long id, @RequestBody OrangTua updateOrangTua) {
        try {
            log.info("Memproses perubahan email dan username untuk idOrangTua: {}", id);
            OrangTua orangTua = orangTuaImpl.ubahUsernamedanemail(id, updateOrangTua);
            log.info("Email dan username berhasil diperbarui untuk idOrangTua: {}", id);
            return ResponseEntity.ok(orangTua);
        } catch (Exception e) {
            log.error("Terjadi kesalahan saat memperbarui email dan username untuk idOrangTua: {}", id, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/export/data-orang-tua/{idAdmin}")
    public void exportOrangTua(@PathVariable Long idAdmin, HttpServletResponse response) {
        try {
            log.info("Memulai proses ekspor data Orang Tua untuk idAdmin: {}", idAdmin);
            excelOrtu.excelOrangTua(idAdmin, response);
            log.info("Ekspor data Orang Tua berhasil untuk idAdmin: {}", idAdmin);
        } catch (IOException e) {
            log.error("Gagal mengekspor data Orang Tua untuk idAdmin: {}", idAdmin, e);
        }
    }

    @GetMapping("/download/template-orang-tua")
    public void templateExcelWaliMurid(HttpServletResponse response) {
        try {
            log.info("Memulai proses unduh template Orang Tua");
            excelOrtu.templateExcelWaliMurid(response);
            log.info("Template Orang Tua berhasil diunduh");
        } catch (IOException e) {
            log.error("Gagal mengunduh template Orang Tua", e);
        }
    }

    @PostMapping("/import/data-orang-tua/{adminId}")
    public ResponseEntity<?> importOrangTua(@PathVariable Long adminId, @RequestPart("file") MultipartFile file) {
        try {
            log.info("Importing OrangTua data for Admin id: {}", adminId);
            List<String> errorMessages = importOrtu.importOrangTua(adminId, file);
            if (!errorMessages.isEmpty()) {
                return ResponseEntity.status(400).body(errorMessages);
            }
            return ResponseEntity.ok("Successfully imported data.");
        } catch (IOException e) {
            log.error("IO error importing data for Admin id: {} - {}", adminId, e.getMessage());
            return ResponseEntity.status(500).body("File processing error.");
        }
    }

    @DeleteMapping("/delete-sementara/{id}")
    public ResponseEntity<?> deleteSementara(@PathVariable Long id) {
        try {
            log.info("Moving OrangTua id: {} to temporary delete", id);
            orangTuaService.DeleteOrtuSementara(id);
            return ResponseEntity.ok("Ortu berhasil dipindahkan ke sampah");
        } catch (NotFoundException e) {
            log.error("OrangTua not found for id: {}", id);
            return ResponseEntity.status(404).body("OrangTua tidak ditemukan.");
        }
    }

    @PutMapping("/pemulihan-kelas/{id}")
    public ResponseEntity<?> pemulihanOrtu(@PathVariable Long id) {
        try {
            log.info("Recovering OrangTua id: {}", id);
            orangTuaService.PemulihanDataOrtu(id);
            return ResponseEntity.ok("Ortu berhasil Dipulihkan");
        } catch (NotFoundException e) {
            log.error("OrangTua not found for id: {}", id);
            return ResponseEntity.status(404).body("OrangTua tidak ditemukan.");
        }
    }
}