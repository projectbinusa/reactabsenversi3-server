package com.example.absensireact.exel;

import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.*;
import com.example.absensireact.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Component
public class ImportSiswa {

    @Autowired
    private UserRepository siswaRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JabatanRepository jabatanRepository;

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private OrganisasiRepository organisasiRepository;

    @Autowired
    private KelasRepository kelasRepository;

    @Autowired
    PasswordEncoder encoder;

//    @Transactional
public void importUser(MultipartFile file, Admin admin) throws IOException {
    Workbook workbook = new XSSFWorkbook(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(0);

    List<UserModel> userList = new ArrayList<>();
    for (int i = 3; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row != null) {
            Cell firstCell = row.getCell(0);
            if (firstCell != null && "No".equalsIgnoreCase(getCellValue(firstCell))) {
                continue;
            }

            UserModel user = new UserModel();

            Cell namaUserCell = row.getCell(1);
            Cell emailCell = row.getCell(2);
            Cell passwordCell = row.getCell(3);
            Cell orangTuaCell = row.getCell(4);
            Cell shiftCell = row.getCell(5);
            Cell organisasiCell = row.getCell(6);
            Cell kelasCell = row.getCell(7);

            if (kelasCell != null || organisasiCell != null || orangTuaCell != null || shiftCell != null || namaUserCell != null || emailCell != null) {
                String namaOrganisasi = getCellValue(organisasiCell);
                String namaKelas = getCellValue(kelasCell);
                String namaShift = getCellValue(shiftCell);
                String namaOrangtua = getCellValue(orangTuaCell);

                Organisasi organisasi = organisasiRepository.findByNamaOrganisasi(namaOrganisasi)
                        .orElseThrow(() -> new NotFoundException("Organisasi dengan nama " + namaOrganisasi + " tidak ditemukan"));
                Kelas kelas = kelasRepository.findByNamaKelas(namaKelas)
                        .orElseThrow(() -> new NotFoundException("Kelas dengan nama " + namaKelas + " tidak ditemukan"));
                Shift shift = shiftRepository.findByShift(namaShift)
                        .orElseThrow(() -> new NotFoundException("Shift dengan nama " + namaShift + " tidak ditemukan"));
                OrangTua orangTua = orangTuaRepository.findByWaliMurid(namaOrangtua)
                        .orElseThrow(() -> new NotFoundException("Orang Tua dengan nama " + namaOrangtua + " tidak ditemukan"));

                user.setOrganisasi(organisasi);
                user.setKelas(kelas);
                user.setShift(shift);
                user.setOrangTua(orangTua);
                user.setUsername(getCellValue(namaUserCell));
                user.setEmail(getCellValue(emailCell));

                // Cek apakah email atau username sudah terdaftar
                if (siswaRepository.existsByEmail(user.getEmail())) {
                    workbook.close();
                    throw new BadRequestException("Email " + user.getEmail() + " telah digunakan");
                }
                if (siswaRepository.existsByUsername(user.getUsername())) {
                    workbook.close();
                    throw new BadRequestException("Username " + user.getUsername() + " telah digunakan");
                }

                String encodedPassword = encoder.encode(passwordCell.getStringCellValue());
                user.setPassword(encodedPassword);
            }

            user.setAdmin(admin);
            user.setRole("Siswa"); // Set status otomatis menjadi "Siswa"
            userList.add(user);
        }
    }

    siswaRepository.saveAll(userList);
    workbook.close();
}

//    @Transactional
public void importUserperKelas(MultipartFile file, Admin admin, Kelas kelas) throws IOException {
    Workbook workbook = new XSSFWorkbook(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(0);

    List<UserModel> userList = new ArrayList<>();
    for (int i = 3; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row != null) {
            UserModel user = new UserModel();

            Cell namaUserCell = row.getCell(1);
            Cell emailCell = row.getCell(2);
            Cell passwordCell = row.getCell(3);
            Cell orangTuaCell = row.getCell(4);
            Cell shiftCell = row.getCell(5);
            Cell organisasiCell = row.getCell(6);

            if (organisasiCell != null || orangTuaCell != null || shiftCell != null || namaUserCell != null || emailCell != null) {
                String namaOrganisasi = getCellValue(organisasiCell);
                String namaShift = getCellValue(shiftCell);
                String namaOrangtua = getCellValue(orangTuaCell);

                Organisasi organisasi = organisasiRepository.findByNamaOrganisasi(namaOrganisasi)
                        .orElseThrow(() -> new NotFoundException("Organisasi dengan nama " + namaOrganisasi + " tidak ditemukan"));
                Shift shift = shiftRepository.findByShift(namaShift)
                        .orElseThrow(() -> new NotFoundException("Shift dengan nama " + namaShift + " tidak ditemukan"));
                OrangTua orangTua = orangTuaRepository.findByWaliMurid(namaOrangtua)
                        .orElseThrow(() -> new NotFoundException("Orang Tua dengan nama " + namaOrangtua + " tidak ditemukan"));

                user.setOrganisasi(organisasi);
                user.setShift(shift);
                user.setOrangTua(orangTua);
                user.setUsername(getCellValue(namaUserCell));
                user.setEmail(getCellValue(emailCell));

                // Cek apakah email atau username sudah terdaftar
                if (siswaRepository.existsByEmail(user.getEmail())) {
                    workbook.close();
                    throw new BadRequestException("Email " + user.getEmail() + " telah digunakan");
                }
                if (siswaRepository.existsByUsername(user.getUsername())) {
                    workbook.close();
                    throw new BadRequestException("Username " + user.getUsername() + " telah digunakan");
                }

                String encodedPassword = encoder.encode(passwordCell.getStringCellValue());
                user.setPassword(encodedPassword);
            }

            user.setAdmin(admin);
            user.setKelas(kelas);
            user.setRole("Siswa");
            userList.add(user);
        }
    }

    siswaRepository.saveAll(userList);
    workbook.close();
}



    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int)cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

//    private Long getLongCellValue(Cell cell) {
//        if (cell == null) {
//            return null;
//        }
//        switch (cell.getCellType()) {
//            case NUMERIC:
//                return (long) cell.getNumericCellValue();
//            case STRING:
//                try {
//                    return Long.parseLong(cell.getStringCellValue());
//                } catch (NumberFormatException e) {
//                    System.out.println("Error parsing numeric value from string: " + e.getMessage());
//                    return null;
//                }
//            default:
//                return null;
//        }
//    }

}

