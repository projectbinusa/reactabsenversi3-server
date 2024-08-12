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
import com.example.absensireact.model.User;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.service.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class SuperAdminController {
    private final SuperAdminService superAdminService;

    @Autowired
    private ExportSuperAdmin exportSuperAdmin;

    @Autowired
    private ExcelDataAdmin excelDataAdmin;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    //    admin
    @GetMapping("/superadmin/import/template")
    public ResponseEntity<Resource> downloadImportTemplate() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ExportSuperAdmin.generateAdminImportTemplate(outputStream);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=AdminImportTemplate.xlsx")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/superadmin/admin/export")
    public void exportAdmin (@RequestParam Long superadminId ,  HttpServletResponse response) throws IOException {
        exportSuperAdmin.excelAdmin(superadminId , response);
    }

    @PostMapping("/superadmin/import/{superadminId}")
    public ResponseEntity<String> importAdminData(@RequestPart("file") MultipartFile file, @PathVariable Long superadminId) {
        return superAdminRepository.findById(superadminId).map(superAdmin -> {
            try {
                exportSuperAdmin.importAdmin(file, superAdmin);
                return ResponseEntity.ok("Admin data imported successfully");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import admin data");
            }
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("SuperAdmin not found"));
    }


    @PostMapping("/superadmin/validasi-code")
    public void requestPasswordReset(@RequestBody VerifyCode verifyCode) {
        try {
            superAdminService.validasiCodeUniqResPass(verifyCode);
        } catch (NotFoundException e) {
            throw new BadRequestException("Invalid reset code or email.");
        }
    }

    @PutMapping("/superadmin/ubahPassByForgot")
    public void resetPassword(@RequestBody ResetPassDTO resetPassDTO) {
        try {
            superAdminService.ubahPassByForgot(resetPassDTO);
        } catch (NotFoundException e) {
            throw new BadRequestException("Email not found.");
        } catch (BadRequestException e) {
            throw new BadRequestException("Password does not match.");
        }
    }

    @PostMapping("/superadmin/forgot_password")
    public CommonResponse<ForGotPass> sendEmail(@RequestBody ForGotPass forGotPass) throws MessagingException {
        return ResponseHelper.ok(superAdminService.sendEmail(forGotPass));

    }
    @PutMapping("/superadmin/edit-email-username/{id}")
    public ResponseEntity<SuperAdmin> editemailusernamesuperadmin(@PathVariable Long id, @RequestBody SuperAdmin updateAdmin) {
        SuperAdmin Admin = superAdminService.ubahUsernamedanemail(id , updateAdmin);
        return new ResponseEntity<>(Admin, HttpStatus.OK);
    }

    @PutMapping("/superadmin/ubah-foto/{id}")
    public ResponseEntity<?>EditFotoSuperAdmin(@PathVariable Long id , @RequestPart MultipartFile image  ){
        try {
            SuperAdmin updateSuperAdmin = superAdminService.uploadImage(id, image );
            return new ResponseEntity<>(updateSuperAdmin, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/superadmin/getAll")
    public ResponseEntity<List<SuperAdmin>> getAllSuperAdmin() {
        List<SuperAdmin> superAdmins = superAdminService.getAllSuperAdmin();
        return ResponseEntity.ok(superAdmins);
    }

    @GetMapping("/superadmin/getbyid/{id}")
    public ResponseEntity<SuperAdmin> getSuperAdminById(@PathVariable Long id) {
        Optional<SuperAdmin> superAdmin = superAdminService.getSuperadminbyId(id);
        return superAdmin.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/superadmin/register")
    public ResponseEntity<SuperAdmin> register(@RequestBody SuperAdmin superAdmin){

        return ResponseEntity.ok( superAdminService.RegisterSuperAdmin(superAdmin));
    }


    @PostMapping("/superadmin/tambahdata/{id} ")
    public ResponseEntity<SuperAdmin> tambahSuperAdmin(@PathVariable Long id,
                                                       @RequestBody SuperAdmin superAdmin,
                                                       @RequestPart("image") MultipartFile image) throws IOException {
        SuperAdmin newSuperAdmin = superAdminService.tambahSuperAdmin(id, superAdmin, image);
        return ResponseEntity.ok(newSuperAdmin);
    }

    @PutMapping("/superadmin/edit/{id}")
    public ResponseEntity<SuperAdmin> editSuperAdmin(@PathVariable Long id,
                                                     @RequestPart("image") MultipartFile image,
                                                     @RequestBody SuperAdmin superAdmin) throws IOException {
        SuperAdmin editedSuperAdmin = superAdminService.EditSuperAdmin(id, image, superAdmin);
        return ResponseEntity.ok(editedSuperAdmin);
    }

    @PutMapping(path = "/superadmin/edit-password/{id}")
    public CommonResponse<SuperAdmin> putPassword(@RequestBody PasswordDTO password, @PathVariable Long id ) {
        return ResponseHelper.ok(superAdminService.putPasswordSuperAdmin(password , id));
    }

    @DeleteMapping("/superadmin/delete/{id}")
    public ResponseEntity<Void> deleteSuperAdmin(@PathVariable Long id) throws IOException {
        superAdminService.deleteSuperAdmin(id);
        return ResponseEntity.ok().build();
    }
}
