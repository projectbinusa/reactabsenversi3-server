package com.example.absensireact.service;

import com.example.absensireact.dto.ForGotPass;
import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.dto.ResetPassDTO;
import com.example.absensireact.dto.VerifyCode;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.LoginRequest;
import com.example.absensireact.model.Reset_Password;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AdminService {

    Admin ubahPassByForgot (ResetPassDTO updatePass);

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUsername(String username);


    Reset_Password validasiCodeUniqResPass(VerifyCode codeUser);

    Admin RegisterBySuperAdmin(Long idSuperAdmin, Admin admin);

    Admin RegisterAdmin(Admin admin);

    Admin getById(Long id);

    List<Admin>getAllBySuperAdmin(Long idSuperAdmin);

    List<Admin> getAll();

    Admin edit(Long id, Long idSuperAdmin, Admin admin);

    Admin uploadImage (Long id, MultipartFile image ) throws IOException;

    Admin ubahUsernamedanemail(Long id, Admin updateadmin);

    Admin putPasswordAdmin(PasswordDTO passwordDTO, Long id);

    Map<String, Boolean> delete(Long id);

    void DeleteAdminSementara(Long id);

    void PemulihanDataAdmin(Long id);

    ForGotPass sendEmail(ForGotPass forGotPass) throws MessagingException;
}
