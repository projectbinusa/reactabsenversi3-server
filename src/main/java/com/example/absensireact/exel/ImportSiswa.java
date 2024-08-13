package com.example.absensireact.exel;

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

    @Transactional
    public void importUser(Long adminId, MultipartFile file) throws IOException {
        List<User> userList = new ArrayList<>();

        // Read Excel file
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through rows and columns
            Iterator<Row> rowIterator = sheet.iterator();
            int rowNum = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // Skip the title and header rows
                if (rowNum++ < 1) {
                    continue;
                }

                // Log to check row data
                System.out.println("Processing row " + rowNum);

                Cell namaCell = row.getCell(1); // Nama
                Cell emailCell = row.getCell(2); // Email
                Cell passwordCell = row.getCell(3); // Password
                Cell idJabatanCell = row.getCell(4); // idJabatan
                Cell idOrangTuaCell = row.getCell(5); // idOrangTua
                Cell idShiftCell = row.getCell(6); // idShift
                Cell idOrganisasiCell = row.getCell(7); // idOrganisasi

                // Check for missing data
                if (emailCell == null || namaCell == null || passwordCell == null ||
                        idJabatanCell == null || idOrangTuaCell == null || idShiftCell == null || idOrganisasiCell == null) {
                    System.out.println("Skipping row " + rowNum + " due to missing data.");
                    continue;
                }

                // Extract and convert values
                Long idJabatan = getLongCellValue(idJabatanCell);
                Long idOrangTua = getLongCellValue(idOrangTuaCell);
                Long idShift = getLongCellValue(idShiftCell);
                Long idOrganisasi = getLongCellValue(idOrganisasiCell);

                // Validate ID values
                if (idJabatan == null || idOrangTua == null || idShift == null || idOrganisasi == null) {
                    System.out.println("Skipping row " + rowNum + " due to invalid ID values.");
                    continue;
                }

                // Retrieve entities
                Optional<Jabatan> jabatanOptional = jabatanRepository.findById(idJabatan);
                Optional<OrangTua> orangTuaOptional = orangTuaRepository.findById(idOrangTua);
                Optional<Shift> shiftOptional = shiftRepository.findById(idShift);
                Optional<Organisasi> organisasiOptional = organisasiRepository.findById(idOrganisasi);

                if (jabatanOptional.isEmpty() || orangTuaOptional.isEmpty() || shiftOptional.isEmpty() || organisasiOptional.isEmpty()) {
                    System.out.println("One or more related entities not found.");
                    continue;
                }

                // Create and populate user
                Jabatan jabatan = jabatanOptional.get();
                OrangTua orangTua = orangTuaOptional.get();
                Shift shift = shiftOptional.get();
                Organisasi organisasi = organisasiOptional.get();

                User user = new User();
                user.setAdmin(adminRepository.findById(adminId).orElse(null)); // Assuming Admin is optional
                user.setJabatan(jabatan);
                user.setOrangTua(orangTua);
                user.setOrganisasi(organisasi);
                user.setShift(shift);
                user.setEmail(emailCell.getStringCellValue());
                user.setUsername(namaCell.getStringCellValue());
                String encodedPassword = encoder.encode(passwordCell.getStringCellValue());
                user.setPassword(encodedPassword);

                user.setRole("USER");

                userList.add(user);
            }
            workbook.close();
        }

        if (!userList.isEmpty()) {
            siswaRepository.saveAll(userList);
            System.out.println("Saving " + userList.size() + " records to database.");
        } else {
            System.out.println("No records to save.");
        }
    }

    private Long getLongCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing numeric value from string: " + e.getMessage());
                    return null;
                }
            default:
                return null;
        }
    }

}

