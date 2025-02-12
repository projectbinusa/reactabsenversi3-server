package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Lembur;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.LemburRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.LemburService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LemburImpl implements LemburService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private LemburRepository lemburRepository;

    private static final Logger logger = LoggerFactory.getLogger(LemburImpl.class);

    @Override
    public List<Lembur> getAllLembur() {
        try {
            logger.info("Mengambil semua data lembur");
            return lemburRepository.findAll();
        } catch (Exception e) {
            logger.error("Gagal mengambil semua data lembur", e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data lembur", e);
        }
    }

    @Override
    public Lembur getLemburById(Long id) {
        try {
            logger.info("Mengambil lembur dengan id: {}", id);
            Optional<Lembur> lemburOptional = lemburRepository.findById(id);
            return lemburOptional.orElse(null);
        } catch (Exception e) {
            logger.error("Gagal mengambil lembur dengan id: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil lembur", e);
        }
    }

    @Override
    public List<Lembur> getLemburByUserId(Long userId) {
        try {
            logger.info("Mengambil lembur berdasarkan userId: {}", userId);
            return lemburRepository.findByuserId(userId);
        } catch (Exception e) {
            logger.error("Gagal mengambil lembur berdasarkan userId: {}", userId, e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil lembur berdasarkan userId", e);
        }
    }

    @Override
    public Lembur IzinLembur(Long userId, Lembur lembur) {
        try {
            logger.info("Mengajukan izin lembur untuk userId: {}", userId);
            UserModel userLembur = userRepository.findById(userId).orElse(null);

            if (userLembur == null) {
                logger.error("User id tidak ditemukan: {}", userId);
                throw new NotFoundException("User id tidak ditemukan");
            }

            lembur.setUser(userLembur);
            return lemburRepository.save(lembur);
        } catch (Exception e) {
            logger.error("Gagal mengajukan izin lembur untuk userId: {}", userId, e);
            throw new RuntimeException("Terjadi kesalahan saat mengajukan izin lembur", e);
        }
    }

    @Override
    public Lembur updateLembur(Long id, Lembur updatedLembur) {
        try {
            logger.info("Mengupdate lembur dengan id: {}", id);
            Optional<Lembur> lemburOptional = lemburRepository.findById(id);

            if (lemburOptional.isPresent()) {
                Lembur lembur = lemburOptional.get();
                lembur.setTanggalLembur(updatedLembur.getTanggalLembur());
                lembur.setJamMulai(updatedLembur.getJamMulai());
                lembur.setJamSelesai(updatedLembur.getJamSelesai());
                lembur.setKeteranganLembur(updatedLembur.getKeteranganLembur());
                lembur.setUser(updatedLembur.getUser());
                return lemburRepository.save(lembur);
            } else {
                logger.error("Lembur dengan id {} tidak ditemukan", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Gagal mengupdate lembur dengan id: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan saat mengupdate lembur", e);
        }
    }

    @Override
    public List<Lembur> getAllByAdmin(Long adminId) {
        try {
            logger.info("Mengambil semua lembur berdasarkan adminId: {}", adminId);
            Optional<Admin> adminOptional = adminRepository.findById(adminId);

            if (!adminOptional.isPresent()) {
                logger.error("Admin dengan id {} tidak ditemukan", adminId);
                throw new NotFoundException("Id Admin tidak ditemukan dengan id: " + adminId);
            }

            List<UserModel> users = userRepository.findByadminIdAbsensi(adminId);

            if (users.isEmpty()) {
                logger.error("Tidak ada pengguna yang terkait dengan admin dengan id: {}", adminId);
                throw new NotFoundException("Tidak ada pengguna yang terkait dengan admin dengan id: " + adminId);
            }

            List<Lembur> lemburList = new ArrayList<>();
            for (UserModel user : users) {
                lemburList.addAll(lemburRepository.findByUser(user));
            }

            return lemburList;
        } catch (Exception e) {
            logger.error("Gagal mengambil lembur berdasarkan adminId: {}", adminId, e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil lembur berdasarkan adminId", e);
        }
    }

    @Override
    public void deleteLembur(Long id) {
        try {
            logger.info("Menghapus lembur dengan id: {}", id);
            lemburRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Gagal menghapus lembur dengan id: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan saat menghapus lembur", e);
        }
    }
}
