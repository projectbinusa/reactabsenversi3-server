package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Cuti;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.CutiRepository;
 import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.CutiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CutiImpl implements CutiService {
    private final CutiRepository cutiRepository;

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    private static final Logger logger = LoggerFactory.getLogger(CutiImpl.class);

    public CutiImpl(CutiRepository cutiRepository, UserRepository userRepository, AdminRepository adminRepository) {
        this.cutiRepository = cutiRepository;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public List<Cuti> GetCutiAll() {
        try {
            logger.info("Mengambil semua data cuti");
            return cutiRepository.findAll();
        } catch (Exception e) {
            logger.error("Gagal mengambil semua data cuti", e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data cuti", e);
        }
    }

    @Override
    public Optional<Cuti> GetCutiById(long id) {
        try {
            logger.info("Mengambil cuti dengan id: {}", id);
            return cutiRepository.findById(id);
        } catch (Exception e) {
            logger.error("Gagal mengambil cuti dengan id: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil cuti", e);
        }
    }

    @Override
    public List<Cuti> GetCutiByUserId(Long userId) {
        try {
            logger.info("Mengambil cuti berdasarkan userId: {}", userId);
            return cutiRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("Gagal mengambil cuti berdasarkan userId: {}", userId, e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil cuti", e);
        }
    }

    @Override
    public Cuti updateCutiById(Long id, Cuti updatedCuti) {
        try {
            logger.info("Memperbarui cuti dengan id: {}", id);
            Optional<Cuti> cutiOptional = cutiRepository.findById(id);
            if (cutiOptional.isPresent()) {
                Cuti existingCuti = cutiOptional.get();
                existingCuti.setAwalCuti(updatedCuti.getAwalCuti());
                existingCuti.setAkhirCuti(updatedCuti.getAkhirCuti());
                existingCuti.setMasukKerja(updatedCuti.getMasukKerja());
                existingCuti.setKeperluan(updatedCuti.getKeperluan());
                existingCuti.setStatus(updatedCuti.getStatus());
                existingCuti.setUser(updatedCuti.getUser());

                return cutiRepository.save(existingCuti);
            } else {
                logger.error("Cuti dengan id {} tidak ditemukan", id);
                throw new NotFoundException("Cuti tidak ditemukan dengan id " + id);
            }
        } catch (Exception e) {
            logger.error("Gagal memperbarui cuti dengan id: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan saat memperbarui cuti", e);
        }
    }

    @Override
    public List<Cuti> getAllByAdmin(Long adminId) {
        try {
            logger.info("Mengambil semua data cuti berdasarkan adminId: {}", adminId);
            Optional<Admin> adminOptional = adminRepository.findById(adminId);

            if (adminOptional.isPresent()) {
                Long admin = adminOptional.get().getId();
                List<UserModel> users = userRepository.findByadminIdAbsensi(admin);

                if (users.isEmpty()) {
                    throw new NotFoundException("Tidak ada pengguna terkait dengan adminId: " + adminId);
                }

                List<Cuti> cutiList = new ArrayList<>();
                for (UserModel user : users) {
                    cutiList.addAll(cutiRepository.findByUser(user));
                }
                return cutiList;
            } else {
                throw new NotFoundException("Admin tidak ditemukan dengan id: " + adminId);
            }
        } catch (Exception e) {
            logger.error("Gagal mengambil semua data cuti berdasarkan adminId: {}", adminId, e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data cuti", e);
        }
    }

    @Override
    public Cuti IzinCuti(Long userId, Cuti cuti) {
        try {
            logger.info("Memproses izin cuti untuk userId: {}", userId);
            Optional<UserModel> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("User dengan id " + userId + " tidak ditemukan");
            }

            UserModel user = userOptional.get();
            cuti.setUser(user);
            cuti.setStatus("diproses");
            cuti.setOrganisasi(user.getOrganisasi());

            return cutiRepository.save(cuti);
        } catch (Exception e) {
            logger.error("Gagal memproses izin cuti untuk userId: {}", userId, e);
            throw new RuntimeException("Terjadi kesalahan saat memproses izin cuti", e);
        }
    }

    @Override
    public Cuti TolakCuti(Long id, Cuti cuti) {
        try {
            logger.info("Menolak cuti dengan id: {}", id);
            Cuti existingCuti = cutiRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Cuti tidak ditemukan dengan id " + id));

            existingCuti.setStatus("ditolak");
            return cutiRepository.save(existingCuti);
        } catch (Exception e) {
            logger.error("Gagal menolak cuti dengan id: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan saat menolak cuti", e);
        }
    }

    @Override
    public Cuti TerimaCuti(Long id, Cuti cuti) {
        try {
            logger.info("Menerima cuti dengan id: {}", id);
            Cuti existingCuti = cutiRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Cuti tidak ditemukan dengan id " + id));

            existingCuti.setStatus("disetujui");
            return cutiRepository.save(existingCuti);
        } catch (Exception e) {
            logger.error("Gagal menerima cuti dengan id: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan saat menerima cuti", e);
        }
    }

    @Override
    public boolean deleteCuti(Long id) {
        try {
            logger.info("Menghapus cuti dengan id: {}", id);
            if (cutiRepository.existsById(id)) {
                cutiRepository.deleteById(id);
                return true;
            } else {
                throw new NotFoundException("Cuti tidak ditemukan dengan id: " + id);
            }
        } catch (Exception e) {
            logger.error("Gagal menghapus cuti dengan id: {}", id, e);
            throw new RuntimeException("Terjadi kesalahan saat menghapus cuti", e);
        }
    }
}
