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
import com.example.absensireact.model.Admin;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@Controller
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;


    @Autowired
    private ExcelDataAdmin excelDataAdmin;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


//    Kelas

    @PostMapping("/admin/importKelas/{idAdmin}")
    public ResponseEntity<String> importKelasData(@RequestPart("file") MultipartFile file, @PathVariable Long idAdmin) {
        logger.info("Importing Kelas data for admin ID: {}", idAdmin);
        return adminRepository.findById(idAdmin).map(admin -> {
            try {
                excelDataAdmin.importKelas(file, admin);
                logger.info("Successfully imported Kelas data for admin ID: {}", idAdmin);
                return ResponseEntity.ok("Admin data imported successfully");
            } catch (IOException e) {
                logger.error("Failed to import admin data: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import admin data");
            }
        }).orElseGet(() -> {
            logger.error("SuperAdmin not found with ID: {}", idAdmin);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SuperAdmin not found");
        });
    }

    @GetMapping("/admin/kelas/templateKelas")
    public void downloadImportTemplateKelas(HttpServletResponse response) {
        try {
            logger.info("Downloading Kelas import template");
            ExcelDataAdmin.downloadTemplateImportKelas(response);
        } catch (IOException e) {
            logger.error("Error generating Kelas template: {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            try {
                response.getWriter().write("Error occurred while generating template");
            } catch (IOException ioException) {
                logger.error("Error writing response: {}", ioException.getMessage());
            }
        }
    }
    @GetMapping("/admin/kelas/export")
    public void exportKelas(@RequestParam Long idAdmin, HttpServletResponse response) {
        try {
            logger.info("Memulai export data kelas untuk idAdmin: {}", idAdmin);
            excelDataAdmin.exportKelas(idAdmin, response);
            logger.info("Berhasil export data kelas untuk idAdmin: {}", idAdmin);
        } catch (IOException e) {
            logger.error("Gagal export data kelas untuk idAdmin: {}", idAdmin, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /** 📌 Import Data Organisasi */
    @PostMapping("/admin/importOrganisasi/{idAdmin}")
    public ResponseEntity<String> importAdminData(@RequestPart("file") MultipartFile file, @PathVariable Long idAdmin) {
        logger.info("Memulai import data organisasi untuk idAdmin: {}", idAdmin);

        return adminRepository.findById(idAdmin).map(admin -> {
            try {
                excelDataAdmin.importOrganisasi(file, admin);
                logger.info("Sukses import data organisasi untuk idAdmin: {}", idAdmin);
                return ResponseEntity.ok("Admin data imported successfully");
            } catch (IOException e) {
                logger.error("Gagal import data organisasi untuk idAdmin: {}", idAdmin, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import admin data");
            }
        }).orElseGet(() -> {
            logger.warn("Admin dengan id {} tidak ditemukan saat import data organisasi", idAdmin);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SuperAdmin not found");
        });
    }

    /** 📌 Download Template Import Organisasi */
    @GetMapping("/admin/organisasi/templateOrganisasi")
    public void downloadImportTemplateOrganisasi(HttpServletResponse response) {
        try {
            logger.info("Memulai download template import organisasi");
            ExcelDataAdmin.downloadTemplateImportOrganisasi(response);
            logger.info("Sukses download template import organisasi");
        } catch (IOException e) {
            logger.error("Gagal download template import organisasi", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            try {
                response.getWriter().write("Error occurred while generating template");
            } catch (IOException ioException) {
                logger.error("Gagal menulis pesan error ke response", ioException);
            }
        }
    }

    /** 📌 Export Data Organisasi */
    @GetMapping("/admin/organisasi/export")
    public void exportOrganisasi(@RequestParam Long idAdmin, HttpServletResponse response) {
        try {
            logger.info("Memulai export data organisasi untuk idAdmin: {}", idAdmin);
            excelDataAdmin.exportOrganisasi(idAdmin, response);
            logger.info("Berhasil export data organisasi untuk idAdmin: {}", idAdmin);
        } catch (IOException e) {
            logger.error("Gagal export data organisasi untuk idAdmin: {}", idAdmin, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @PostMapping("/admin/validasi-code")
    public void requestPasswordReset(@RequestBody VerifyCode verifyCode) {
        try {
            logger.info("Validating reset code for email: {}", verifyCode.getEmail());
            adminService.validasiCodeUniqResPass(verifyCode);
        } catch (NotFoundException e) {
            logger.error("Invalid reset code or email: {}", verifyCode.getEmail());
            throw new BadRequestException("Invalid reset code or email.");
        }
    }

    @PutMapping("/admin/ubahPassByForgot")
    public void resetPassword(@RequestBody ResetPassDTO resetPassDTO) {
        try {
            logger.info("Resetting password for email: {}", resetPassDTO.getEmail());
            adminService.ubahPassByForgot(resetPassDTO);
        } catch (NotFoundException e) {
            logger.error("Email not found: {}", resetPassDTO.getEmail());
            throw new BadRequestException("Email not found.");
        } catch (BadRequestException e) {
            logger.error("Password does not match for email: {}", resetPassDTO.getEmail());
            throw new BadRequestException("Password does not match.");
        }
    }
    @PostMapping("/admin/forgot_password")
    public CommonResponse<ForGotPass> sendEmail(@RequestBody ForGotPass forGotPass) {
        try {
            logger.info("Menerima permintaan lupa password untuk email: {}", forGotPass.getEmail());
            return ResponseHelper.ok(adminService.sendEmail(forGotPass));
        } catch (MessagingException e) {
            logger.error("Gagal mengirim email ke: {}", forGotPass.getEmail(), e);
            throw new RuntimeException("Terjadi kesalahan dalam pengiriman email", e);
        }
    }

    @PostMapping("/admin/register")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        try {
            logger.info("Menerima permintaan pendaftaran admin dengan username: {}", admin.getUsername());
            Admin registeredAdmin = adminService.RegisterAdmin(admin);
            return new ResponseEntity<>(registeredAdmin, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Gagal mendaftarkan admin dengan username: {}", admin.getUsername(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/register-by-superadmin/{idSuperAdmin}")
    public ResponseEntity<Admin> registerBySuperAdmin(@PathVariable Long idSuperAdmin, @RequestBody Admin admin) {
        try {
            logger.info("Menerima permintaan registrasi oleh superadmin ID: {}", idSuperAdmin);
            Admin registeredAdmin = adminService.RegisterBySuperAdmin(idSuperAdmin, admin);
            return new ResponseEntity<>(registeredAdmin, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Gagal registrasi oleh superadmin ID: {}", idSuperAdmin, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/getById/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        try {
            logger.info("Mengambil data admin dengan ID: {}", id);
            Admin admin = adminService.getById(id);
            return new ResponseEntity<>(admin, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Gagal mengambil data admin dengan ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        try {
            logger.info("Mengambil semua data admin");
            List<Admin> admins = adminService.getAll();
            return new ResponseEntity<>(admins, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Gagal mengambil semua data admin", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/ubah-foto/{id}")
    public ResponseEntity<?> editFotoAdmin(@PathVariable Long id, @RequestPart MultipartFile image) {
        try {
            logger.info("Mengubah foto admin dengan ID: {}", id);
            Admin updatedAdmin = adminService.uploadImage(id, image);
            return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Gagal mengubah foto admin dengan ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            logger.error("Admin dengan ID: {} tidak ditemukan", id, e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/admin/get-all-by-super/{idSuperAdmin}")
    public ResponseEntity<List<Admin>> getAllAdminsBySuperAdmin(@PathVariable Long idSuperAdmin) {
        try {
            logger.info("Mengambil semua admin di bawah superadmin ID: {}", idSuperAdmin);
            List<Admin> admins = adminService.getAllBySuperAdmin(idSuperAdmin);
            return new ResponseEntity<>(admins, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Gagal mengambil data admin oleh superadmin ID: {}", idSuperAdmin, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/edit-password/{id}")
    public CommonResponse<Admin> putPassword(@RequestBody PasswordDTO password, @PathVariable Long id) {
        try {
            logger.info("Mengubah password untuk admin ID: {}", id);
            return ResponseHelper.ok(adminService.putPasswordAdmin(password, id));
        } catch (Exception e) {
            logger.error("Gagal mengubah password untuk admin ID: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan dalam mengubah password", e);
        }
    }

    @PutMapping("/admin/edit/{id}/{idSuperAdmin}")
    public ResponseEntity<Admin> editAdmin(@PathVariable Long id, @PathVariable Long idSuperAdmin, @RequestBody Admin existingUser) {
        try {
            logger.info("Mengedit data admin ID: {} oleh superadmin ID: {}", id, idSuperAdmin);
            Admin admin = adminService.edit(id, idSuperAdmin, existingUser);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            logger.error("Gagal mengedit admin ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/edit-email-username/{id}")
    public ResponseEntity<Admin> editEmailUsername(@PathVariable Long id, @RequestBody Admin updateAdmin) {
        try {
            logger.info("Mengubah email/username admin ID: {}", id);
            Admin updatedAdmin = adminService.ubahUsernamedanemail(id, updateAdmin);
            return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Gagal mengubah email/username admin ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteAdmin(@PathVariable Long id) {
        try {
            logger.info("Menghapus admin dengan ID: {}", id);
            Map<String, Boolean> response = adminService.delete(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Gagal menghapus admin dengan ID: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/admin/delete-sementara/{id}")
    public ResponseEntity<String> deleteSemenetara(@PathVariable Long id) {
        try {
            logger.info("Temporarily deleting admin with ID: {}", id);
            adminService.DeleteAdminSementara(id);
            return ResponseEntity.ok("Admin berhasil dipindahkan ke sampah");
        } catch (NotFoundException e) {
            logger.error("Admin not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin tidak ditemukan dengan id: " + id);
        }
    }

    @PutMapping("/admin/pemulihan-kelas/{id}")
    public ResponseEntity<String> PemulihanAdmin(@PathVariable Long id) {
        try {
            logger.info("Restoring admin with ID: {}", id);
            adminService.PemulihanDataAdmin(id);
            return ResponseEntity.ok("Admin berhasil Dipulihkan");
        } catch (NotFoundException e) {
            logger.error("Admin not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin tidak ditemukan dengan id: " + id);
        }
    }
}
