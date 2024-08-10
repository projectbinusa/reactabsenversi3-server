package com.example.absensireact.controller;

import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.exception.CommonResponse;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exception.ResponseHelper;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.SuperAdmin;
import com.example.absensireact.model.User;
import com.example.absensireact.service.OrangTuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orang-tua")
@CrossOrigin(origins = "*")
public class OrangTuaController {

    @Autowired
    OrangTuaService orangTuaImpl;

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


    @PutMapping(path = "/edit-password/{id}")
    public CommonResponse<OrangTua> putPassword(@RequestBody PasswordDTO password, @PathVariable Long id ) {
        return ResponseHelper.ok(orangTuaService.putPasswordOrangTua(password , id));
    }

    @DeleteMapping("/deleteOrangTua/{id}")
    public ResponseEntity<Void> deleteOrangTua(@PathVariable Long id) throws IOException {
        orangTuaService.deleteOrangTua(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/orang-tua/ubah-foto/{id}")
    public ResponseEntity<?>EditFotoOrangTua(@PathVariable Long id , @RequestPart MultipartFile image  ){
        try {
            OrangTua updateOrangTua = orangTuaService.uploadImage(id, image );
            return new ResponseEntity<>(updateOrangTua, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/orang-tua/edit-email-username/{id}")
    public ResponseEntity<OrangTua> editemailusername(@PathVariable Long id, @RequestBody OrangTua updateOrangTua) {
        OrangTua orangTua = orangTuaImpl.ubahUsernamedanemail(id , updateOrangTua );
        return new ResponseEntity<>(orangTua, HttpStatus.OK);
    }
}