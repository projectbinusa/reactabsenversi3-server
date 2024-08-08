package com.example.absensireact.service;

import com.example.absensireact.model.OrangTua;

import java.util.List;
import java.util.Optional;

public interface OrangTuaService {

    List<OrangTua> getAllOrangTua();

    List<OrangTua> getAllBySuperAdmin(Long idSuperAdmin);

    Optional<OrangTua> getOrangTuaById(Long id);

    OrangTua editOrangTuaById(Long id,Long idSuperAdmin, OrangTua updateOrangTua);

    OrangTua tambahOrangTua(Long idSuperAdmin, OrangTua orangTua);

    void deleteOrangTua(Long id);
}