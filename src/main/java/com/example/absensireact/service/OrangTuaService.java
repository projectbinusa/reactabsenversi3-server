package com.example.absensireact.service;

import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.model.OrangTua;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface OrangTuaService {

    List<OrangTua> getAllOrangTua();

    List<OrangTua> getAllByAdmin(Long idAdmin);

    Optional<OrangTua> getOrangTuaById(Long id);

    OrangTua editOrangTuaById(Long id, Long idAdmin, OrangTua updateOrangTua);

    OrangTua tambahOrangTua(Long idAdmin, OrangTua orangTua);

    OrangTua putPasswordOrangTua(PasswordDTO passwordDTO, Long id);


    void deleteOrangTua(Long id);

    OrangTua uploadImage(Long id, MultipartFile image) throws IOException;

    OrangTua ubahUsernamedanemail(Long id, OrangTua updateOrangTua);
}