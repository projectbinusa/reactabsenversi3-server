package com.example.absensireact.impl;

import com.example.absensireact.dto.LokasiDTO;
import com.example.absensireact.dto.AdminDTO;
import com.example.absensireact.dto.OrganisasiDTO;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Lokasi;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.model.SuperAdmin;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.LokasiRepository;
import com.example.absensireact.repository.OrganisasiRepository;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.service.LokasiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LokasiImpl implements LokasiService {

    @Autowired
    private LokasiRepository lokasiRepository;

    @Autowired
    private OrganisasiRepository organisasiRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    private static final Logger logger = LoggerFactory.getLogger(LokasiImpl.class);

    @Override
    public List<Lokasi> getAllBySuperAdmin(Long idSuperAdmin) {
        try {
            logger.info("Memulai proses getAllBySuperAdmin dengan ID Super Admin: {}", idSuperAdmin);

            Optional<SuperAdmin> superAdminOptional = superAdminRepository.findById(idSuperAdmin);
            if (superAdminOptional.isEmpty()) {
                logger.error("ID Super Admin tidak ditemukan: {}", idSuperAdmin);
                throw new NotFoundException("ID Super Admin tidak ditemukan: " + idSuperAdmin);
            }
            SuperAdmin superAdmin = superAdminOptional.get();

            List<Admin> adminList = adminRepository.findBySuperAdmin(superAdmin);

            List<Lokasi> lokasiList = new ArrayList<>();

            for (Admin admin : adminList) {
                List<Lokasi> adminLokasiList = lokasiRepository.findByAdmin(admin);
                lokasiList.addAll(adminLokasiList);
            }

            logger.info("Proses getAllBySuperAdmin berhasil dengan ID Super Admin: {}", idSuperAdmin);
            return lokasiList;
        } catch (Exception e) {
            logger.error("Terjadi kesalahan pada proses getAllBySuperAdmin dengan ID Super Admin: {}", idSuperAdmin, e);
            throw e;
        }
    }


    @Override
    public List<Lokasi>getAllByAdmin(Long idAdmin){
        try{
            return lokasiRepository.findbyAdmin(idAdmin);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan pada proses get all lokasi dengan ID Admin: {}: ", idAdmin, e);
            throw e;
        }
    }

    @Override
    public Lokasi tambahLokasi(Long idAdmin, Lokasi lokasi, Long idOrganisasi) {
        try {
            logger.info("Memulai proses tambahLokasi dengan ID Admin: {} dan ID Organisasi: {}", idAdmin, idOrganisasi);

            Optional<Admin> adminOptional = adminRepository.findById(idAdmin);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                Optional<Organisasi> organisasiOptional = organisasiRepository.findById(idOrganisasi);
                if (organisasiOptional.isPresent()) {
                    Organisasi organisasi = organisasiOptional.get();
                    lokasi.setNamaLokasi(lokasi.getNamaLokasi());
                    lokasi.setAlamat(lokasi.getAlamat());
                    lokasi.setAdmin(admin);
                    lokasi.setDeleted(0);
                    lokasi.setOrganisasi(organisasi);

                    logger.info("Proses tambahLokasi berhasil dengan ID Admin: {} dan ID Organisasi: {}", idAdmin, idOrganisasi);
                    return lokasiRepository.save(lokasi);
                }
                logger.error("Organisasi dengan ID {} tidak ditemukan.", idOrganisasi);
                throw new NotFoundException("Organisasi dengan ID " + idOrganisasi + " tidak ditemukan.");
            }
            logger.error("Admin dengan ID {} tidak ditemukan.", idAdmin);
            throw new NotFoundException("Admin dengan ID " + idAdmin + " tidak ditemukan.");
        } catch (Exception e) {
            logger.error("Terjadi kesalahan pada proses tambahLokasi dengan ID Admin: {} dan ID Organisasi: {}", idAdmin, idOrganisasi, e);
            throw e;
        }
    }

    @Override
    public Lokasi tambahLokasiBySuperAdmin(Long idSuperAdmin, Lokasi lokasi, Long idOrganisasi) {
        try {
            logger.info("Memulai proses tambahLokasiBySuperAdmin dengan ID Super Admin: {} dan ID Organisasi: {}", idSuperAdmin, idOrganisasi);

            Optional<SuperAdmin> superadminOptional = superAdminRepository.findById(idSuperAdmin);
            if (superadminOptional.isPresent()) {
                Optional<Organisasi> organisasiOptional = organisasiRepository.findById(idOrganisasi);
                if (organisasiOptional.isPresent()) {
                    Organisasi organisasi = organisasiOptional.get();
                    lokasi.setNamaLokasi(lokasi.getNamaLokasi());
                    lokasi.setAlamat(lokasi.getAlamat());
                    lokasi.setAdmin(organisasi.getAdmin());
                    lokasi.setOrganisasi(organisasi);
                    lokasi.setDeleted(0);

                    logger.info("Proses tambahLokasiBySuperAdmin berhasil dengan ID Super Admin: {} dan ID Organisasi: {}", idSuperAdmin, idOrganisasi);
                    return lokasiRepository.save(lokasi);
                }
                logger.error("Organisasi dengan ID {} tidak ditemukan.", idOrganisasi);
                throw new NotFoundException("Organisasi dengan ID " + idOrganisasi + " tidak ditemukan.");
            }
            logger.error("Super admin dengan ID {} tidak ditemukan.", idSuperAdmin);
            throw new NotFoundException("Super admin dengan ID " + idSuperAdmin + " tidak ditemukan.");
        } catch (Exception e) {
            logger.error("Terjadi kesalahan pada proses tambahLokasiBySuperAdmin dengan ID Super Admin: {} dan ID Organisasi: {}", idSuperAdmin, idOrganisasi, e);
            throw e;
        }
    }

    @Override
    public List<LokasiDTO> getAllLokasi() {
        try {
            logger.info("Memulai proses getAllLokasi");

            List<LokasiDTO> lokasiDTOList = lokasiRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            logger.info("Proses getAllLokasi berhasil");
            return lokasiDTOList;
        } catch (Exception e) {
            logger.error("Terjadi kesalahan pada proses getAllLokasi", e);
            throw e;
        }
    }

    @Override
    public LokasiDTO getLokasiById(Long idLokasi) {
        try {
            logger.info("Memulai proses getLokasiById dengan ID Lokasi: {}", idLokasi);

            Optional<Lokasi> lokasi = lokasiRepository.findById(idLokasi);
            LokasiDTO lokasiDTO = lokasi.map(this::convertToDto).orElse(null);

            logger.info("Proses getLokasiById berhasil dengan ID Lokasi: {}", idLokasi);
            return lokasiDTO;
        } catch (Exception e) {
            logger.error("Terjadi kesalahan pada proses getLokasiById dengan ID Lokasi: {}", idLokasi, e);
            throw e;
        }
    }

    @Override
    public Optional<Lokasi> getByIdLokasi(Long idLokasi) {
        try {
            logger.info("Fetching Lokasi with id: {}", idLokasi);
            return lokasiRepository.findById(idLokasi);
        } catch (Exception e) {
            logger.error("Error fetching Lokasi with id: {}", idLokasi, e);
            return Optional.empty();
        }
    }

    @Override
    public LokasiDTO updateLokasi(Long idLokasi, LokasiDTO lokasiDTO) {
        try {
            return lokasiRepository.findById(idLokasi).map(existingLokasi -> {
                updateEntity(existingLokasi, lokasiDTO);
                lokasiRepository.save(existingLokasi);
                logger.info("Updated Lokasi with id: {}", idLokasi);
                return convertToDto(existingLokasi);
            }).orElse(null);
        } catch (Exception e) {
            logger.error("Error updating Lokasi with id: {}", idLokasi, e);
            return null;
        }
    }


    @Override
    public void deleteLokasi(Long idLokasi) {
        try {
            lokasiRepository.deleteById(idLokasi);
            logger.info("Deleted Lokasi with id: {}", idLokasi);
        } catch (Exception e) {
            logger.error("Error deleting Lokasi with id: {}", idLokasi, e);
        }
    }

    @Override
    public void DeleteLokasiSementara(Long idLokasi) {
        try {
            lokasiRepository.findById(idLokasi).ifPresent(lokasi -> {
                lokasi.setDeleted(1);
                lokasiRepository.save(lokasi);
                logger.info("Temporarily deleted Lokasi with id: {}", idLokasi);
            });
        } catch (Exception e) {
            logger.error("Error temporarily deleting Lokasi with id: {}", idLokasi, e);
        }
    }

    @Override
    public void PemulihanDataLokasi(Long idLokasi) {
        try {
            lokasiRepository.findById(idLokasi).ifPresent(lokasi -> {
                lokasi.setDeleted(0);
                lokasiRepository.save(lokasi);
                logger.info("Restored Lokasi with id: {}", idLokasi);
            });
        } catch (Exception e) {
            logger.error("Error restoring Lokasi with id: {}", idLokasi, e);
        }
    }

    @Override
    public OrganisasiDTO getOrganisasiById(Long id) {
        Logger logger = LoggerFactory.getLogger(getClass());
        try {
            logger.info("Mencari organisasi dengan ID: {}", id);
            Optional<Organisasi> lokasi = organisasiRepository.findById(id);

            if (lokasi.isPresent()) {
                logger.info("Organisasi ditemukan dengan ID: {}", id);
                return convertOrganisasiToDto(lokasi.get());
            } else {
                logger.warn("Organisasi dengan ID: {} tidak ditemukan", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mencari organisasi dengan ID: {}", id, e);
            return null;
        }
    }

    @Override
    public AdminDTO getAdminById(Long id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isPresent()) {
            return convertAdminToDto(adminOptional.get());
        } else {
            return null;
        }
    }

    @Override
    public Lokasi updateLokasiByIdlokasi(Long idLokasi, Lokasi lokasiDetails) {
        try {
            return lokasiRepository.findById(idLokasi).map(lokasi -> {
                lokasi.setNamaLokasi(lokasiDetails.getNamaLokasi());
                lokasi.setAlamat(lokasiDetails.getAlamat());
                logger.info("Updated Lokasi details with id: {}", idLokasi);
                return lokasiRepository.save(lokasi);
            }).orElseThrow(() -> new NotFoundException("Lokasi not found with id " + idLokasi));
        } catch (Exception e) {
            logger.error("Error updating Lokasi details with id: {}", idLokasi, e);
            throw e;
        }
    }

    private OrganisasiDTO convertOrganisasiToDto(Organisasi organisasi) {
        OrganisasiDTO organisasiDTO = new OrganisasiDTO();
        organisasiDTO.setId(organisasi.getId());
        organisasiDTO.setNamaOrganisasi(organisasi.getNamaOrganisasi());
        organisasiDTO.setAlamat(organisasi.getAlamat());
        organisasiDTO.setNomerTelepon(organisasi.getNomerTelepon());
        organisasiDTO.setEmailOrganisasi(organisasi.getEmailOrganisasi());
        organisasiDTO.setKecamatan(organisasi.getKecamatan());
        organisasiDTO.setKabupaten(organisasi.getKabupaten());
        organisasiDTO.setProvinsi(organisasi.getProvinsi());
        organisasiDTO.setStatus(organisasi.getStatus());
        organisasiDTO.setFotoOrganisasi(organisasi.getFotoOrganisasi());

        // Set Admin ID jika ada admin terkait
        Admin admin = organisasi.getAdmin();
        if (admin != null) {
            organisasiDTO.setAdmin(String.valueOf(admin.getId()));
        }

        return organisasiDTO;
    }




 
 
    private AdminDTO convertAdminToDto(Admin admin) {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(admin.getId());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setPassword(admin.getPassword());
        adminDTO.setUsername(admin.getUsername());
        adminDTO.setImageAdmin(admin.getImageAdmin());


        return adminDTO;
    }



    private void updateEntity(Lokasi existingLokasi, LokasiDTO lokasiDTO) {
        existingLokasi.setNamaLokasi(lokasiDTO.getNamaLokasi());
        existingLokasi.setAlamat(lokasiDTO.getAlamat());

        Optional<Organisasi> organisasiOptional = organisasiRepository.findById(lokasiDTO.getIdOrganisasi());
        organisasiOptional.ifPresent(existingLokasi::setOrganisasi);
    }

    private LokasiDTO convertToDto(Lokasi lokasi) {
        LokasiDTO lokasiDTO = new LokasiDTO();
        lokasiDTO.setNamaLokasi(lokasi.getNamaLokasi());
        lokasiDTO.setAlamat(lokasi.getAlamat());

        // Set ID Organisasi
        if (lokasi.getOrganisasi() != null) {
            lokasiDTO.setIdOrganisasi(lokasi.getOrganisasi().getId());
        }

        // Set Admin
        if (lokasi.getAdmin() != null) {
            AdminDTO adminDTO = convertAdminToDto(lokasi.getAdmin());
            lokasiDTO.setAdmin(adminDTO);

            // Set Admin ID
            lokasiDTO.setAdminId(lokasi.getAdmin().getId());
        }

        return lokasiDTO;
    }



}
