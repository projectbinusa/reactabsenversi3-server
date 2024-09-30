package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Admin;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.KoordinatRepository;
import com.example.absensireact.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.absensireact.model.Koordinat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KoordinatImpl {

    @Autowired
    private KoordinatRepository koordinatRepository;

    @Autowired
    private AdminRepository adminRepository;
    public List<Koordinat> getAllKoordinat() {
        return koordinatRepository.findAll();
    }

    public Optional<Koordinat> getKoordinatById(Long id) {
        return koordinatRepository.findById(id);
    }

    public List<Koordinat> getKoordinatByIdAdmin(Long idAdmin) {
        return koordinatRepository.findByIdAdmin(idAdmin);
    }

//    public Koordinat saveKoordinat(Long idAdmin , Koordinat koordinat) {
//        Admin organisasiValidate = organisasiRepository.findById(idAdmin)
//                        .orElseThrow(() -> new NotFoundException("Id organisasi tidak ditemukan : " + idAdmin));
//        validateKoordinat(koordinat);
//        koordinat.setAdmin(organisasiValidate);
//        return koordinatRepository.save(koordinat);
//    }

    public Koordinat tambahKoordinat(Long idAdmin, Koordinat koordinat) {
        // Validasi jika organisasi dengan id yang diberikan tidak ditemukan
        Admin adminValidate = adminRepository.findById(idAdmin)
                .orElseThrow(() -> new NotFoundException("Id admin tidak ditemukan: " + idAdmin));

        koordinat.setSouthEastLat(koordinat.getSouthEastLat());
        koordinat.setSouthEastLng(koordinat.getSouthEastLng());
        koordinat.setSouthWestLat(koordinat.getSouthWestLat());
        koordinat.setSouthWestLng(koordinat.getSouthWestLng());
        koordinat.setNorthEastLat(koordinat.getNorthEastLat());
        koordinat.setNorthEastLng(koordinat.getNorthEastLng());
        koordinat.setNorthWestLat(koordinat.getNorthWestLat());
        koordinat.setNorthWestLng(koordinat.getNorthWestLng());

        koordinat.setAdmin(adminValidate);

        return koordinatRepository.save(koordinat);
    }

    public Koordinat updateKoordinat(Long id , Koordinat koordinat2) {
        Koordinat koordinat = koordinatRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id koordinat tidak ditemukan: " + id));

        koordinat.setSouthEastLat(koordinat2.getSouthEastLat());
        koordinat.setSouthEastLng(koordinat2.getSouthEastLng());
        koordinat.setSouthWestLat(koordinat2.getSouthWestLat());
        koordinat.setSouthWestLng(koordinat2.getSouthWestLng());
        koordinat.setNorthEastLat(koordinat2.getNorthEastLat());
        koordinat.setNorthEastLng(koordinat2.getNorthEastLng());
        koordinat.setNorthWestLat(koordinat2.getNorthWestLat());
        koordinat.setNorthWestLng(koordinat2.getNorthWestLng());


        return koordinatRepository.save(koordinat);
    }

//    public Koordinat updateKoordinat(Long id, Koordinat updatedKoordinat) {
//        return koordinatRepository.findById(id).map(koordinat -> {
//            validateKoordinat(updatedKoordinat);
//            koordinat.setSouthEastLat(updatedKoordinat.getSouthEastLat());
//            koordinat.setSouthEastLng(updatedKoordinat.getSouthEastLng());
//            koordinat.setSouthWestLat(updatedKoordinat.getSouthWestLat());
//            koordinat.setSouthWestLng(updatedKoordinat.getSouthWestLng());
//            koordinat.setNorthEastLat(updatedKoordinat.getNorthEastLat());
//            koordinat.setNorthEastLng(updatedKoordinat.getNorthEastLng());
//            koordinat.setNorthWestLat(updatedKoordinat.getNorthWestLat());
//            koordinat.setNorthWestLng(updatedKoordinat.getNorthWestLng());
//            koordinat.setAdmin(updatedKoordinat.getAdmin());
//            return koordinatRepository.save(koordinat);
//        }).orElseThrow(() -> new RuntimeException("Koordinat not found with id " + id));
//    }

//    private void validateKoordinat(Koordinat koordinat) {
//        try {
//            double southWestLat = Double.parseDouble(koordinat.getSouthWestLat());
//            double southWestLng = Double.parseDouble(koordinat.getSouthWestLng());
//            double northWestLat = Double.parseDouble(koordinat.getNorthWestLat());
//            double northWestLng = Double.parseDouble(koordinat.getNorthWestLng());
//            double southEastLat = Double.parseDouble(koordinat.getSouthEastLat());
//            double southEastLng = Double.parseDouble(koordinat.getSouthEastLng());
//            double northEastLat = Double.parseDouble(koordinat.getNorthEastLat());
//            double northEastLng = Double.parseDouble(koordinat.getNorthEastLng());
//
//            if (southWestLat >= northWestLat || southWestLng >= southEastLng) {
//                throw new IllegalArgumentException("Koordinat Southwest tidak valid.");
//            }
//
//            if (northWestLat <= southWestLat || northWestLng >= northEastLng) {
//                throw new IllegalArgumentException("Koordinat Northwest tidak valid.");
//            }
//
//            if (southEastLat >= northEastLat || southEastLng <= southWestLng) {
//                throw new IllegalArgumentException("Koordinat Southeast tidak valid.");
//            }
//
//            if (northEastLat <= southEastLat || northEastLng <= northWestLng) {
//                throw new IllegalArgumentException("Koordinat Northeast tidak valid.");
//            }
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("Nilai koordinat harus berupa angka yang valid.", e);
//        }
//    }

    public void deleteKoordinat(Long id) {
        koordinatRepository.deleteById(id);
    }
}