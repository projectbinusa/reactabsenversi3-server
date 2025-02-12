package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Shift;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.OrganisasiRepository;
import com.example.absensireact.repository.ShiftRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.ShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShiftImpl implements ShiftService {
    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisasiRepository organisasiRepository;
    private static final Logger logger = LoggerFactory.getLogger(ShiftImpl.class);

    @Override
    public List<Shift> getAllShift() {
        try {
            logger.info("Fetching all shifts");
            return shiftRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all shifts", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Shift> getAllShiftByAdmin(Long idAdmin) {
        try {
            logger.info("Fetching shifts for admin with ID: {}", idAdmin);
            return shiftRepository.getByIdAdmin(idAdmin);
        } catch (Exception e) {
            logger.error("Error fetching shifts for admin with ID: {}", idAdmin, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Shift> getshiftById(Long id) {
        try {
            logger.info("Fetching shift by ID: {}", id);
            return shiftRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error fetching shift by ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Shift> getbyAdmin(Long idAdmin) {
        try {
            logger.info("Fetching shift by admin ID: {}", idAdmin);
            return shiftRepository.findByIdAdmin(idAdmin);
        } catch (Exception e) {
            logger.error("Error fetching shift by admin ID: {}", idAdmin, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Shift> getShiftsByAdmin(Long idAdmin) {
        try {
            logger.info("Fetching shifts by admin ID: {}", idAdmin);
            return shiftRepository.findByAdminId(idAdmin);
        } catch (Exception e) {
            logger.error("Error fetching shifts by admin ID: {}", idAdmin, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Shift> getShiftBySuperAdminId(Long idSuperAdmin) {
        try {
            logger.info("Fetching shifts for super admin ID: {}", idSuperAdmin);
            List<Admin> admins = adminRepository.findBySuperAdminId(idSuperAdmin);
            List<Shift> shifts = new ArrayList<>();
            for (Admin admin : admins) {
                shifts.addAll(shiftRepository.getByIdAdmin(admin.getId()));
            }
            return shifts;
        } catch (Exception e) {
            logger.error("Error fetching shifts for super admin ID: {}", idSuperAdmin, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Shift> getByUserId(Long userId) {
        try {
            logger.info("Fetching shift for user ID: {}", userId);
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("id user tidak ditemukan"));
            return shiftRepository.findById(user.getShift().getId());
        } catch (NotFoundException e) {
            logger.warn("User ID {} not found", userId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error fetching shift for user ID: {}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public Shift PostShift(Long idAdmin, Shift shift) {
        try {
            logger.info("Memulai proses pembuatan shift untuk admin dengan ID: {}", idAdmin);

            Optional<Admin> adminOptional = adminRepository.findById(idAdmin);
            if (adminOptional.isEmpty()) {
                logger.error("Admin dengan ID: {} tidak ditemukan", idAdmin);
                throw new NotFoundException("Admin tidak ditemukan");
            }

            boolean namaShiftExisting = shiftRepository.existsByNamaShift(shift.getNamaShift());
            if (namaShiftExisting) {
                logger.error("Shift dengan nama: {} sudah terdaftar", shift.getNamaShift());
                throw new IllegalStateException("Shift dengan nama: " + shift.getNamaShift() + " sudah terdaftar");
            }

            Admin admin = adminOptional.get();
            shift.setAdmin(admin);
            shift.setNamaShift(shift.getNamaShift());
            shift.setWaktuMasuk(shift.getWaktuMasuk());
            shift.setWaktuPulang(shift.getWaktuPulang());
            shift.setDeleted(0);

            Shift savedShift = shiftRepository.save(shift);
            logger.info("Shift dengan nama: {} berhasil disimpan", shift.getNamaShift());
            return savedShift;
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat membuat shift: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Shift editShiftById(Long id, Shift updatedShift) {
        try {
            logger.info("Memulai proses pengeditan shift dengan ID: {}", id);

            Optional<Shift> shiftOptional = shiftRepository.findById(id);
            if (shiftOptional.isEmpty()) {
                logger.error("Shift dengan ID: {} tidak ditemukan", id);
                throw new NotFoundException("Shift id tidak ditemukan");
            }

            Shift shift = shiftOptional.get();
            shift.setNamaShift(updatedShift.getNamaShift());
            shift.setWaktuMasuk(updatedShift.getWaktuMasuk());
            shift.setWaktuPulang(updatedShift.getWaktuPulang());

            Shift updated = shiftRepository.save(shift);
            logger.info("Shift dengan ID: {} berhasil diperbarui", id);
            return updated;
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengedit shift dengan ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public Map<String, Boolean> delete(Long id) {
        Map<String, Boolean> res = new HashMap<>();
        try {
            logger.info("Deleting shift ID: {}", id);
            List<UserModel> users = userRepository.findByIdShift(id);
            for (UserModel user : users) {
                user.setShift(null);
                userRepository.save(user);
            }

            shiftRepository.deleteById(id);
            res.put("Deleted", Boolean.TRUE);
            return res;
        } catch (Exception e) {
            logger.error("Error deleting shift ID: {}", id, e);
            res.put("Deleted", Boolean.FALSE);
            return res;
        }
    }

    @Override
    public void DeleteShiftSementara(Long id) {
        try {
            logger.info("Soft deleting shift ID: {}", id);
            Optional<Shift> shiftOptional = shiftRepository.findById(id);
            if (shiftOptional.isPresent()) {
                Shift shift = shiftOptional.get();
                shift.setDeleted(1);
                shiftRepository.save(shift);
            }
        } catch (Exception e) {
            logger.error("Error soft deleting shift ID: {}", id, e);
        }
    }

    @Override
    public void PemulihanDataShift(Long id) {
        try {
            logger.info("Restoring shift ID: {}", id);
            Optional<Shift> shiftOptional = shiftRepository.findById(id);
            if (shiftOptional.isPresent()) {
                Shift shift = shiftOptional.get();
                shift.setDeleted(0);
                shiftRepository.save(shift);
            }
        } catch (Exception e) {
            logger.error("Error restoring shift ID: {}", id, e);
        }
    }
}
