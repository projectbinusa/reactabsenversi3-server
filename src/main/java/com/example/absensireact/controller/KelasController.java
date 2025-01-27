package com.example.absensireact.controller;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Jabatan;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.model.Shift;
import com.example.absensireact.service.KelasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/kelas")
@CrossOrigin(origins = "*")
@Controller
public class KelasController {

    @Autowired
    private KelasService kelasService;

    @GetMapping("/kelas/all")
    public ResponseEntity<List<Kelas>> getAllKelas() {
        return ResponseEntity.ok(kelasService.getAllKelas());
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<Kelas> getKelasById(@PathVariable Long id) {
        Optional<Kelas> kelas = kelasService.getKelasById(id);
        return kelas.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getALlByOrganisasi/{idOrganisasi}")
    public ResponseEntity<List<Kelas>> getALlByOrganisasi(@PathVariable Long idOrganisasi) {
        List<Kelas> kelasList =  kelasService.getALlByOrganisasi(idOrganisasi);
        return ResponseEntity.ok(kelasList);
    }

    @GetMapping("/getAllByAdmin/{idAdmin}")
    public ResponseEntity<List<Kelas>> getALlByAdmin(@PathVariable Long idAdmin) {
        List<Kelas> kelasList =  kelasService.getAllByIdAdmin(idAdmin);
        return ResponseEntity.ok(kelasList);
    }

    @PutMapping("/editKelasById/{id}")
    public ResponseEntity<Kelas> editJabatanById(@PathVariable("id") Long id, @RequestBody Kelas kelas) {
        Kelas updatedKelas = kelasService.editKelasById(id, kelas);
        return ResponseEntity.ok(updatedKelas);
    }

    @PostMapping("/tambah")
    public ResponseEntity<Kelas> tambahKelas(@RequestBody Kelas kelas , @RequestParam Long idOrganisasi , @RequestParam Long idAdmin) {
        Kelas kelasBaru = kelasService.tambahKelas(kelas , idOrganisasi , idAdmin);
        return new ResponseEntity<>(kelasBaru, HttpStatus.CREATED);
    }

    @GetMapping("/hasRelations/{id}")
    public ResponseEntity<Boolean> hasRelations(@PathVariable Long id) {
        boolean hasRelations = kelasService.checkIfHasRelations(id);
        return ResponseEntity.ok(hasRelations);
    }


    @DeleteMapping("/deleteKelas/{id}")
    public ResponseEntity<Void> deleteKelas(@PathVariable Long id) throws IOException {
        kelasService.deleteKelas(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-sementara/{id}")
    public ResponseEntity<String> deleteSemenetara(@PathVariable Long id) {
        try {
            kelasService.DeleteKelasSementara(id);
            return ResponseEntity.ok("Kelas berhasil dipindahkan ke sampah");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kelas tidak ditemukan dengan id: " + id);
        }
    }
    @PutMapping("/pemulihan-kelas/{id}")
    public ResponseEntity<String> PemulihanKelas(@PathVariable Long id) {
        try {
            kelasService.PemulihanDataKelas(id);
            return ResponseEntity.ok("Kelas berhasil Dipulihkan");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kelas tidak ditemukan dengan id: " + id);
        }
    }
}
