package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.*;
import com.example.absensireact.repository.*;
import com.example.absensireact.service.OrganisasiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class OrganisasiImpl implements OrganisasiService {
//    static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/absensireact.appspot.com/o/%s?alt=media";

    private static final String BASE_URL = "https://s3.lynk2.co/api/s3/organisasi";

    private final OrganisasiRepository organisasiRepository;
    private final UserRepository userRepository;
    private final LokasiRepository lokasiRepository;

    private final KelasRepository kelasRepository;
    private final SuperAdminRepository superAdminRepository;
    private final AdminRepository adminRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrganisasiImpl.class);

    public OrganisasiImpl(OrganisasiRepository organisasiRepository, UserRepository userRepository, LokasiRepository lokasiRepository, KelasRepository kelasRepository, SuperAdminRepository superAdminRepository, AdminRepository adminRepository) throws IOException {
        this.organisasiRepository = organisasiRepository;
        this.userRepository = userRepository;
        this.lokasiRepository = lokasiRepository;
        this.kelasRepository = kelasRepository;
        this.superAdminRepository = superAdminRepository;
        this.adminRepository = adminRepository;

    }
    @Override
    public List<Organisasi> getAllOrganisasi(){
        return organisasiRepository.findAll();
    }

    @Override
    public List<Organisasi>getAllByAdmin(Long idAdmin){
        return organisasiRepository.getOrganisasiByIdAdmin(idAdmin);
    }
    @Override
    public Optional<Organisasi>GetOrganisasiById(Long id){
        return organisasiRepository.findById(id);
    }


    @Override
    public List<Organisasi> getAllBySuperAdmin(Long idSuperAdmin) {
        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findById(idSuperAdmin);
        if (superAdminOptional.isPresent()) {
            SuperAdmin superAdmin = superAdminOptional.get();

            List<Admin> adminList = adminRepository.findBySuperAdmin(superAdmin);
            List<Organisasi> organisasiList = new ArrayList<>();

            for (Admin admin : adminList) {
                List<Organisasi> organisasiListByAdmin = organisasiRepository.findByAdmin(admin);
                organisasiList.addAll(organisasiListByAdmin);
            }

            return organisasiList;
        }
        throw new NotFoundException("Id Super admin tidak ditemukan");
    }


    @Override
    public Optional<Organisasi> GetByIdAdmin(Long idAdmin){
        return  organisasiRepository.findOrganisasiByIdAdmin(idAdmin);}
    @Override
    public Optional<Organisasi> GetAllBYId(Long id){
        return organisasiRepository.findById(id);
    }


    @Override
    public Organisasi TambahOrganisasiBySuperAdmin(Long idSuperAdmin, Long idAdmin, Organisasi organisasi) throws IOException {
        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findById(idSuperAdmin);

        if (superAdminOptional.isEmpty()) {
            throw new NotFoundException("id super admin tidak ditemukan : " + idSuperAdmin);
        }

        Optional<Admin> adminOptional = adminRepository.findById(idAdmin);

        if (!adminOptional.isPresent()) {
            throw new NotFoundException("Id Admin tidak ditemukan");
        }
        Admin admin = adminOptional.get();

        Optional<Organisasi> existingOrganisasi = organisasiRepository.findOrganisasiByIdAdmin(idAdmin);
        if (existingOrganisasi.isPresent()) {
            throw new IllegalStateException("Admin sudah memiliki organisasi");
        }

        // Cek apakah nama organisasi sudah ada
        boolean nameExisting = organisasiRepository.existsByNamaOrganisasi(organisasi.getNamaOrganisasi());
        if (nameExisting) {
            throw new IllegalStateException("Organisasi dengan nama : " + organisasi.getNamaOrganisasi() + " sudah terdaftar");
        }

        organisasi.setNamaOrganisasi(organisasi.getNamaOrganisasi());
        organisasi.setAlamat(organisasi.getAlamat());
        organisasi.setKecamatan(organisasi.getKecamatan());
        organisasi.setKabupaten(organisasi.getKabupaten());
        organisasi.setProvinsi(organisasi.getProvinsi());
        organisasi.setNomerTelepon(organisasi.getNomerTelepon());
        organisasi.setEmailOrganisasi(organisasi.getEmailOrganisasi());
        organisasi.setAdmin(admin);

        return organisasiRepository.save(organisasi);
    }

    @Override
    public Organisasi TambahOrganisasi(Long idAdmin, Organisasi organisasi) {
        Optional<Admin> adminOptional = Optional.ofNullable(adminRepository.findById(idAdmin).orElse(null));

        if (!adminOptional.isPresent()) {
            throw new NotFoundException("Id Admin tidak ditemukan");
        }
        Admin admin = adminOptional.get();

        Optional<Organisasi> existingOrganisasi = organisasiRepository.findOrganisasiByIdAdmin(idAdmin);
        if (existingOrganisasi.isPresent()) {
            throw new IllegalStateException("Admin sudah memiliki organisasi");
        }

        // Cek apakah nama organisasi sudah ada
        boolean nameExisting = organisasiRepository.existsByNamaOrganisasi(organisasi.getNamaOrganisasi());
        if (nameExisting) {
            throw new IllegalStateException("Organisasi dengan nama : " + organisasi.getNamaOrganisasi() + " sudah terdaftar");
        }

        organisasi.setAdmin(admin);
        organisasi.setNamaOrganisasi(organisasi.getNamaOrganisasi());
        organisasi.setAlamat(organisasi.getAlamat());
        organisasi.setKecamatan(organisasi.getKecamatan());
        organisasi.setKabupaten(organisasi.getKabupaten());
        organisasi.setProvinsi(organisasi.getProvinsi());
        organisasi.setNomerTelepon(organisasi.getNomerTelepon());
        organisasi.setEmailOrganisasi(organisasi.getEmailOrganisasi());
        return organisasiRepository.save(organisasi);
    }


    @Override
    public void saveOrganisasiImage(Long idAdmin, Long organisasiId, MultipartFile image) throws IOException {
        Optional<Organisasi> organisasiOptional = organisasiRepository.findById(organisasiId);
        if (!organisasiOptional.isPresent()) {
            throw new NotFoundException("Organisasi tidak ditemukan");
        }

        Organisasi organisasi = organisasiOptional.get();
        String file = uploadFoto(image);
        organisasi.setFotoOrganisasi(file);
        organisasiRepository.save(organisasi);
    }

    @Override
    public Organisasi uploadImage(Long id, MultipartFile image) throws IOException {
        Optional<Organisasi> organisasiOptional = organisasiRepository.findById(id);
        if (organisasiOptional.isEmpty()) {
            throw new NotFoundException("Id organisasi tidak ditemukan");
        }
        String fileUrl = uploadFoto(image);
        Organisasi organisasi = organisasiOptional.get();
        organisasi.setFotoOrganisasi(fileUrl);
        return organisasiRepository.save(organisasi);
    }

    private String uploadFoto(MultipartFile multipartFile) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", multipartFile.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, requestEntity, String.class);
        String fileUrl = extractFileUrlFromResponse(response.getBody());
        return fileUrl;
    }

    private String extractFileUrlFromResponse(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(responseBody);
        JsonNode dataNode = jsonResponse.path("data");
        String urlFile = dataNode.path("url_file").asText();

        return urlFile;
    }



