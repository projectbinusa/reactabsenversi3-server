package com.example.absensireact.controller;

import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.impl.KoordinatImpl;
import com.example.absensireact.model.Koordinat;
import com.example.absensireact.securityNew.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/koordinat")
@CrossOrigin(origins = "*")
public class KoordinatController {

    @Autowired
    private KoordinatImpl koordinatService;

    @Autowired
    private JwtTokenUtil jwtTokenUtill;

    @GetMapping("/getAll-Koordinat")
    public List<Koordinat> getAllKoordinat() {
        return koordinatService.getAllKoordinat();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Koordinat> getKoordinatById(@PathVariable Long id) {
        Optional<Koordinat> koordinat = koordinatService.getKoordinatById(id);
        return koordinat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getByIdAdmin")
    public ResponseEntity<List<Koordinat>> getbyAdmin(@RequestParam String token) {
        Long idAdmin = jwtTokenUtill.getIdFromToken(token);
        List<Koordinat> koordinatList = koordinatService.getByadminId(idAdmin);
        if (!koordinatList.isEmpty()) {
            return ResponseEntity.ok(koordinatList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }





    @PostMapping("/tambahKoordinat")
    public ResponseEntity<Koordinat> tambah(@RequestParam String token , @RequestBody Koordinat koordinat) {
        try {
            Long idAdmin = jwtTokenUtill.getIdFromToken(token);
            return ResponseEntity.ok(koordinatService.tambahKoordinat(idAdmin , koordinat));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/ubah-koordinat/{id}")
    public ResponseEntity<Koordinat> updateKoordinat(@PathVariable Long id, @RequestBody Koordinat updatedKoordinat) {
        try {
            return ResponseEntity.ok(koordinatService.updateKoordinat(id, updatedKoordinat));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete-koordinat/{id}")
    public ResponseEntity<Void> deleteKoordinat(@PathVariable Long id) {
        koordinatService.deleteKoordinat(id);
        return ResponseEntity.noContent().build();
    }
}