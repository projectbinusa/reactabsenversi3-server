package com.example.absensireact.service;

import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.OrangTua;

import java.util.List;
import java.util.Optional;

public interface OrangTuaService {

    List<OrangTua> getAllOrangTua();

    Optional<OrangTua> getOrangTuaById(Long id);

    OrangTua tambahOrangTua(OrangTua orangTua);

//    Kelas editOrangTuaById(Long id, OrangTua updateOrangTua);

    void deleteOrangTua(Long id);
}