//    private String uploadFoto(MultipartFile multipartFile, String fileName) throws IOException {
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        String folderPath = "organisasi/";
//        String fullPath = folderPath + timestamp + "_" + fileName;
//        BlobId blobId = BlobId.of("absensireact.appspot.com", fullPath);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
//        storage.create(blobInfo, multipartFile.getBytes());
//        return String.format(DOWNLOAD_URL, URLEncoder.encode(fullPath, StandardCharsets.UTF_8));
//    }

    private void deleteFoto(String fileName) throws IOException {
        BlobId blobId = BlobId.of("absensireact.appspot.com", fileName);
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("./src/main/resources/FirebaseConfig.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.delete(blobId);
    }

    @Override
    public Organisasi UbahDataOrgannisasi(Long idAdmin, Organisasi organisasi, MultipartFile image) throws IOException {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            logger.info("Memulai proses mengubah data organisasi untuk idAdmin: {}", idAdmin);
            Admin admin = adminRepository.findById(idAdmin).orElse(null);
            if (admin == null) {
                throw new NotFoundException("Id admin tidak ditemukan");
            }

            Optional<Organisasi> existingOrganisasiOptional = organisasiRepository.findOrganisasiByIdAdmin(idAdmin);
            if (!existingOrganisasiOptional.isPresent()) {
                throw new NotFoundException("Organisasi dengan id admin " + idAdmin + " tidak ditemukan");
            }

            Organisasi existingOrganisasi = existingOrganisasiOptional.get();

            if (existingOrganisasi.getFotoOrganisasi() != null && !existingOrganisasi.getFotoOrganisasi().isEmpty()) {
                String currentFotoUrl = existingOrganisasi.getFotoOrganisasi();
                String fileName = currentFotoUrl.substring(currentFotoUrl.indexOf("/o/") + 3, currentFotoUrl.indexOf("?alt=media"));
                deleteFoto(fileName);
            }

            if (image != null && !image.isEmpty()) {
                String imageUrl = uploadFoto(image);
                organisasi.setFotoOrganisasi(imageUrl);
            } else {
                organisasi.setFotoOrganisasi(existingOrganisasi.getFotoOrganisasi());
            }

            boolean nameExisting = organisasiRepository.existsByNamaOrganisasi(organisasi.getNamaOrganisasi());
            if (nameExisting) {
                throw new IllegalStateException("Organisasi dengan nama : " + organisasi.getNamaOrganisasi() + " sudah terdaftar");
            }

            existingOrganisasi.setNamaOrganisasi(organisasi.getNamaOrganisasi());
            existingOrganisasi.setAlamat(organisasi.getAlamat());
            existingOrganisasi.setKecamatan(organisasi.getKecamatan());
            existingOrganisasi.setKabupaten(organisasi.getKabupaten());
            existingOrganisasi.setProvinsi(organisasi.getProvinsi());
            existingOrganisasi.setNomerTelepon(organisasi.getNomerTelepon());
            existingOrganisasi.setEmailOrganisasi(organisasi.getEmailOrganisasi());
            existingOrganisasi.setAdmin(admin);

            logger.info("Berhasil mengubah data organisasi untuk idAdmin: {}", idAdmin);
            return organisasiRepository.save(existingOrganisasi);
        } catch (Exception e) {
            logger.error("Gagal mengubah data organisasi untuk idAdmin: {}", idAdmin, e);
            throw e;
        }
    }

    @Override
    public Organisasi EditByid(Long id, Long idAdmin, Organisasi organisasi, MultipartFile image) throws IOException {
        logger.info("Memulai proses edit organisasi dengan ID: {}", id);
        try {
            Organisasi existingOrganisasi = organisasiRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Organisasi dengan id " + id + " tidak ditemukan"));

            Optional<Admin> adminOptional = adminRepository.findById(idAdmin);
            if (adminOptional.isEmpty()) {
                throw new NotFoundException("Id admin tidak ditemukan");
            }

            if (existingOrganisasi.getFotoOrganisasi() != null && !existingOrganisasi.getFotoOrganisasi().isEmpty()) {
                String currentFotoUrl = existingOrganisasi.getFotoOrganisasi();
                String fileName = currentFotoUrl.substring(currentFotoUrl.indexOf("/o/") + 3, currentFotoUrl.indexOf("?alt=media"));
                deleteFoto(fileName);
                logger.info("Foto lama dihapus: {}", fileName);
            }

            if (image != null && !image.isEmpty()) {
                String imageUrl = uploadFoto(image);
                organisasi.setFotoOrganisasi(imageUrl);
                logger.info("Foto baru diunggah: {}", imageUrl);
            } else {
                organisasi.setFotoOrganisasi(existingOrganisasi.getFotoOrganisasi());
            }

            boolean nameExisting = organisasiRepository.existsByNamaOrganisasi(organisasi.getNamaOrganisasi());
            if (nameExisting) {
                throw new IllegalStateException("Organisasi dengan nama : " + organisasi.getNamaOrganisasi() + " sudah terdaftar");
            }

            existingOrganisasi.setNamaOrganisasi(organisasi.getNamaOrganisasi());
            existingOrganisasi.setAlamat(organisasi.getAlamat());
            existingOrganisasi.setKecamatan(organisasi.getKecamatan());
            existingOrganisasi.setKabupaten(organisasi.getKabupaten());
            existingOrganisasi.setProvinsi(organisasi.getProvinsi());
            existingOrganisasi.setNomerTelepon(organisasi.getNomerTelepon());
            existingOrganisasi.setEmailOrganisasi(organisasi.getEmailOrganisasi());
            existingOrganisasi.setAdmin(adminOptional.get());

            Organisasi updatedOrganisasi = organisasiRepository.save(existingOrganisasi);
            logger.info("Organisasi dengan ID {} berhasil diperbarui", id);
            return updatedOrganisasi;
        } catch (Exception e) {
            logger.error("Gagal memperbarui organisasi dengan ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void DeleteOrganisasiSementara(Long id) {
        logger.info("Memulai proses delete sementara untuk organisasi dengan ID: {}", id);
        try {
            Optional<Organisasi> shiftOptional = organisasiRepository.findById(id);
            if (shiftOptional.isPresent()) {
                Organisasi shift = shiftOptional.get();
                shift.setDeleted(1);
                organisasiRepository.save(shift);
                logger.info("Organisasi dengan ID {} berhasil ditandai sebagai dihapus sementara", id);
            } else {
                logger.warn("Organisasi dengan ID {} tidak ditemukan", id);
            }
        } catch (Exception e) {
            logger.error("Gagal menghapus sementara organisasi dengan ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Map<String, Boolean> deleteOrganisasi(Long id) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            logger.info("Memulai proses penghapusan organisasi dengan id: {}", id);

            // Cari semua UserModel yang menggunakan id organisasi ini
            List<UserModel> users = userRepository.findByOrganisasiId(id);
            for (UserModel user : users) {
                user.setOrganisasi(null);
                userRepository.save(user);
            }
            logger.info("Berhasil menghapus referensi organisasi dari user.");

            List<Lokasi> lokasis = lokasiRepository.findByOrganisasiId(id);
            for (Lokasi lokasi : lokasis) {
                lokasi.setOrganisasi(null);
                lokasiRepository.save(lokasi);
            }
            logger.info("Berhasil menghapus referensi organisasi dari lokasi.");

            List<Kelas> kelass = kelasRepository.findAllByOrganisasi(id);
            for (Kelas kelas : kelass) {
                kelas.setOrganisasi(null);
                kelasRepository.save(kelas);
            }
            logger.info("Berhasil menghapus referensi organisasi dari kelas.");

            // Hapus foto organisasi jika ada
            Optional<Organisasi> organisasiOptional = organisasiRepository.findById(id);
            if (organisasiOptional.isPresent()) {
                Organisasi organisasi = organisasiOptional.get();
                if (organisasi.getFotoOrganisasi() != null) {
                    String fotoUrl = organisasi.getFotoOrganisasi();
                    int startIdx = fotoUrl.indexOf("/o/") + 3;
                    int endIdx = fotoUrl.indexOf("?alt=media");

                    if (endIdx != -1) {
                        String fileName = fotoUrl.substring(startIdx, endIdx);
                        deleteFoto(fileName);
                        logger.info("Berhasil menghapus foto organisasi dengan nama file: {}", fileName);
                    } else {
                        logger.warn("Format foto URL tidak sesuai: {}", fotoUrl);
                    }
                }

                // Hapus organisasi setelah semua referensi dihapus
                organisasiRepository.deleteById(id);
                logger.info("Organisasi dengan id {} berhasil dihapus.", id);
            } else {
                logger.error("Organisasi tidak ditemukan dengan id: {}", id);
                throw new NotFoundException("Organisasi not found with id: " + id);
            }

            // Return success
            Map<String, Boolean> res = new HashMap<>();
            res.put("Deleted", Boolean.TRUE);
            return res;
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat menghapus organisasi dengan id {}: {}", id, e.getMessage(), e);
            Map<String, Boolean> res = new HashMap<>();
            res.put("Deleted", Boolean.FALSE);
            return res;
        }
    }


//    @Override
//    public Map<String, Boolean> delete(Long id) {
//        try {
//            List<UserModel> users = userRepository.findByIdShift(id);
//            for (UserModel user : users) {
//                user.setShift(null);
//                userRepository.save(user);
//            }
//
//            shiftRepository.deleteById(id);
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

}
