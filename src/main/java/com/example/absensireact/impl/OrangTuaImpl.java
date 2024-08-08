package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.SuperAdmin;
import com.example.absensireact.repository.OrangTuaRepository;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.service.OrangTuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrangTuaImpl implements OrangTuaService {

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public List<OrangTua>getAllOrangTua(){
        return orangTuaRepository.findAll();
    }

    @Override
    public Optional<OrangTua> getOrangTuaById(Long id){
        return orangTuaRepository.findById(id);
    }

    @Override
    public List<OrangTua>getAllBySuperAdmin(Long idSuperAdmin){
        return orangTuaRepository.findAllBySuperAdmin(idSuperAdmin);
    }

    @Override
    public OrangTua tambahOrangTua(Long idSuperAdmin, OrangTua orangTua) {
        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findById(idSuperAdmin);
        if (superAdminOptional.isPresent()) {
            SuperAdmin superAdmin = superAdminOptional.get();
            orangTua.setSuperAdmin(superAdmin);
            orangTua.setEmail(orangTua.getEmail());
            orangTua.setNama(orangTua.getNama());
            orangTua.setImageOrtu(orangTua.getImageOrtu());
            orangTua.setRole("Wali Murid");
            orangTua.setPassword(encoder.encode(orangTua.getPassword()));
            return orangTuaRepository.save(orangTua);
        }
        throw new NotFoundException("SuperAdmin tidak ditemukan");
    }

    @Override
    public OrangTua editOrangTuaById(Long id, OrangTua updateOrangTua){
        OrangTua orangTua = orangTuaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id ortu tidak ditemukan : " + id));
        orangTua.setNama(updateOrangTua.getNama());
        orangTua.setEmail(updateOrangTua.getEmail());
        orangTua.setImageOrtu(updateOrangTua.getImageOrtu());
        orangTua.setPassword(encoder.encode(updateOrangTua.getPassword()));
        if (updateOrangTua.getSuperAdmin() != null){
            orangTua.setSuperAdmin(updateOrangTua.getSuperAdmin());
        }
        return orangTuaRepository.save(orangTua);
    }

    @Override
    public void deleteOrangTua(Long id) {
        orangTuaRepository.deleteById(id);
    }


}