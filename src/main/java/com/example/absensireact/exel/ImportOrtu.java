package com.example.absensireact.exel;

import com.example.absensireact.model.Admin;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.OrangTuaRepository;
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
    PasswordEncoder encoder;

    @Transactional
    public void importOrangTua(Long adminId, MultipartFile file) throws IOException {
        List<OrangTua> orangTuaList = new ArrayList<>();

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

                Cell namaCell = row.getCell(1);
                Cell emailCell = row.getCell(2);
                Cell passwordCell = row.getCell(3);

                if (emailCell == null || namaCell == null || passwordCell == null) {
                    System.out.println("Skipping row " + rowNum + " due to missing data.");
                    continue;
                }

                Optional<Admin> adminOptional = adminRepository.findById(adminId);
                if (adminOptional.isEmpty()) {
                    System.out.println("Admin with id " + adminId + " not found.");
                    continue;
                }

                Admin admin = adminOptional.get();
                OrangTua orangTua = new OrangTua();
                orangTua.setAdmin(admin);
                orangTua.setEmail(emailCell.getStringCellValue());
                orangTua.setNama(namaCell.getStringCellValue());
                String encodedPassword = encoder.encode(passwordCell.getStringCellValue());
                orangTua.setPassword(encodedPassword);

                orangTua.setRole("Wali Murid");

                orangTuaList.add(orangTua);
            }
            workbook.close();
        }

        if (!orangTuaList.isEmpty()) {
            orangTuaRepository.saveAll(orangTuaList);
            System.out.println("Saving " + orangTuaList.size() + " records to database.");
        } else {
            System.out.println("No records to save.");
        }
    }

}

