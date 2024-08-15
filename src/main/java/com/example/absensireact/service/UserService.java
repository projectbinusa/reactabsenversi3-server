package com.example.absensireact.service;

import com.example.absensireact.dto.*;
import com.example.absensireact.model.Reset_Password;
import com.example.absensireact.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

//    Map<Object, Object> login(LoginRequest loginRequest, HttpServletRequest request);

//    User Register(User user);


    User ubahPassByForgot(ResetPassDTO updatePass);


    Reset_Password validasiCodeUniqResPass(VerifyCode codeUser);

    ForGotPass sendEmail(ForGotPass forGotPass) throws MessagingException;

    User Register(User user, Long idOrganisasi, Long idShift);

    List<User> getAllByJabatan(Long idJabatan);

    List<User> getAllByAdmin(Long idAdmin);

    List<User> getAllBySuperAdmin(Long idSuperAdmin);

    List<User> getAllByShift(Long idShift);

    User Tambahkaryawan(UserDTO userDTO, Long idAdmin, Long idOrganisasi, Long idShift, Long idOrangTua);


    User editUsernameJabatanShift(Long id, Long idJabatan, Long idShift, Long idOrangTua, Long idKelas, UserDTO updatedUserDTO);

    User putPassword(PasswordDTO passwordDTO, Long id);


    User ubahUsernamedanemail(Long id, User updateUser);

    User EditUserBySuper(Long id, Long idJabatan, Long idShift, User updateUser);

    List<User> GetAllKaryawanByIdAdmin(Long idAdmin);


    User getById(Long id);

    List<User> getAll();


    User fotoUser(Long id, MultipartFile image) throws  IOException;

    User edit(Long id, User user);

    void delete(Long id) throws IOException;

    void deleteUser(Long id);

    List<User> getUsersByIdKelas(Long idKelas);



}
