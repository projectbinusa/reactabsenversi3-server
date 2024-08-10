package com.example.absensireact.excel;

import com.example.absensireact.model.OrangTua;
import com.example.absensireact.repository.OrangTuaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ImportOrtu {

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    public void importOrangTua(MultipartFile file) throws IOException {
        List<OrangTua> orangTuaList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header row
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                OrangTua orangTua = new OrangTua();

                Iterator<Cell> cellsInRow = currentRow.iterator();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0: // Email
                            orangTua.setEmail(currentCell.getStringCellValue());
                            break;

                        case 1: // Nama
                            orangTua.setNama(currentCell.getStringCellValue());
                            break;

                        // Add more cases if you have more columns
                        default:
                            break;
                    }
                    cellIdx++;
                }

                orangTuaList.add(orangTua);
            }

            // Save to database
            orangTuaRepository.saveAll(orangTuaList);
        }
    }
}
