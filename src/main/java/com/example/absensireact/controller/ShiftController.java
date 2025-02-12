package com.example.absensireact.controller;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Shift;
import com.example.absensireact.service.ShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/shift")
@Controller
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    private static final Logger logger = LoggerFactory.getLogger(ShiftController.class);

    @GetMapping("/getbyadmin")
    public ResponseEntity<List<Shift>> getShiftsByAdmin(@RequestParam("idAdmin") Long idAdmin) {
        try {
            logger.info("Memproses permintaan untuk mendapatkan shift oleh admin dengan id: {}", idAdmin);
            List<Shift> shifts = shiftService.getShiftsByAdmin(idAdmin);
            return ResponseEntity.ok(shifts);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil shift oleh admin dengan id: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Shift>> getAllShifts() {
        try {
            logger.info("Memproses permintaan untuk mendapatkan semua shift.");
            List<Shift> shifts = shiftService.getAllShift();
            return ResponseEntity.ok(shifts);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil semua shift", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getall-byadmin/{idAdmin}")
    public ResponseEntity<List<Shift>> getAllShiftsByAdmin(@PathVariable Long idAdmin) {
        try {
            logger.info("Memproses permintaan untuk mendapatkan semua shift oleh admin dengan id: {}", idAdmin);
            List<Shift> shifts = shiftService.getAllShiftByAdmin(idAdmin);
            return ResponseEntity.ok(shifts);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil semua shift oleh admin dengan id: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getShift-byUserId/{userId}")
    public ResponseEntity<?> getAllShiftsByUserId(@PathVariable Long userId) {
        try {
            logger.info("Memproses permintaan untuk mendapatkan shift berdasarkan userId: {}", userId);
            Optional<Shift> shifts = shiftService.getByUserId(userId);
            return ResponseEntity.ok(shifts);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil shift berdasarkan userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan dalam pengambilan data.");
        }
    }

    @GetMapping("/getbyId/{id}")
    public ResponseEntity<Shift> getShiftById(@PathVariable("id") Long id) {
        try {
            logger.info("Memproses permintaan untuk mendapatkan shift dengan id: {}", id);
            Optional<Shift> shift = shiftService.getshiftById(id);
            return shift.map(value -> ResponseEntity.ok(value))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengambil shift dengan id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/tambahShift/{idAdmin}")
    public ResponseEntity<Shift> postShift(@PathVariable("idAdmin") Long idAdmin, @RequestBody Shift shift) {
        try {
            logger.info("Memproses permintaan untuk menambah shift oleh admin dengan id: {}", idAdmin);
            Shift createdShift = shiftService.PostShift(idAdmin, shift);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdShift);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menambah shift oleh admin dengan id: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/editbyId/{id}")
    public ResponseEntity<Shift> editShiftById(@PathVariable("id") Long id, @RequestBody Shift shift) {
        try {
            logger.info("Memproses permintaan untuk mengedit shift dengan id: {}", id);
            Shift updatedShift = shiftService.editShiftById(id, shift);
            return ResponseEntity.ok(updatedShift);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengedit shift dengan id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteShift(@PathVariable("id") Long id) {
        try {
            logger.info("Memproses permintaan untuk menghapus shift dengan id: {}", id);
            Map<String, Boolean> response = shiftService.delete(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menghapus shift dengan id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete-sementara/{id}")
    public ResponseEntity<String> deleteSemenetara(@PathVariable Long id) {
        try {
            logger.info("Memproses permintaan untuk menghapus sementara shift dengan id: {}", id);
            shiftService.DeleteShiftSementara(id);
            return ResponseEntity.ok("Shift berhasil dipindahkan ke sampah");
        } catch (NotFoundException e) {
            logger.error("Shift tidak ditemukan dengan id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shift tidak ditemukan dengan id: " + id);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menghapus sementara shift dengan id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan dalam penghapusan shift.");
        }
    }

    @PutMapping("/pemulihan-shift/{id}")
    public ResponseEntity<String> PemulihanShift(@PathVariable Long id) {
        try {
            logger.info("Memproses permintaan untuk memulihkan shift dengan id: {}", id);
            shiftService.PemulihanDataShift(id);
            return ResponseEntity.ok("Shift berhasil Dipulihkan");
        } catch (NotFoundException e) {
            logger.error("Shift tidak ditemukan dengan id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shift tidak ditemukan dengan id: " + id);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat memulihkan shift dengan id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan dalam pemulihan shift.");
        }
    }
}