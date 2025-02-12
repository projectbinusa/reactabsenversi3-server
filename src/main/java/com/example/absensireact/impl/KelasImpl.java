package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.KelasRepository;
import com.example.absensireact.repository.OrganisasiRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.KelasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KelasImpl implements KelasService {

    private static final Logger logger = LoggerFactory.getLogger(KelasImpl.class);

    @Autowired
    private KelasRepository kelasRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganisasiRepository organisasiRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Kelas> getAllKelas() {
        logger.info("Mengambil semua data kelas");
        return kelasRepository.findAll();
    }

    @Override
    public Optional<Kelas> getKelasById(Long id) {
        logger.info("Mengambil data kelas dengan ID: {}", id);
        return kelasRepository.findById(id);
    }

    @Override
    public List<Kelas> getALlByOrganisasi(Long idOrganisasi) {
        logger.info("Mengambil semua kelas berdasarkan organisasi dengan ID: {}", idOrganisasi);
        return kelasRepository.findAllByOrganisasi(idOrganisasi);
    }

    @Override
    public List<Kelas> getAllByIdAdmin(Long idAdmin) {
        logger.info("Mengambil semua kelas berdasarkan admin dengan ID: {}", idAdmin);
        return kelasRepository.findByIdAdmin(idAdmin);
    }

    @Override
    public Kelas editKelasById(Long id, Kelas updateKelas) {
        try {
            logger.info("Mengedit kelas dengan ID: {}", id);

            Kelas kelas = kelasRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("ID kelas tidak ditemukan: " + id));

            Optional<Kelas> existingKelas = kelasRepository.findByNamaKelasAndAdmin(updateKelas.getNamaKelas(), kelas.getAdmin());

            if (existingKelas.isPresent() && !existingKelas.get().getId().equals(id)) {
                throw new NotFoundException("Kelas dengan nama yang sama sudah ada di bawah admin ini");
            }

            kelas.setNamaKelas(updateKelas.getNamaKelas());

            if (updateKelas.getOrganisasi() != null) {
                kelas.setOrganisasi(updateKelas.getOrganisasi());
            }

            Kelas updatedKelas = kelasRepository.save(kelas);
            logger.info("Berhasil mengedit kelas dengan ID: {}", id);
            return updatedKelas;
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengedit kelas dengan ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Kelas tambahKelas(Kelas kelas, Long idOrganisasi, Long idAdmin) {
        try {
            logger.info("Menambahkan kelas baru dengan nama: {}", kelas.getNamaKelas());

            Organisasi organisasi = organisasiRepository.findById(idOrganisasi)
                    .orElseThrow(() -> new NotFoundException("Organisasi tidak ditemukan"));
            Admin admin = adminRepository.findById(idAdmin)
                    .orElseThrow(() -> new NotFoundException("ID admin tidak ditemukan"));

            Optional<Kelas> existingKelas = kelasRepository.findByNamaKelasAndAdmin(kelas.getNamaKelas(), admin);
            if (existingKelas.isPresent()) {
                throw new NotFoundException("Kelas dengan nama yang sama sudah ada di bawah admin ini");
            }

            kelas.setOrganisasi(organisasi);
            kelas.setAdmin(admin);
            kelas.setDeleted(0);
            Kelas newKelas = kelasRepository.save(kelas);
            logger.info("Berhasil menambahkan kelas dengan ID: {}", newKelas.getId());
            return newKelas;
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menambahkan kelas dengan nama: {}", kelas.getNamaKelas(), e);
            throw e;
        }
    }

    @Override
    public Map<String, Boolean> deleteKelas(Long id) {
        Map<String, Boolean> res = new HashMap<>();
        try {
            logger.info("Menghapus kelas dengan ID: {}", id);

            List<UserModel> users = userRepository.findByKelasId(id);
            for (UserModel user : users) {
                user.setKelas(null);
                userRepository.save(user);
            }

            kelasRepository.deleteById(id);
            res.put("Deleted", Boolean.TRUE);
            logger.info("Berhasil menghapus kelas dengan ID: {}", id);
        } catch (Exception e) {
            logger.error("Gagal menghapus kelas dengan ID: {}", id, e);
            res.put("Deleted", Boolean.FALSE);
        }
        return res;
    }

    @Override
    public void DeleteKelasSementara(Long id) {
        try {
            logger.info("Menghapus sementara kelas dengan ID: {}", id);
            Optional<Kelas> kelasOptional = kelasRepository.findById(id);
            if (kelasOptional.isPresent()) {
                Kelas kelas = kelasOptional.get();
                kelas.setDeleted(1);
                kelasRepository.save(kelas);
                logger.info("Berhasil menghapus sementara kelas dengan ID: {}", id);
            }
        } catch (Exception e) {
            logger.error("Gagal menghapus sementara kelas dengan ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public void PemulihanDataKelas(Long id) {
        try {
            logger.info("Memulihkan kelas dengan ID: {}", id);
            Optional<Kelas> kelasOptional = kelasRepository.findById(id);
            if (kelasOptional.isPresent()) {
                Kelas kelas = kelasOptional.get();
                kelas.setDeleted(0);
                kelasRepository.save(kelas);
                logger.info("Berhasil memulihkan kelas dengan ID: {}", id);
            }
        } catch (Exception e) {
            logger.error("Gagal memulihkan kelas dengan ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public boolean checkIfHasRelations(Long kelasId) {
        try {
            logger.info("Memeriksa apakah kelas dengan ID: {} memiliki relasi dengan pengguna", kelasId);
            List<UserModel> users = userRepository.findUsersByKelas(kelasId);
            boolean hasRelations = !users.isEmpty();
            logger.info("Kelas dengan ID: {} memiliki relasi? {}", kelasId, hasRelations);
            return hasRelations;
        } catch (Exception e) {
            logger.error("Gagal memeriksa relasi kelas dengan ID: {}", kelasId, e);
            throw e;
        }
    }
}
