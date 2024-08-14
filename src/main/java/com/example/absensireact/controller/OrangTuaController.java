package com.example.absensireact.controller;

import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.exception.CommonResponse;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exception.ResponseHelper;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.*;
import com.example.absensireact.service.OrangTuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
    com.example.absensireact.exel.ExcelOrtu excelOrtu;

    @Autowired
    private com.example.absensireact.exel.ImportOrtu importOrtu;

    @Autowired
    private OrangTuaService orangTuaService;

    @GetMapping("/{id}/admin")
    public Admin getAdminByOrangTuaId(@PathVariable Long id) {
        return orangTuaService.getAdminByOrangTuaId(id);
    }
    @GetMapping("/all")
    public ResponseEntity<List<OrangTua>> getAllOrangTua(){
        return ResponseEntity.ok(orangTuaService.getAllOrangTua());
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<OrangTua> getOrangTuaById(@PathVariable Long id) {
        Optional<OrangTua> orangTua = orangTuaService.getOrangTuaById(id);
        return orangTua.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getALlBySuperAdmin/{idAdmin}")
    public ResponseEntity<List<OrangTua>> getALlBySuperAdmin(@PathVariable Long idAdmin) {
        List<OrangTua> orangtuaList =  orangTuaService.getAllByAdmin(idAdmin);
        return ResponseEntity.ok(orangtuaList);
    }

    @PostMapping("/tambah/{idAdmin}")
    public ResponseEntity<OrangTua> tambahOrangtua(@PathVariable Long idAdmin, @RequestBody OrangTua orangTua) {
        orangTua.setId(null);
        OrangTua orangTuaBaru = orangTuaService.tambahOrangTua(idAdmin, orangTua);
        return new ResponseEntity<>(orangTuaBaru, HttpStatus.CREATED);
    }

    @PutMapping("/editOrtuById/{id}/{idAdmin}")
    public ResponseEntity<OrangTua> editOrangTua(@PathVariable("id") Long id,
                                                 @PathVariable("idAdmin") Long idAdmin,
                                                 @RequestBody OrangTua orangTua) {
        OrangTua updateOrangtua = orangTuaService.editOrangTuaById(id, idAdmin, orangTua);
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


    @GetMapping("/export/data-orang-tua/{idAdmin}")
    public void exportOrangTua(@PathVariable Long idAdmin,HttpServletResponse response) throws IOException {
        excelOrtu.excelOrangTua(idAdmin, response);
    }

    @GetMapping("/download/template-orang-tua")
    public void templateExcelWaliMurid(HttpServletResponse response) throws IOException {
        excelOrtu.templateExcelWaliMurid(response);
    }

    @PostMapping("/import/data-orang-tua/{adminId}")
    public ResponseEntity<String> importOrangTua(@PathVariable Long adminId, @RequestPart("file") MultipartFile file) {
        try {
            importOrtu.importOrangTua(adminId, file);
            return ResponseEntity.ok("Import berhasil!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan saat mengimpor data: " + e.getMessage());
        }
    }



}