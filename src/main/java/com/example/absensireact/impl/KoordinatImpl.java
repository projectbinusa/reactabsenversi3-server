package com.example.absensireact.impl;

import com.example.absensireact.repository.KoordinatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.absensireact.model.Koordinat;

import java.util.List;
import java.util.Optional;


public class KoordinatImpl {

    @Autowired
    private KoordinatRepository koordinatRepository;
    public List<Koordinat> getAllKoordinat() {
        return koordinatRepository.findAll();
    }

    public Optional<Koordinat> getKoordinatById(Long id) {
        return koordinatRepository.findById(id);
    }

    public Koordinat saveKoordinat(Koordinat koordinat) {
        validateKoordinat(koordinat);
        return koordinatRepository.save(koordinat);
    }

    public Koordinat updateKoordinat(Long id, Koordinat updatedKoordinat) {
        return koordinatRepository.findById(id).map(koordinat -> {
            validateKoordinat(updatedKoordinat);
            koordinat.setSouthEastLat(updatedKoordinat.getSouthEastLat());
            koordinat.setSouthEastLng(updatedKoordinat.getSouthEastLng());
            koordinat.setSouthWestLat(updatedKoordinat.getSouthWestLat());
            koordinat.setSouthWestLng(updatedKoordinat.getSouthWestLng());
            koordinat.setNorthEastLat(updatedKoordinat.getNorthEastLat());
            koordinat.setNorthEastLng(updatedKoordinat.getNorthEastLng());
            koordinat.setNorthWestLat(updatedKoordinat.getNorthWestLat());
            koordinat.setNorthWestLng(updatedKoordinat.getNorthWestLng());
            koordinat.setOrganisasi(updatedKoordinat.getOrganisasi());
            return koordinatRepository.save(koordinat);
        }).orElseThrow(() -> new RuntimeException("Koordinat not found with id " + id));
    }

    private void validateKoordinat(Koordinat koordinat) {
        // Validasi bahwa koordinat sesuai dengan aturan Southwest, Northwest, Southeast, Northeast
        if (koordinat.getSouthWestLat() >= koordinat.getNorthWestLat() || koordinat.getSouthWestLng() >= koordinat.getSouthEastLng()) {
            throw new IllegalArgumentException("Koordinat Southwest tidak valid.");
        }

        if (koordinat.getNorthWestLat() <= koordinat.getSouthWestLat() || koordinat.getNorthWestLng() >= koordinat.getNorthEastLng()) {
            throw new IllegalArgumentException("Koordinat Northwest tidak valid.");
        }

        if (koordinat.getSouthEastLat() >= koordinat.getNorthEastLat() || koordinat.getSouthEastLng() <= koordinat.getSouthWestLng()) {
            throw new IllegalArgumentException("Koordinat Southeast tidak valid.");
        }

        if (koordinat.getNorthEastLat() <= koordinat.getSouthEastLat() || koordinat.getNorthEastLng() <= koordinat.getNorthWestLng()) {
            throw new IllegalArgumentException("Koordinat Northeast tidak valid.");
        }
    }

    public void deleteKoordinat(Long id) {
        koordinatRepository.deleteById(id);
    }
}