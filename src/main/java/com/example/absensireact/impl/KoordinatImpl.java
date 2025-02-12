package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.repository.KoordinatRepository;
import com.example.absensireact.repository.OrganisasiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.absensireact.model.Koordinat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KoordinatImpl {

    @Autowired
    private KoordinatRepository koordinatRepository;

    private static final Logger logger = LoggerFactory.getLogger(KoordinatImpl.class);

    @Autowired
    private OrganisasiRepository organisasiRepository;
    public List<Koordinat> getAllKoordinat() {
        try {
            logger.info("Mengambil semua data koordinat");
            return koordinatRepository.findAll();
        } catch (Exception e) {
            logger.error("Gagal mengambil data koordinat: {}", e.getMessage());
            throw new RuntimeException("Terjadi kesalahan saat mengambil data koordinat", e);
        }
    }

    public Optional<Koordinat> getKoordinatById(Long id) {
        try {
            logger.info("Mengambil koordinat dengan ID: {}", id);
            return koordinatRepository.findById(id);
        } catch (Exception e) {
            logger.error("Gagal mengambil koordinat dengan ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Terjadi kesalahan saat mengambil koordinat", e);
        }
    }

    public Koordinat tambahKoordinat(Long idOrganisasi, Koordinat koordinat) {
        try {
            logger.info("Menambahkan koordinat untuk organisasi dengan ID: {}", idOrganisasi);

            Organisasi organisasiValidate = organisasiRepository.findById(idOrganisasi)
                    .orElseThrow(() -> new NotFoundException("Id organisasi tidak ditemukan: " + idOrganisasi));

            koordinat.setOrganisasi(organisasiValidate);

            Koordinat savedKoordinat = koordinatRepository.save(koordinat);
            logger.info("Koordinat berhasil ditambahkan dengan ID: {}", savedKoordinat.getId());

            return savedKoordinat;
        } catch (Exception e) {
            logger.error("Gagal menambahkan koordinat untuk organisasi dengan ID {}: {}", idOrganisasi, e.getMessage());
            throw new RuntimeException("Terjadi kesalahan saat menambahkan koordinat", e);
        }
    }

    public Koordinat updateKoordinat(Long id, Koordinat koordinat2) {
        try {
            logger.info("Memperbarui koordinat dengan ID: {}", id);

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

            Koordinat updatedKoordinat = koordinatRepository.save(koordinat);
            logger.info("Koordinat dengan ID {} berhasil diperbarui", id);

            return updatedKoordinat;
        } catch (Exception e) {
            logger.error("Gagal memperbarui koordinat dengan ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Terjadi kesalahan saat memperbarui koordinat", e);
        }
    }

    public void deleteKoordinat(Long id) {
        try {
            logger.info("Menghapus koordinat dengan ID: {}", id);
            koordinatRepository.deleteById(id);
            logger.info("Koordinat dengan ID {} berhasil dihapus", id);
        } catch (Exception e) {
            logger.error("Gagal menghapus koordinat dengan ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Terjadi kesalahan saat menghapus koordinat", e);
        }
    }
}