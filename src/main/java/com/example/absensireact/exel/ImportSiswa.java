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

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public void importUser(MultipartFile file, Admin admin) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<UserModel> userList = new ArrayList<>();
        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            // Jika baris kosong, lanjutkan ke baris berikutnya
            if (row == null || isRowEmpty(row)) {
                System.out.println("Baris kosong terdeteksi di baris: " + (i + 1));
                continue;
            }

            // Abaikan baris header berdasarkan nilai sel di kolom "Nama Siswa"
            Cell headerCell = row.getCell(1);  // Ambil sel di kolom "Nama Siswa"
            if (headerCell != null && "Nama Siswa".equalsIgnoreCase(getCellValue(headerCell))) {
                System.out.println("Header terdeteksi di baris: " + (i + 1) + ", baris diabaikan.");
                continue;
            }

            // Ambil nilai dari sel-sel yang dibutuhkan
            Cell namaUserCell = row.getCell(1);
            Cell emailCell = row.getCell(2);
            Cell passwordCell = row.getCell(3);
            Cell orangTuaCell = row.getCell(4);
            Cell shiftCell = row.getCell(5);
            Cell organisasiCell = row.getCell(6);
//            Cell kelasCell = row.getCell(7);

            // Cek jika ada sel penting yang kosong
            if (namaUserCell == null || emailCell == null || passwordCell == null ||
                    orangTuaCell == null || shiftCell == null || organisasiCell == null) {
                System.out.println("Data kosong terdeteksi di baris: " + (i + 1));
                continue; // Lewati baris ini dan lanjutkan ke baris berikutnya
            }

            // Proses data user jika valid
            UserModel user = new UserModel();
            user.setUsername(getCellValue(namaUserCell));
            user.setEmail(getCellValue(emailCell));
            user.setPassword(encoder.encode(passwordCell.getStringCellValue()));

            // Set organisasi, kelas, shift, dan orang tua berdasarkan data yang valid
            String namaOrganisasi = getCellValue(organisasiCell);
//            String namaKelas = getCellValue(kelasCell);
            String namaShift = getCellValue(shiftCell);
            String namaOrangTua = getCellValue(orangTuaCell);

            try {
                Organisasi organisasi = organisasiRepository.findByNamaOrganisasi(namaOrganisasi)
                        .orElseThrow(() -> new NotFoundException("Organisasi dengan nama " + namaOrganisasi + " tidak ditemukan"));
//                Kelas kelas = kelasRepository.findByNamaKelas(namaKelas)
//                        .orElseThrow(() -> new NotFoundException("Kelas dengan nama " + namaKelas + " tidak ditemukan"));
                Shift shift = shiftRepository.findByShift(namaShift)
                        .orElseThrow(() -> new NotFoundException("Shift dengan nama " + namaShift + " tidak ditemukan"));
                OrangTua orangTua = orangTuaRepository.findByWaliMurid(namaOrangTua)
                        .orElseThrow(() -> new NotFoundException("Orang Tua dengan nama " + namaOrangTua + " tidak ditemukan"));

                user.setOrganisasi(organisasi);
//                user.setKelas(kelas);
                user.setShift(shift);
                user.setOrangTua(orangTua);
                user.setAdmin(admin);
                user.setRole("USER"); // Set status otomatis menjadi "Siswa"
                userList.add(user);

                System.out.println("Data user berhasil ditambahkan dari baris " + (i + 1));
            } catch (NotFoundException e) {
                System.out.println("Error pada baris " + (i + 1) + ": " + e.getMessage());
                continue; // Jika ada kesalahan pada entitas yang dicari, lewati baris ini
            }
        }
        workbook.close();

        // Simpan semua user yang valid ke dalam database setelah semua data selesai diproses
        if (!userList.isEmpty()) {
            siswaRepository.saveAll(userList);
            System.out.println("Semua data user berhasil disimpan ke database.");
        } else {
            System.out.println("Tidak ada data user yang valid untuk disimpan.");
        }
    }

    //    @Transactional
    public void importUserperKelas(MultipartFile file, Admin admin, Kelas kelas) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<UserModel> userList = new ArrayList<>();
        for (int i = 5; i <= sheet.getLastRowNum(); i++) { // Mulai dari baris ke-5 untuk abaikan header
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) {
                System.out.println("Baris kosong terdeteksi di baris: " + (i + 1));
                continue; // Lewati baris kosong
            }

            Cell namaUserCell = row.getCell(1);
            Cell emailCell = row.getCell(2);
            Cell passwordCell = row.getCell(3);
            Cell orangTuaCell = row.getCell(4);
            Cell shiftCell = row.getCell(5);
            Cell organisasiCell = row.getCell(6);

            // Abaikan jika ini adalah baris header
            if ("Nama Siswa".equalsIgnoreCase(getCellValue(namaUserCell))) {
                System.out.println("Header terdeteksi di baris: " + (i + 1) + ", baris diabaikan.");
                continue;
            }

            // Cek jika sel penting kosong
            if (namaUserCell == null || emailCell == null || passwordCell == null || orangTuaCell == null || shiftCell == null || organisasiCell == null) {
                System.out.println("Data kosong terdeteksi di baris: " + (i + 1));
                continue;
            }

            // Proses data user jika valid
            UserModel user = new UserModel();
            user.setUsername(getCellValue(namaUserCell));
            user.setEmail(getCellValue(emailCell));
            user.setPassword(encoder.encode(passwordCell.getStringCellValue()));

            try {
                String namaOrganisasi = getCellValue(organisasiCell);
                String namaShift = getCellValue(shiftCell);
                String namaOrangTua = getCellValue(orangTuaCell);

                Organisasi organisasi = organisasiRepository.findByNamaOrganisasi(namaOrganisasi)
                        .orElseThrow(() -> new NotFoundException("Organisasi dengan nama " + namaOrganisasi + " tidak ditemukan"));
                Shift shift = shiftRepository.findByShift(namaShift)
                        .orElseThrow(() -> new NotFoundException("Shift dengan nama " + namaShift + " tidak ditemukan"));
                OrangTua orangTua = orangTuaRepository.findByWaliMurid(namaOrangTua)
                        .orElseThrow(() -> new NotFoundException("Orang Tua dengan nama " + namaOrangTua + " tidak ditemukan"));

                user.setOrganisasi(organisasi);
                user.setShift(shift);
                user.setOrangTua(orangTua);

                // Cek apakah email atau username sudah terdaftar
                if (siswaRepository.existsByEmail(user.getEmail())) {
                    System.out.println("Email " + user.getEmail() + " telah digunakan, baris " + (i + 1) + " diabaikan.");
                    continue;
                }
                if (siswaRepository.existsByUsername(user.getUsername())) {
                    System.out.println("Username " + user.getUsername() + " telah digunakan, baris " + (i + 1) + " diabaikan.");
                    continue;
                }

                user.setAdmin(admin);
                user.setKelas(kelas);
                user.setRole("Siswa");
                userList.add(user);
                System.out.println("Data user berhasil ditambahkan dari baris " + (i + 1));

            } catch (NotFoundException e) {
                System.out.println("Error pada baris " + (i + 1) + ": " + e.getMessage());
                continue;
            }
        }

        if (!userList.isEmpty()) {
            siswaRepository.saveAll(userList);
            System.out.println("Semua data user berhasil disimpan ke database.");
        } else {
            System.out.println("Tidak ada data user yang valid untuk disimpan.");
        }

        workbook.close();
    }



    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
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

