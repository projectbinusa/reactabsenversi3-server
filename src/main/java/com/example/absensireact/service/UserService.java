package com.example.absensireact.service;

import com.example.absensireact.dto.*;
import com.example.absensireact.model.Reset_Password;
import com.example.absensireact.model.UserModel;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

//    Map<Object, Object> login(LoginRequest loginRequest, HttpServletRequest request);

//    UserModel Register(UserModel user);


    UserModel ubahPassByForgot(ResetPassDTO updatePass);


    Reset_Password validasiCodeUniqResPass(VerifyCode codeUser);

    ForGotPass sendEmail(ForGotPass forGotPass) throws MessagingException;

    UserModel Register(UserModel user, Long idOrganisasi, Long idShift);

    List<UserModel> getAllByJabatan(Long idJabatan);

    List<UserModel> getAllByAdmin(Long idAdmin);

    List<UserModel> getAllByAdminandKelas(Long idAdmin, Long KlasId);

    List<UserModel> getAllBySuperAdmin(Long idSuperAdmin);

    List<UserModel> getAllByShift(Long idShift);

    UserModel EditUserBySuper(Long id, Long idJabatan, Long idShift, UserModel updateUser);

    UserModel Tambahkaryawan(UserDTO userDTO, Long idAdmin, Long idOrganisasi, Long idShift, Long idOrangTua);

    UserModel TambahUserKelas(UserDTO userDTO, Long idAdmin, Long idOrganisasi, Long idShift, Long idOrangTua, Long idKelas);


    UserModel editUsernameJabatanShift(Long id, Long idJabatan, Long idShift, Long idOrangTua, Long idKelas, UserDTO updatedUserDTO);

    List<UserModel>getAllByOrangTua(Long idOrangTua);

    UserModel putPassword(PasswordDTO passwordDTO, Long id);


    UserModel ubahUsernamedanemail(Long id, UserModel updateUser);

    UserModel EditUserBySuper(Long id, Long idShift, Long idOrangTua, Long idKelas, UserModel updateUser);

    List<UserModel> GetAllKaryawanByIdAdmin(Long idAdmin);


    UserModel getById(Long id);

    List<UserModel> getAll();


    UserModel fotoUser(Long id, MultipartFile image) throws  IOException;

    UserModel edit(Long id, UserModel user);

    void delete(Long id) throws IOException;

    void deleteUser(Long id);

    List<UserModel> getUsersByIdKelas(Long idKelas);



}
