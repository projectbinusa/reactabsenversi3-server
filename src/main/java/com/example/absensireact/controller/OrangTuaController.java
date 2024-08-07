package com.example.absensireact.controller;

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

    @PostMapping("/tambah")
    public ResponseEntity<OrangTua> tambahOrangtua(@RequestBody OrangTua orangTua) {
        OrangTua OrangtuaBaru = orangTuaService.tambahOrangTua(orangTua);
        return new ResponseEntity<>(OrangtuaBaru, HttpStatus.CREATED);
    }

    @PutMapping("/editOrtuById/{id}")
    public ResponseEntity<OrangTua> editOrangTua(@PathVariable("id") Long id, @RequestBody OrangTua orangTua) {
        OrangTua updateOrangtua = orangTuaService.editOrangTuaById(id, orangTua);
        return ResponseEntity.ok(updateOrangtua);
    }

    @DeleteMapping("/deleteOrangTua/{id}")
    public ResponseEntity<Void> deleteOrangTua(@PathVariable Long id) throws IOException {
        orangTuaService.deleteOrangTua(id);
        return ResponseEntity.noContent().build();
    }
}
