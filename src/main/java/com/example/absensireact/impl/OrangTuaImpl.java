package com.example.absensireact.impl;

import com.example.absensireact.dto.PasswordDTO;
import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.*;
import com.example.absensireact.repository.OrangTuaRepository;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.service.OrangTuaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OrangTuaImpl implements OrangTuaService {

    private static final String BASE_URL = "https://s3.lynk2.co/api/s3/superadmin";

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public List<OrangTua>getAllOrangTua(){
        return orangTuaRepository.findAll();
    }

    @Override
    public Optional<OrangTua> getOrangTuaById(Long id){
        return orangTuaRepository.findById(id);
    }

    @Override
    public List<OrangTua>getAllBySuperAdmin(Long idSuperAdmin){
        return orangTuaRepository.findAllBySuperAdmin(idSuperAdmin);
    }

    @Override
    public OrangTua tambahOrangTua(Long idSuperAdmin, OrangTua orangTua) {
        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findById(idSuperAdmin);
        if (superAdminOptional.isPresent()) {
            SuperAdmin superAdmin = superAdminOptional.get();
            orangTua.setSuperAdmin(superAdmin);
            orangTua.setEmail(orangTua.getEmail());
            orangTua.setNama(orangTua.getNama());
            orangTua.setImageOrtu(orangTua.getImageOrtu());
            orangTua.setRole("Wali Murid");
            orangTua.setPassword(encoder.encode(orangTua.getPassword()));
            return orangTuaRepository.save(orangTua);
        }
        throw new NotFoundException("SuperAdmin tidak ditemukan");
    }

    @Override
    public OrangTua editOrangTuaById(Long id, Long idSuperAdmin, OrangTua updateOrangTua) {
        OrangTua orangTua = orangTuaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id ortu tidak ditemukan : " + id));

        SuperAdmin superAdmin = superAdminRepository.findById(idSuperAdmin)
                .orElseThrow(() -> new NotFoundException("SuperAdmin dengan id: " + idSuperAdmin + " tidak ditemukan"));

        orangTua.setNama(updateOrangTua.getNama());
        orangTua.setEmail(updateOrangTua.getEmail());
        orangTua.setImageOrtu(updateOrangTua.getImageOrtu());
        orangTua.setPassword(encoder.encode(updateOrangTua.getPassword()));
        orangTua.setSuperAdmin(superAdmin);

        return orangTuaRepository.save(orangTua);
    }

    @Override
    public OrangTua uploadImage(Long id, MultipartFile image) throws IOException {
        Optional<OrangTua> orangTuaOptional = orangTuaRepository.findById(id);
        if (orangTuaOptional.isEmpty()) {
            throw new NotFoundException("Id ortu tidak ditemukan");
        }
        String fileUrl = uploadFoto(image);
        OrangTua orangTua = orangTuaOptional.get();
        orangTua.setImageOrtu(fileUrl);
        return orangTuaRepository.save(orangTua);
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

    @Override
    public OrangTua putPasswordOrangTua(PasswordDTO passwordDTO, Long id) {
        OrangTua update = orangTuaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id Not Found"));

        boolean isOldPasswordCorrect = encoder.matches(passwordDTO.getOld_password(), update.getPassword());

        if (!isOldPasswordCorrect) {
            throw new NotFoundException("Password lama tidak sesuai");
        }

        if (passwordDTO.getNew_password().equals(passwordDTO.getConfirm_new_password())) {
            update.setPassword(encoder.encode(passwordDTO.getNew_password()));
            return orangTuaRepository.save(update);
        } else {
            throw new BadRequestException("Password tidak sesuai");
        }
    }


    private String extractFileUrlFromResponse(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(responseBody);
        JsonNode dataNode = jsonResponse.path("data");
        String urlFile = dataNode.path("url_file").asText();

        return urlFile;
    }

    @Override
    public OrangTua ubahUsernamedanemail(Long id, OrangTua updateOrangTua){
        Optional<OrangTua> orangTuaOptional = orangTuaRepository.findById(id);
        if (orangTuaOptional.isEmpty()) {
            throw new NotFoundException("Id Ortu tidak ditemukan :" + id);
        }
        OrangTua orangTua = orangTuaOptional.get();
        orangTua.setEmail(updateOrangTua.getEmail());
        orangTua.setNama(updateOrangTua.getNama());


        return orangTuaRepository.save(orangTua);
    }

    @Override
    public void deleteOrangTua(Long id) {
        orangTuaRepository.deleteById(id);
    }

}