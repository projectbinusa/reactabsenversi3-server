package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.repository.OrangTuaRepository;
import com.example.absensireact.service.OrangTuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrangTuaImpl implements OrangTuaService {

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    @Override
    public List<OrangTua> getAllOrangTua(){
        return orangTuaRepository.findAll();
    }

    @Override
    public Optional<OrangTua> getOrangTuaById(Long id){
        return orangTuaRepository.findById(id);
    }

    @Override
    public OrangTua tambahOrangTua(OrangTua orangTua) {
        orangTua.setEmail(orangTua.getEmail());
        orangTua.setNama(orangTua.getNama());
        orangTua.setImageOrtu(orangTua.getImageOrtu());
        orangTua.setRole("Wali Murid");
        orangTua.setPassword(orangTua.getPassword());
        orangTua.setUser(orangTua.getUser());

        return orangTuaRepository.save(orangTua);
    }

//    @Override
//    public OrangTua editOrangTuaById(Long id, OrangTua updateOrangTua){
//        OrangTua orangTua = orangTuaRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("id ortu tidak ditemukan : " + id));
//        orangTua.setNama(updateOrangTua.getNama());
//        orangTua.setEmail(updateOrangTua.getEmail());
//        orangTua.setImageOrtu(updateOrangTua.getImageOrtu());
//        orangTua.setPassword(updateOrangTua.getPassword());
//        if (updateOrangTua.getUser() != null){
//            orangTua.setUser(updateOrangTua.getUser());
//        }
//        return orangTuaRepository.save(orangTua);
//    }

    @Override
    public void deleteOrangTua(Long id) {
        orangTuaRepository.deleteById(id);
    }



}
