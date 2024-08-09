package com.example.absensireact.service;

import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.SuperAdmin;
import com.example.absensireact.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface OrangTuaService {

    List<OrangTua> getAllOrangTua();

    List<OrangTua> getAllBySuperAdmin(Long idSuperAdmin);

    Optional<OrangTua> getOrangTuaById(Long id);

    OrangTua editOrangTuaById(Long id,Long idSuperAdmin, OrangTua updateOrangTua);

    OrangTua tambahOrangTua(Long idSuperAdmin, OrangTua orangTua);

    OrangTua putPasswordOrangTua(PasswordDTO passwordDTO, Long id);


    void deleteOrangTua(Long id);

    OrangTua uploadImage(Long id, MultipartFile image) throws IOException;

    OrangTua ubahUsernamedanemail(Long id, OrangTua updateOrangTua);
}