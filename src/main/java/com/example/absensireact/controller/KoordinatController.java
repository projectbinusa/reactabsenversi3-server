package com.example.absensireact.controller;

import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.impl.KoordinatImpl;
import com.example.absensireact.model.Koordinat;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/getAll-Koordinat")
    public List<Koordinat> getAllKoordinat() {
        return koordinatService.getAllKoordinat();
    }

     @GetMapping("/getById/{id}")
    public ResponseEntity<Koordinat> getKoordinatById(@PathVariable Long id) {
        Optional<Koordinat> koordinat = koordinatService.getKoordinatById(id);
        return koordinat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }



    @PostMapping("/tambahKoordinat/{idOrganisasi}")
    public ResponseEntity<Koordinat> tambah(@PathVariable Long idOrganisasi , @RequestBody Koordinat koordinat) {
        try {
            return ResponseEntity.ok(koordinatService.tambahKoordinat(idOrganisasi , koordinat));
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
