package com.example.absensireact.impl;

import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.*;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.OrangTuaRepository;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.OrangTuaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrangTuaImpl implements OrangTuaService {

    private static final String BASE_URL = "https://s3.lynk2.co/api/s3/orang_tua";

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    @Autowired
    private AdminRepository adminrepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    PasswordEncoder encoder;

    private static final Logger logger = LoggerFactory.getLogger(OrangTuaImpl.class);

    @Override
    public List<OrangTua> getAllOrangTua() {
        try {
            logger.info("Fetching all OrangTua");
            return orangTuaRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all OrangTua", e);
            throw e;
        }
    }

    @Override
    public Optional<OrangTua> getOrangTuaById(Long id) {
        try {
            logger.info("Fetching OrangTua with ID: {}", id);
            return orangTuaRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error fetching OrangTua with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<OrangTua> getAllByAdmin(Long idAdmin) {
        try {
            logger.info("Fetching all OrangTua by Admin ID: {}", idAdmin);
            return orangTuaRepository.findAllByAdmin(idAdmin);
        } catch (Exception e) {
            logger.error("Error fetching OrangTua by Admin ID: {}", idAdmin, e);
            throw e;
        }
    }

    @Override
    public OrangTua tambahOrangTua(Long idAdmin, OrangTua orangTua) {
        try {
            logger.info("Adding new OrangTua with email: {}", orangTua.getEmail());
            Optional<Admin> adminOptional = adminrepository.findById(idAdmin);
            if (adminOptional.isPresent()) {
                boolean emailExists = adminrepository.existsByEmail(orangTua.getEmail()) ||
                        superAdminRepository.existsByEmail(orangTua.getEmail()) ||
                        userRepository.existsByEmail(orangTua.getEmail()) ||
                        orangTuaRepository.existsByEmail(orangTua.getEmail());
                if (emailExists) {
                    logger.error("Email {} already exists", orangTua.getEmail());
                    throw new NotFoundException("email : " + orangTua.getEmail() + " telah terdaftar");
                }
                Admin admin = adminOptional.get();
                orangTua.setAdmin(admin);
                orangTua.setRole("Wali Murid");
                orangTua.setPassword(encoder.encode(orangTua.getPassword()));
                return orangTuaRepository.save(orangTua);
            }
            throw new NotFoundException("Admin tidak ditemukan");
        } catch (Exception e) {
            logger.error("Error adding OrangTua", e);
            throw e;
        }
    }

    @Override
    public OrangTua editOrangTuaById(Long id, Long idAdmin, OrangTua updateOrangTua) {
        try {
            logger.info("Editing OrangTua with ID: {}", id);
            OrangTua orangTua = orangTuaRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("id ortu tidak ditemukan : " + id));

            Admin admin = adminrepository.findById(idAdmin)
                    .orElseThrow(() -> new NotFoundException("Admin dengan id: " + idAdmin + " tidak ditemukan"));
            orangTua.setAdmin(admin);
            if (orangTuaRepository.existsByNama(updateOrangTua.getNama())) {
                logger.error("Username {} sudah digunakan", updateOrangTua.getNama());
                throw new BadRequestException("Username " + updateOrangTua.getNama() + " telah digunakan");
            }
            orangTua.setNama(updateOrangTua.getNama());
            orangTua.setEmail(updateOrangTua.getEmail());
            orangTua.setImageOrtu(updateOrangTua.getImageOrtu());
            orangTua.setPassword(encoder.encode(updateOrangTua.getPassword()));
            return orangTuaRepository.save(orangTua);
        } catch (Exception e) {
            logger.error("Error editing OrangTua with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public OrangTua uploadImage(Long id, MultipartFile image) throws IOException {
        try {
            logger.info("Uploading image for OrangTua with ID: {}", id);
            Optional<OrangTua> orangTuaOptional = orangTuaRepository.findById(id);
            if (orangTuaOptional.isEmpty()) {
                throw new NotFoundException("Id ortu tidak ditemukan");
            }
            String fileUrl = uploadFoto(image);
            OrangTua orangTua = orangTuaOptional.get();
            orangTua.setImageOrtu(fileUrl);
            return orangTuaRepository.save(orangTua);
        } catch (IOException e) {
            logger.error("Error uploading image for OrangTua with ID: {}", id, e);
            throw e;
        }
    }

    private String uploadFoto(MultipartFile multipartFile) throws IOException {
        try {
            logger.info("Mengunggah foto...");
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", multipartFile.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, requestEntity, String.class);
            String fileUrl = extractFileUrlFromResponse(response.getBody());

            logger.info("Foto berhasil diunggah. URL: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            logger.error("Gagal mengunggah foto: ", e);
            throw new IOException("Error saat mengunggah foto", e);
        }
    }

    @Override
    public OrangTua putPasswordOrangTua(PasswordDTO passwordDTO, Long id) {
        try {
            logger.info("Memperbarui password untuk OrangTua dengan ID: {}", id);
            OrangTua update = orangTuaRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Id Not Found"));

            boolean isOldPasswordCorrect = encoder.matches(passwordDTO.getOld_password(), update.getPassword());
            if (!isOldPasswordCorrect) {
                logger.error("Password lama tidak sesuai untuk ID: {}", id);
                throw new NotFoundException("Password lama tidak sesuai");
            }

            if (passwordDTO.getNew_password().equals(passwordDTO.getConfirm_new_password())) {
                update.setPassword(encoder.encode(passwordDTO.getNew_password()));
                logger.info("Password berhasil diperbarui untuk ID: {}", id);
                return orangTuaRepository.save(update);
            } else {
                logger.error("Password baru dan konfirmasi password tidak cocok untuk ID: {}", id);
                throw new BadRequestException("Password tidak sesuai");
            }
        } catch (Exception e) {
            logger.error("Gagal memperbarui password untuk ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Admin getAdminByOrangTuaId(Long id) {
        try {
            logger.info("Mengambil admin berdasarkan ID OrangTua: {}", id);
            OrangTua orangTua = orangTuaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("OrangTua not found with id: " + id));
            return orangTua.getAdmin();
        } catch (Exception e) {
            logger.error("Gagal mengambil admin untuk ID OrangTua: {}", id, e);
            throw e;
        }
    }

    private String extractFileUrlFromResponse(String responseBody) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(responseBody);
            JsonNode dataNode = jsonResponse.path("data");
            String urlFile = dataNode.path("url_file").asText();
            logger.info("File URL berhasil diekstrak: {}", urlFile);
            return urlFile;
        } catch (Exception e) {
            logger.error("Gagal mengekstrak file URL dari response", e);
            throw new IOException("Error parsing response", e);
        }
    }

    @Override
    public OrangTua ubahUsernamedanemail(Long id, OrangTua updateOrangTua) {
        try {
            logger.info("Mengubah username dan email untuk ID OrangTua: {}", id);
            Optional<OrangTua> orangTuaOptional = orangTuaRepository.findById(id);
            if (orangTuaOptional.isEmpty()) {
                logger.error("ID OrangTua tidak ditemukan: {}", id);
                throw new NotFoundException("Id Ortu tidak ditemukan: " + id);
            }
            OrangTua orangTua = orangTuaOptional.get();
            orangTua.setEmail(updateOrangTua.getEmail());
            orangTua.setNama(updateOrangTua.getNama());

            logger.info("Username dan email berhasil diperbarui untuk ID OrangTua: {}", id);
            return orangTuaRepository.save(orangTua);
        } catch (Exception e) {
            logger.error("Gagal mengubah username dan email untuk ID OrangTua: {}", id, e);
            throw e;
        }
    }

//    @Override
//    public Map<String, Boolean> deleteOrangTua(Long id) {
//        try {
//            List<UserModel> users = userRepository.findByIdOrangTua(id);
//            for (UserModel user : users) {
//                user.setOrangTua(null);
//                userRepository.save(user);
//            }
//
//            orangTuaRepository.deleteById(id);
//
//            Map<String, Boolean> res = new HashMap<>();
//            res.put("Deleted", Boolean.TRUE);
//            return res;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Map<String, Boolean> res = new HashMap<>();
//            res.put("Deleted", Boolean.FALSE);
//            return res;
//        }
//    }

//    @Override
//    public void deleteOrangTua(Long id) {
//        orangTuaRepository.deleteById(id);
//    }

    @Override
    public Map<String, Boolean> deleteOrangTua(Long id) {
        try {
            logger.info("Mulai menghapus data OrangTua dengan ID: {}", id);

            List<UserModel> users = userRepository.findByIdOrangTua(id);
            for (UserModel user : users) {
                user.setOrangTua(null);
                userRepository.save(user);
            }

            orangTuaRepository.deleteById(id);
            logger.info("Berhasil menghapus data OrangTua dengan ID: {}", id);

            Map<String, Boolean> res = new HashMap<>();
            res.put("Deleted", Boolean.TRUE);
            return res;
        } catch (Exception e) {
            logger.error("Gagal menghapus data OrangTua dengan ID: {}. Error: {}", id, e.getMessage());
            Map<String, Boolean> res = new HashMap<>();
            res.put("Deleted", Boolean.FALSE);
            return res;
        }
    }

    @Override
    public void DeleteOrtuSementara(Long id) {
        try {
            Optional<OrangTua> orangtuaOptional = orangTuaRepository.findById(id);
            if (orangtuaOptional.isPresent()) {
                OrangTua orangtua = orangtuaOptional.get();
                orangtua.setDeleted(1);
                orangTuaRepository.save(orangtua);
                logger.info("Berhasil menandai OrangTua dengan ID: {} sebagai dihapus sementara", id);
            } else {
                logger.warn("Data OrangTua dengan ID: {} tidak ditemukan untuk penghapusan sementara", id);
            }
        } catch (Exception e) {
            logger.error("Gagal menghapus sementara data OrangTua dengan ID: {}. Error: {}", id, e.getMessage());
        }
    }

    @Override
    public void PemulihanDataOrtu(Long id) {
        try {
            Optional<OrangTua> orangTuaOptional = orangTuaRepository.findById(id);
            if (orangTuaOptional.isPresent()) {
                OrangTua orangTua = orangTuaOptional.get();
                orangTua.setDeleted(0);
                orangTuaRepository.save(orangTua);
                logger.info("Berhasil memulihkan data OrangTua dengan ID: {}", id);
            } else {
                logger.warn("Data OrangTua dengan ID: {} tidak ditemukan untuk pemulihan", id);
            }
        } catch (Exception e) {
            logger.error("Gagal memulihkan data OrangTua dengan ID: {}. Error: {}", id, e.getMessage());
        }
    }
}