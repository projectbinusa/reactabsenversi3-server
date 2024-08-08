package com.example.absensireact.controller;

import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.service.OrangTuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orang-tua")
public class OrangTuaController {

    @Autowired
    private OrangTuaService orangTuaService;

    @GetMapping("/all")
    public ResponseEntity<List<OrangTua>> getAllOrangTua(){
        return ResponseEntity.ok(orangTuaService.getAllOrangTua());
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<OrangTua> getOrangTuaById(@PathVariable Long id) {
        Optional<OrangTua> orangTua = orangTuaService.getOrangTuaById(id);
        return orangTua.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getALlBySuperAdmin/{idSuperAdmin}")
    public ResponseEntity<List<OrangTua>> getALlBySuperAdmin(@PathVariable Long idSuperAdmin) {
        List<OrangTua> orangtuaList =  orangTuaService.getAllBySuperAdmin(idSuperAdmin);
        return ResponseEntity.ok(orangtuaList);
    }

    @PostMapping("/tambah/{idSuperAdmin}")
    public ResponseEntity<OrangTua> tambahOrangtua(@PathVariable Long idSuperAdmin, @RequestBody OrangTua orangTua) {
        orangTua.setId(null);
        OrangTua orangTuaBaru = orangTuaService.tambahOrangTua(idSuperAdmin, orangTua);
        return new ResponseEntity<>(orangTuaBaru, HttpStatus.CREATED);
    }

    @PutMapping("/editOrtuById/{id}/{idSuperAdmin}")
    public ResponseEntity<OrangTua> editOrangTua(@PathVariable("id") Long id,
                                                 @PathVariable("idSuperAdmin") Long idSuperAdmin,
                                                 @RequestBody OrangTua orangTua) {
        OrangTua updateOrangtua = orangTuaService.editOrangTuaById(id, idSuperAdmin, orangTua);
        return ResponseEntity.ok(updateOrangtua);
    }


    @DeleteMapping("/deleteOrangTua/{id}")
    public ResponseEntity<Void> deleteOrangTua(@PathVariable Long id) throws IOException {
        orangTuaService.deleteOrangTua(id);
        return ResponseEntity.noContent().build();
    }
}