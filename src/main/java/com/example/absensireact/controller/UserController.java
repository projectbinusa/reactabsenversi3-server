package com.example.absensireact.controller;


import com.example.absensireact.config.AppConfig;
import com.example.absensireact.dto.*;
import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.CommonResponse;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exception.ResponseHelper;
import com.example.absensireact.exel.ExcelDataSiswa;
import com.example.absensireact.exel.ImportSiswa;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.model.User;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.OrganisasiRepository;
import com.example.absensireact.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userImpl;

    @Autowired
    OrganisasiRepository organisasiRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ImportSiswa importSiswa;

    @Autowired
    private ExcelDataSiswa excelDataSiswa;

    @Autowired
    private AppConfig appConfig;


    @PostMapping("/user/{id}/upload-photo")
    public ResponseEntity<User> uploadPhoto(@PathVariable Long id, @RequestPart("image") MultipartFile image) throws IOException {
        User user = userImpl.fotoUser(id, image);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/user/validasi-code")
    public void requestPasswordReset(@RequestBody VerifyCode verifyCode) {
        try {
            userImpl.validasiCodeUniqResPass(verifyCode);
        } catch (NotFoundException e) {
            throw new BadRequestException("Invalid reset code or email.");
        }
    }

    @PutMapping("/user/ubahPassByForgot")
    public void resetPassword(@RequestBody ResetPassDTO resetPassDTO) {
        try {
            userImpl.ubahPassByForgot(resetPassDTO);
        } catch (NotFoundException e) {
            throw new BadRequestException("Email not found.");
        } catch (BadRequestException e) {
            throw new BadRequestException("Password does not match.");
        }
    }

    @PostMapping("/user/forgot_password")
    public CommonResponse<ForGotPass> sendEmail(@RequestBody ForGotPass forGotPass) throws MessagingException {
        return ResponseHelper.ok(userImpl.sendEmail(forGotPass));

    }
    @PostMapping("/user/register")
    public ResponseEntity<User> registerUser(@RequestBody User user, @RequestParam Long idOrganisasi , @RequestParam Long idShift) {
        User newUser = userImpl.Register(user, idOrganisasi , idShift);
        return ResponseEntity.ok(newUser);
    }
    @GetMapping("/user/{idAdmin}/users")
    public List<User> getAllKaryawanByIdAdmin(@PathVariable Long idAdmin) {
        return userImpl.GetAllKaryawanByIdAdmin(idAdmin);
    }

    @PostMapping("/user/tambahkaryawan/{idAdmin}")
    public ResponseEntity<User> tambahKaryawan(
            @RequestBody UserDTO userDTO,
            @PathVariable Long idAdmin,
            @RequestParam Long idOrganisasi,
            @RequestParam Long idOrangTua,
            @RequestParam Long idJabatan,
            @RequestParam Long idShift) {
        try {
            User savedUser = userImpl.Tambahkaryawan(userDTO, idAdmin, idOrganisasi, idJabatan, idShift, idOrangTua);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/user/edit-email-username/{id}")
    public ResponseEntity<User> editemailusername(@PathVariable Long id, @RequestBody User updateUser) {
        User user = userImpl.ubahUsernamedanemail(id , updateUser );
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user/get-allUser")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = userImpl.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/byJabatan/{idJabatan}")
    public ResponseEntity<List<User>> getUsersByJabatan(@PathVariable Long idJabatan) {
        try {
            List<User> users = userImpl.getAllByJabatan(idJabatan);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/byShift/{idShift}")
    public ResponseEntity<List<User>> getUsersByShift(@PathVariable Long idShift) {
        try {
            List<User> users = userImpl.getAllByShift(idShift);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/byAdmin/{idAdmin}")
    public ResponseEntity<List<User>> getUsersByAdmin(@PathVariable Long idAdmin) {
        try {
            List<User> users = userImpl.getAllByAdmin(idAdmin);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/bySuperAdmin/{idSuperAdmin}")
    public ResponseEntity<List<User>> getUsersBySuperAdmin(@PathVariable Long idSuperAdmin) {
        try {
            List<User> users = userImpl.getAllBySuperAdmin(idSuperAdmin);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/user/getUserBy/{id}")
    public ResponseEntity<User> GetUserById (@PathVariable Long id){
        User user = userImpl.getById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping(path = "/user/edit-password/{id}")
    public CommonResponse<User> putPassword( @RequestBody PasswordDTO password,  @PathVariable Long id ) {
        return ResponseHelper.ok(userImpl.putPassword(password , id));
    }
    @PutMapping("/user/editBY/{id}")
    public ResponseEntity<User> editUser(@PathVariable Long id, @RequestBody  User user ) {
        try {
            User updatedUser = userImpl.edit(id, user );
            return ResponseEntity.ok(updatedUser);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/user/editBYSuper/{id}")
    public ResponseEntity<User> editUserBySuper(@PathVariable Long id,@RequestParam Long idJabatan , @RequestParam Long idShift ,@RequestBody  User user ) {
        try {
            User updatedUser = userImpl.EditUserBySuper(id,idJabatan, idShift, user );
            return ResponseEntity.ok(updatedUser);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/user/edit-kar/{id}")
    public ResponseEntity<User> editUser(
            @PathVariable("id") Long id,
            @RequestParam(required = false) Long idJabatan,
            @RequestParam(required = false) Long idShift,
            @RequestParam(required = false) Long idOrangTua,
            @RequestParam(required = false) Long idKelas,
            @RequestBody UserDTO updatedUserDTO) {
        try {
            User editedUser = userImpl.editUsernameJabatanShift(id, idJabatan, idShift, idOrangTua, idKelas, updatedUserDTO);
            return ResponseEntity.ok(editedUser);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PutMapping("/user/editFotoBY/{id}")
    public ResponseEntity<User> editFotoUser(@PathVariable Long id, @RequestPart("image") MultipartFile image) {
        try {
            User updatedUser = userImpl.fotoUser(id,image );
            return ResponseEntity.ok(updatedUser);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/user/delete-foto/{id}")
    public ResponseEntity<String> deleteFoto(@PathVariable Long id) {
        try {
            userImpl.delete(id);
            return ResponseEntity.ok("User berhasil dihapus");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User tidak ditemukan dengan id: " + id);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menghapus user");
        }
    }
    @DeleteMapping("/user/delete-user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userImpl.deleteUser(id);
            return ResponseEntity.ok("User berhasil dihapus");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User tidak ditemukan dengan id: " + id);
        }
    }

    @GetMapping("/user/by-kelas/{idKelas}")
    public ResponseEntity<List<User>> getUserByIdKelas(@PathVariable Long idKelas) {
        List<User> users = userImpl.getUsersByIdKelas(idKelas);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/export-data-siswa/{idAdmin}")
    public ResponseEntity<Void> exportDataSiswa(@PathVariable Long idAdmin, HttpServletResponse response) {
        try {
            excelDataSiswa.exportDataSiswa(idAdmin, response);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/download/template-excel-siswa")
    public ResponseEntity<Void> templateExcelSiswa(HttpServletResponse response) {
        try {
            excelDataSiswa.templateExcelSiswa(response);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

//    @PostMapping("/import/data-siswa/admin/{adminId}/jabatan/{idJabatan}/walimurid/{idOrangTua}/shift/{idShift}/organisasi/{idOrganisasi}")
    @PostMapping("/import/data-siswa/admin/{adminId}")
    public ResponseEntity<String> importUser(@RequestPart("file") MultipartFile file, @PathVariable Long adminId) {
        return adminRepository.findById(adminId).map(admin -> {
        try {
            importSiswa.importUser(file, admin);
            return ResponseEntity.ok("Import berhasil!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan saat mengimpor data: " + e.getMessage());
        }
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("SuperAdmin not found"));
    }


}
