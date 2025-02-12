package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Jabatan;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.JabatanRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.JabatanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class JabatanImpl implements JabatanService {

    private static final Logger logger = LoggerFactory.getLogger(JabatanImpl.class);

    @Autowired
    private UserRepository userRepository;
    private final JabatanRepository jabatanRepository;
    private final AdminRepository adminRepository;

    public JabatanImpl(AdminRepository adminRepository, JabatanRepository jabatanRepository) {
        this.adminRepository = adminRepository;
        this.jabatanRepository = jabatanRepository;
    }

    @Override
    public List<Jabatan> getAllJabatan() {
        logger.info("Mengambil semua data Jabatan.");
        return jabatanRepository.findAll();
    }

    @Override
    public List<Jabatan> getJabatanByAdminId(Long adminId) {
        logger.info("Mengambil data Jabatan untuk adminId: {}", adminId);
        return jabatanRepository.findByAdminId(adminId);
    }

    @Override
    public Optional<Jabatan> getJabatanById(Long idJabatan) {
        logger.info("Mengambil Jabatan dengan id: {}", idJabatan);
        return jabatanRepository.findById(idJabatan);
    }

    @Override
    public Jabatan addJabatan(Long adminId, Jabatan jabatan) {
        try {
            logger.info("Menambahkan Jabatan baru untuk adminId: {}", adminId);

            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new NotFoundException("Admin not found with id: " + adminId));

            jabatan.setAdmin(admin);
            jabatan.setNamaJabatan(jabatan.getNamaJabatan());
            Jabatan savedJabatan = jabatanRepository.save(jabatan);

            logger.info("Berhasil menambahkan Jabatan dengan id: {}", savedJabatan.getIdJabatan());
            return savedJabatan;
        } catch (Exception e) {
            logger.error("Gagal menambahkan Jabatan untuk adminId: {}", adminId, e);
            throw e;
        }
    }

    @Override
    public List<Jabatan> getJabatanBySuperAdminId(Long idSuperAdmin) {
        try {
            logger.info("Mengambil semua Jabatan untuk Super Admin dengan id: {}", idSuperAdmin);
            List<Admin> admins = adminRepository.findBySuperAdminId(idSuperAdmin);
            List<Jabatan> jabatans = new ArrayList<>();
            for (Admin admin : admins) {
                jabatans.addAll(jabatanRepository.findByAdminId(admin.getId()));
            }
            logger.info("Berhasil mendapatkan {} Jabatan untuk Super Admin id: {}", jabatans.size(), idSuperAdmin);
            return jabatans;
        } catch (Exception e) {
            logger.error("Gagal mengambil Jabatan untuk Super Admin id: {}", idSuperAdmin, e);
            throw e;
        }
    }

    @Override
    public Jabatan editJabatan(Long adminId, Jabatan jabatan) {
        try {
            logger.info("Mengedit Jabatan dengan id: {} untuk adminId: {}", jabatan.getIdJabatan(), adminId);

            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new NotFoundException("Admin not found with id: " + adminId));

            Jabatan existingJabatan = jabatanRepository.findById(jabatan.getIdJabatan())
                    .orElseThrow(() -> new NotFoundException("Jabatan not found with id: " + jabatan.getIdJabatan()));

            if (!existingJabatan.getAdmin().equals(admin)) {
                throw new IllegalArgumentException("You are not authorized to edit this Jabatan.");
            }

            jabatan.setAdmin(admin);
            Jabatan updatedJabatan = jabatanRepository.save(jabatan);

            logger.info("Berhasil mengedit Jabatan dengan id: {}", updatedJabatan.getIdJabatan());
            return updatedJabatan;
        } catch (Exception e) {
            logger.error("Gagal mengedit Jabatan dengan id: {} untuk adminId: {}", jabatan.getIdJabatan(), adminId, e);
            throw e;
        }
    }

    @Override
    public Jabatan editJabatanById(Long idJabatan, Jabatan updateJabatan) {
        try {
            logger.info("Mengedit Jabatan dengan id: {}", idJabatan);
            Jabatan jabatan = jabatanRepository.findById(idJabatan)
                    .orElseThrow(() -> new NotFoundException("id jabatan tidak ditemukan " + idJabatan));

            jabatan.setNamaJabatan(updateJabatan.getNamaJabatan());
            Jabatan updatedJabatan = jabatanRepository.save(jabatan);

            logger.info("Berhasil mengedit Jabatan dengan id: {}", updatedJabatan.getIdJabatan());
            return updatedJabatan;
        } catch (Exception e) {
            logger.error("Gagal mengedit Jabatan dengan id: {}", idJabatan, e);
            throw e;
        }
    }

    @Override
    public Map<String, Boolean> deleteJabatan(Long idJabatan) {
        try {
            logger.info("Menghapus Jabatan dengan id: {}", idJabatan);

            List<UserModel> users = userRepository.findByIdJabatan(idJabatan);
            for (UserModel user : users) {
                userRepository.save(user);
            }

            jabatanRepository.deleteById(idJabatan);

            Map<String, Boolean> res = new HashMap<>();
            res.put("Deleted", Boolean.TRUE);
            logger.info("Berhasil menghapus Jabatan dengan id: {}", idJabatan);
            return res;
        } catch (Exception e) {
            logger.error("Gagal menghapus Jabatan dengan id: {}", idJabatan, e);
            Map<String, Boolean> res = new HashMap<>();
            res.put("Deleted", Boolean.FALSE);
            return res;
        }
    }
}

