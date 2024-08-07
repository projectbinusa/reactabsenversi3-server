package com.example.absensireact.service;

import com.example.absensireact.model.OrangTua;

import java.util.List;
import java.util.Optional;

public interface OrangTuaService {

    List<OrangTua> getAllOrangTua();

    Optional<OrangTua> getOrangTuaById(Long id);

    OrangTua editOrangTuaById(Long id, OrangTua updateOrangTua);

    OrangTua tambahOrangTua(OrangTua orangTua);

    void deleteOrangTua(Long id);
}
