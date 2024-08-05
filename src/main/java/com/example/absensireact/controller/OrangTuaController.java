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

    @GetMapping("/orang-tua/all")
    public ResponseEntity<List<OrangTua>> getAllOrangTua(){
        return ResponseEntity.ok(orangTuaService.getAllOrangTua());
    }

    @GetMapping("/orang-tua/getbyid/{id}")
    public ResponseEntity<OrangTua> getOrangTuaById(@PathVariable Long id) {
        Optional<OrangTua> orangTua = orangTuaService.getOrangTuaById(id);
        return orangTua.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/orang-tua/tambah")
    public ResponseEntity<OrangTua> tambahOrangTua(@RequestBody OrangTua orangTua) {
        OrangTua orangTuaBaru = orangTuaService.tambahOrangTua(orangTua);
        return new ResponseEntity<>(orangTuaBaru, HttpStatus.CREATED);
    }

//    @PutMapping("/editOrtuById/{id}")
//    public ResponseEntity<OrangTua> editOrangTuaById(@PathVariable("id") Long id, @RequestBody OrangTua orangTua) {
//        OrangTua updatedOrangTua= orangTuaService.editOrangTuaById(id, orangTua);
//        return ResponseEntity.ok(updatedOrangTua);
//    }

    @DeleteMapping("/deleteOrangTua/{id}")
    public ResponseEntity<Void> deleteOrangTua(@PathVariable Long id) throws IOException {
        orangTuaService.deleteOrangTua(id);
        return ResponseEntity.noContent().build();
    }
}
