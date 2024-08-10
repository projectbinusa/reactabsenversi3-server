package com.example.absensireact.controller;

import com.example.absensireact.dto.ForGotPass;
import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.dto.ResetPassDTO;
import com.example.absensireact.dto.VerifyCode;
import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.CommonResponse;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exception.ResponseHelper;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.User;
import com.example.absensireact.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @PostMapping("/admin/validasi-code")
    public void requestPasswordReset(@RequestBody VerifyCode verifyCode) {
        try {
            adminService.validasiCodeUniqResPass(verifyCode);
        } catch (NotFoundException e) {
            throw new BadRequestException("Invalid reset code or email.");
        }
    }

    @PutMapping("/admin/ubahPassByForgot")
    public void resetPassword(@RequestBody ResetPassDTO resetPassDTO) {
        try {
            adminService.ubahPassByForgot(resetPassDTO);
        } catch (NotFoundException e) {
            throw new BadRequestException("Email not found.");
        } catch (BadRequestException e) {
            throw new BadRequestException("Password does not match.");
        }
    }

    @PostMapping("/admin/forgot_password")
    public CommonResponse<ForGotPass> sendEmail(@RequestBody ForGotPass forGotPass) throws MessagingException {
        return ResponseHelper.ok(adminService.sendEmail(forGotPass));

    }
    @PostMapping("/admin/register")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        Admin registeredAdmin = adminService.RegisterAdmin(admin);
        return new ResponseEntity<>(registeredAdmin, HttpStatus.CREATED);
    }
    @PostMapping("/admin/register-by-superadmin/{idSuperAdmin}")
    public ResponseEntity<Admin> registerbySuperAdmin(@PathVariable Long idSuperAdmin ,@RequestBody Admin admin) {
        Admin registeredAdmin = adminService.RegisterBySuperAdmin(idSuperAdmin,admin);
        return new ResponseEntity<>(registeredAdmin, HttpStatus.CREATED);
    }

    @GetMapping("/admin/getById/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAll();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @PutMapping("/admin/ubah-foto/{id}")
    public ResponseEntity<?>EditFotoAdmin(@PathVariable Long id , @RequestPart MultipartFile image  ){
        try {
            Admin updateAdmin = adminService.uploadImage(id, image );
            return new ResponseEntity<>(updateAdmin, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/admin/get-all-by-super/{idSuperAdmin}")
    public ResponseEntity<List<Admin>> getAllAdminsbysuperadmin(@PathVariable Long idSuperAdmin) {
        List<Admin> admins = adminService.getAllBySuperAdmin(idSuperAdmin);
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }


    @PutMapping(path = "/admin/edit-password/{id}")
    public CommonResponse<Admin> putPassword(@RequestBody PasswordDTO password, @PathVariable Long id ) {
        return ResponseHelper.ok(adminService.putPasswordAdmin(password , id));
    }
    @PutMapping("/admin/edit/{id}/{idSuperAdmin}")
    public ResponseEntity<Admin> editAdmin(@PathVariable("id") Long id,
                                           @PathVariable("idSuperAdmin") Long idSuperAdmin,
                                           @RequestBody Admin existingUser) {
        Admin admin = adminService.edit(id, idSuperAdmin, existingUser);
        return ResponseEntity.ok(admin);
    }


    @PutMapping("/admin/edit-email-username/{id}")
    public ResponseEntity<Admin> editemailusername(@PathVariable Long id, @RequestBody Admin updateAdmin) {
        Admin updatedAdmin = adminService.ubahUsernamedanemail(id , updateAdmin);
        return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
    }
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteAdmin(@PathVariable Long id) {
        Map<String, Boolean> response = adminService.delete(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
