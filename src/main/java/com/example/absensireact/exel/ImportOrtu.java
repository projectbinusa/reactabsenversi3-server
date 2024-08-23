package com.example.absensireact.exel;

import com.example.absensireact.model.Admin;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.repository.OrangTuaRepository;
import com.example.absensireact.repository.SuperAdminRepository;
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
public class ImportOrtu {

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Transactional
    public List<String> importOrangTua(Long adminId, MultipartFile file) throws IOException {
        List<OrangTua> orangTuaList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            int rowNum = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (rowNum++ < 2) {
                    continue;
                }

                String nama = getCellStringValue(row.getCell(1));
                String email = getCellStringValue(row.getCell(2));
                String password = getCellStringValue(row.getCell(3));

                if (email == null || nama == null || password == null) {
                    errorMessages.add("Row " + rowNum + ": Missing data in one or more required columns.");
                    continue;
                }

                // Check if the email already exists in any of the repositories
                boolean emailExistsInAdmin = adminRepository.existsByEmail(email);
                boolean emailExistsInSuperAdmin = superAdminRepository.existsByEmail(email);
                boolean emailExistsInUser = userRepository.existsByEmail(email);
                boolean emailExistsInOrangTua = orangTuaRepository.existsByEmail(email);

                if (emailExistsInAdmin || emailExistsInSuperAdmin || emailExistsInUser || emailExistsInOrangTua) {
                    errorMessages.add("Row " + rowNum + ": Email " + email + " already exists in the system.");
                    continue;
                }

                Optional<Admin> adminOptional = adminRepository.findById(adminId);
                if (adminOptional.isEmpty()) {
                    errorMessages.add("Row " + rowNum + ": Admin with ID " + adminId + " not found.");
                    continue;
                }

                Admin admin = adminOptional.get();
                OrangTua orangTua = new OrangTua();
                orangTua.setAdmin(admin);
                orangTua.setEmail(email);
                orangTua.setNama(nama);
                orangTua.setPassword(encoder.encode(password));
                orangTua.setRole("Wali Murid");

                orangTuaList.add(orangTua);
            }
            workbook.close();
        }

        if (!orangTuaList.isEmpty()) {
            orangTuaRepository.saveAll(orangTuaList);
        }

        return errorMessages;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}

