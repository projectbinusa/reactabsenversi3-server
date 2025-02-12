package com.example.absensireact.controller;

import com.example.absensireact.dto.ForGotPass;
import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.dto.ResetPassDTO;
import com.example.absensireact.dto.VerifyCode;
import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.CommonResponse;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exception.ResponseHelper;
import com.example.absensireact.exel.ExcelDataAdmin;
import com.example.absensireact.exel.ExportSuperAdmin;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.SuperAdmin;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.service.SuperAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    private static final Logger logger = LoggerFactory.getLogger(SuperAdminController.class);
    private final SuperAdminService superAdminService;
    private final ExportSuperAdmin exportSuperAdmin;
    private final SuperAdminRepository superAdminRepository;

    public SuperAdminController(SuperAdminService superAdminService, ExportSuperAdmin exportSuperAdmin, SuperAdminRepository superAdminRepository) {
        this.superAdminService = superAdminService;
        this.exportSuperAdmin = exportSuperAdmin;
        this.superAdminRepository = superAdminRepository;
    }

    @GetMapping("/admin/export")
    public void exportAdmin(@RequestParam Long superadminId, HttpServletResponse response) throws IOException {
        try {
            logger.info("Exporting admin data for SuperAdmin ID: {}", superadminId);
            exportSuperAdmin.excelAdmin(superadminId, response);
        } catch (Exception e) {
            logger.error("Error exporting admin data: {}", e.getMessage());
            throw new IOException("Error exporting admin data", e);
        }
    }

    @PostMapping("/validasi-code")
    public void requestPasswordReset(@RequestBody VerifyCode verifyCode) {
        try {
            logger.info("Validating reset password code.");
            superAdminService.validasiCodeUniqResPass(verifyCode);
        } catch (NotFoundException e) {
            logger.error("Invalid reset code or email: {}", e.getMessage());
            throw new BadRequestException("Invalid reset code or email.");
        }
    }

    @PutMapping("/ubahPassByForgot")
    public void resetPassword(@RequestBody ResetPassDTO resetPassDTO) {
        try {
            logger.info("Resetting password.");
            superAdminService.ubahPassByForgot(resetPassDTO);
        } catch (NotFoundException e) {
            logger.error("Email not found: {}", e.getMessage());
            throw new BadRequestException("Email not found.");
        } catch (BadRequestException e) {
            logger.error("Password mismatch: {}", e.getMessage());
            throw new BadRequestException("Password does not match.");
        }
    }

    @PostMapping("/forgot_password")
    public CommonResponse<ForGotPass> sendEmail(@RequestBody ForGotPass forGotPass) throws MessagingException {
        logger.info("Sending password reset email.");
        return ResponseHelper.ok(superAdminService.sendEmail(forGotPass));
    }

    @PutMapping("/edit-email-username/{id}")
    public ResponseEntity<SuperAdmin> editemailusernamesuperadmin(@PathVariable Long id, @RequestBody SuperAdmin updateAdmin) {
        logger.info("Editing email and username for SuperAdmin ID: {}", id);
        SuperAdmin admin = superAdminService.ubahUsernamedanemail(id, updateAdmin);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @PutMapping("/ubah-foto/{id}")
    public ResponseEntity<?> editFotoSuperAdmin(@PathVariable Long id, @RequestPart MultipartFile image) {
        try {
            logger.info("Updating profile photo for SuperAdmin ID: {}", id);
            SuperAdmin updateSuperAdmin = superAdminService.uploadImage(id, image);
            return new ResponseEntity<>(updateSuperAdmin, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            logger.error("SuperAdmin not found: {}", id);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<SuperAdmin>> getAllSuperAdmin() {
        logger.info("Fetching all SuperAdmins.");
        List<SuperAdmin> superAdmins = superAdminService.getAllSuperAdmin();
        return ResponseEntity.ok(superAdmins);
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<SuperAdmin> getSuperAdminById(@PathVariable Long id) {
        logger.info("Fetching SuperAdmin by ID: {}", id);
        Optional<SuperAdmin> superAdmin = superAdminService.getSuperadminbyId(id);
        return superAdmin.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<SuperAdmin> register(@RequestBody SuperAdmin superAdmin) {
        logger.info("Registering a new SuperAdmin.");
        return ResponseEntity.ok(superAdminService.RegisterSuperAdmin(superAdmin));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSuperAdmin(@PathVariable Long id) throws IOException {
        try {
            logger.info("Deleting SuperAdmin ID: {}", id);
            superAdminService.deleteSuperAdmin(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting SuperAdmin: {}", e.getMessage());
            throw new IOException("Error deleting SuperAdmin", e);
        }
    }

    @PostMapping("/import/data-admin/{superAdminId}")
    public ResponseEntity<String> importAdmin(@PathVariable Long superAdminId, @RequestPart("file") MultipartFile file) {
        try {
            logger.info("Importing admin data for SuperAdmin ID: {}", superAdminId);
            exportSuperAdmin.importAdmin(superAdminId, file);
            return ResponseEntity.ok("Import berhasil!");
        } catch (Exception e) {
            logger.error("Error importing admin data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan saat mengimpor data: " + e.getMessage());
        }
    }
}
