package com.example.absensireact.exel;

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
    PasswordEncoder encoder;

//    @Transactional
    public void importUser(MultipartFile file, Admin admin) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<User> UserList = new ArrayList<>();
        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                User user = new User();

                Cell namaUserCell = row.getCell(1);
                Cell emailCell = row.getCell(2);
                Cell passwordCell = row.getCell(3);
                Cell jabatanCell = row.getCell(4);
                Cell orangTuaCell = row.getCell(5);
                Cell shiftCell = row.getCell(6);
                Cell organisasiCell = row.getCell(7);

//                if (namaUserCell != null) {
//                    user.setUsername(getCellValue(namaUserCell));
//                }

                if (organisasiCell != null || jabatanCell != null || orangTuaCell != null || shiftCell != null || namaUserCell != null || emailCell != null) {
                    String namaOrganisasi = getCellValue(organisasiCell);
                    String namaShift = getCellValue(shiftCell);
                    String namaJabatan = getCellValue(jabatanCell);
                    String namaOrangtua = getCellValue(orangTuaCell);
                    Organisasi organisasi = organisasiRepository.findByNamaOrganisasi(namaOrganisasi)
                            .orElseThrow(() -> new NotFoundException("Organisasi dengan nama " + namaOrganisasi + " tidak ditemukan"));
                    Shift shift = shiftRepository.findByShift(namaShift)
                            .orElseThrow(() -> new NotFoundException("waktu pembelajaran dengan nama " + namaShift + " tidak ditemukan"));
                    Jabatan jabatan = jabatanRepository.findByNamaStatus(namaJabatan)
                            .orElseThrow(() -> new NotFoundException("status dengan nama " + namaJabatan + " tidak ditemukan"));
                    OrangTua orangTua = orangTuaRepository.findByWaliMurid(namaOrangtua)
                            .orElseThrow(() -> new NotFoundException("wali murid dengan nama " + namaOrangtua + " tidak ditemukan"));
                    user.setOrganisasi(organisasi);
                    user.setJabatan(jabatan);
                    user.setShift(shift);
                    user.setOrangTua(orangTua);
                    user.setUsername(getCellValue(namaUserCell));
                    user.setEmail(getCellValue(emailCell));
                    String encodedPassword = encoder.encode(passwordCell.getStringCellValue());
                    user.setPassword(encodedPassword);
                    System.out.println("ID Organisasi, jabatan, shift, orangtua yang di-set: " + organisasi.getId());

                } else {
                    System.out.println("tidak ada");
                }

//                user.setUsername(namaUserCell);
                user.setAdmin(admin);
                UserList.add(user);
            }
        }

        siswaRepository.saveAll(UserList);
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

