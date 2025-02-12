package com.example.absensireact.impl;

import com.example.absensireact.exception.BadRequestException;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.*;
import com.example.absensireact.repository.*;
import com.example.absensireact.service.AbsensiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
//import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


@Service
public class AbsensiImpl implements AbsensiService {
//    private final AbsensiService absensiService;
    static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/absensireact.appspot.com/o/%s?alt=media";

    private static final String BASE_URL = "https://s3.lynk2.co/api/s3";

    private static final Logger logger = LoggerFactory.getLogger(AbsensiImpl.class);

    private final AbsensiRepository absensiRepository;

    private final UserRepository userRepository;

    private final AdminRepository adminRepository;
    private final ShiftRepository shiftRepository;

    private final KelasRepository kelasRepository;

//    private final AbsensiService absensiService;

    private final OrangTuaRepository orangTuaRepository;



    public AbsensiImpl(AbsensiRepository absensiRepository, UserRepository userRepository, AdminRepository adminRepository, ShiftRepository shiftRepository, KelasRepository kelasRepository, OrangTuaRepository orangTuaRepository) throws IOException {
        this.absensiRepository = absensiRepository;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.shiftRepository = shiftRepository;
        this.kelasRepository = kelasRepository;
        this.orangTuaRepository = orangTuaRepository;
    }


    @Override
    public List<Absensi> getAllByAdmin(Long adminId) {
        try {
            logger.info("Fetching absensi for adminId: {}", adminId);

            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new NotFoundException("Id Admin tidak ditemukan dengan id: " + adminId));

            List<UserModel> users = userRepository.findByadminIdAbsensi(admin.getId());

            if (users.isEmpty()) {
                throw new NotFoundException("Tidak ada pengguna yang terkait dengan admin dengan id: " + adminId);
            }

            List<Absensi> absensiList = new ArrayList<>();
            for (UserModel user : users) {
                absensiList.addAll(absensiRepository.findByUser(user));
            }

            logger.info("Total absensi ditemukan: {}", absensiList.size());
            return absensiList;

        } catch (Exception e) {
            logger.error("Error fetching absensi for adminId {}: {}", adminId, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Absensi> getAllAbsensi(){
        return  absensiRepository.findAll();
    }

    public List<Absensi> getAbsensiByTanggal(Date tanggalAbsen) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tanggalAbsen);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            logger.info("Fetching absensi for day: {}, month: {}, year: {}", day, month, year);

            List<Absensi> absensiList = absensiRepository.findByTanggalAbsen(day, month, year);

            logger.info("Number of records found: {}", absensiList.size());
            return absensiList;

        } catch (Exception e) {
            logger.error("Error fetching absensi by date: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Absensi> getAbsensiByBulan(Date tanggalAbsen) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tanggalAbsen);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        logger.info("Fetching absensi for month: " + month + ", year: " + year);

        List<Absensi> absensiList = absensiRepository.findByMonthAndYear(month, year);

        logger.info("Number of records found: " + absensiList.size());

        return absensiList;
    }
    @Override
    public List<Absensi> getAbsensiByBulanSimpel(int month, Long idAdmin) {
        try {
            logger.info("Fetching absensi for month: {} and admin ID: {}", month, idAdmin);

            Admin admin = adminRepository.findById(idAdmin)
                    .orElseThrow(() -> new NotFoundException("ID admin tidak ditemukan: " + idAdmin));

            List<Absensi> absensiList = absensiRepository.findByMonth(month);
            logger.info("Number of records found: {}", absensiList.size());

            return absensiList;
        } catch (Exception e) {
            logger.error("Error fetching absensi for month {} and admin ID {}: {}", month, idAdmin, e.getMessage());
            throw e;
        }
    }

    @Override
    public Map<String, List<Absensi>> getAbsensiByMingguan(Date tanggalAwal, Date tanggalAkhir) {
        try {
            logger.info("Fetching weekly absensi from {} to {}", tanggalAwal, tanggalAkhir);

            List<Absensi> absensiList = absensiRepository.findByMingguan(tanggalAwal, tanggalAkhir);
            Map<String, List<Absensi>> weeklyAbsensiMap = new HashMap<>();

            for (Absensi absensi : absensiList) {
                String weekRange = getWeekRange(absensi.getTanggalAbsen());
                weeklyAbsensiMap.computeIfAbsent(weekRange, k -> new ArrayList<>()).add(absensi);
            }

            logger.info("Weekly absensi fetched successfully with {} records", absensiList.size());
            return weeklyAbsensiMap;
        } catch (Exception e) {
            logger.error("Error fetching weekly absensi: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Map<String, List<Absensi>> getAbsensiByMingguanPerKelas(Date tanggalAwal, Date tanggalAkhir, Long kelasId) {
        try {
            logger.info("Fetching weekly absensi from {} to {} for class ID: {}", tanggalAwal, tanggalAkhir, kelasId);

            List<Absensi> absensiList = absensiRepository.findByMingguanAndKelas(tanggalAwal, tanggalAkhir, kelasId);
            Map<String, List<Absensi>> weeklyAbsensiMap = new HashMap<>();

            for (Absensi absensi : absensiList) {
                String weekRange = getWeekRange(absensi.getTanggalAbsen());
                weeklyAbsensiMap.computeIfAbsent(weekRange, k -> new ArrayList<>()).add(absensi);
            }

            logger.info("Weekly absensi for class {} fetched successfully with {} records", kelasId, absensiList.size());
            return weeklyAbsensiMap;
        } catch (Exception e) {
            logger.error("Error fetching weekly absensi for class ID {}: {}", kelasId, e.getMessage());
            throw e;
        }
    }

    @Override
    public Absensi PostAbsensi(String email, Absensi absensi) throws IOException, ParseException {
        try {
            logger.info("Processing absensi for email: {}", email);

            List<Absensi> existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));
            if (!existingAbsensi.isEmpty()) {
                logger.warn("User {} sudah melakukan absensi masuk pada hari yang sama sebelumnya.", email);
                return null;
            }

            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User dengan email: " + email + " tidak ditemukan."));
            Shift shift = shiftRepository.findById(user.getShift().getId())
                    .orElseThrow(() -> new NotFoundException("ID shift tidak ditemukan"));

            Date tanggalHariIni = truncateTime(new Date());
            Date masuk = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String jamMasukString = formatter.format(masuk);

            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date waktuMasukShift = timeFormatter.parse(shift.getWaktuMasuk());

            String keterangan = (masuk.before(waktuMasukShift)) ? "Lebih Awal" : "Terlambat";

            absensi.setUserEmail(email);
            absensi.setUser(user);
            absensi.setTanggalAbsen(tanggalHariIni);
            absensi.setJamMasuk(jamMasukString);
            absensi.setJamPulang("-");
            absensi.setLokasiMasuk(absensi.getLokasiMasuk());
            absensi.setLokasiPulang("-");
            absensi.setKeteranganTerlambat(absensi.getKeteranganTerlambat() != null ? absensi.getKeteranganTerlambat() : "-");
            absensi.setStatusAbsen(keterangan);
            absensi.setFotoMasuk(absensi.getFotoMasuk());

            Absensi savedAbsensi = absensiRepository.save(absensi);
            logger.info("Absensi berhasil disimpan untuk email: {}", email);

            return savedAbsensi;
        } catch (Exception e) {
            logger.error("Error processing absensi for email {}: {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    public Absensi PostAbsensiSmart(String email, Absensi absensi) throws IOException, ParseException {
        try {
            logger.info("Processing smart absensi for email: {}", email);

            List<Absensi> existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));
            if (!existingAbsensi.isEmpty()) {
                logger.warn("User {} sudah melakukan absensi masuk pada hari yang sama sebelumnya.", email);
                return null;
            }

            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User dengan email: " + email + " tidak ditemukan."));
                    logger.warn("User dengan email: " + email + " tidak ditemukan.");
            Shift shift = shiftRepository.findById(user.getShift().getId())
                    .orElseThrow(() -> new NotFoundException("ID shift tidak ditemukan"));

            Date tanggalHariIni = truncateTime(new Date());
            Date masuk = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String jamMasukString = formatter.format(masuk);

            LocalTime waktuMasukShift = LocalTime.parse(absensi.getJamShift());
            LocalTime waktuMasuk = LocalTime.parse(jamMasukString);

            String keterangan = waktuMasuk.isBefore(waktuMasukShift) ? "Lebih Awal" : "Terlambat";

            absensi.setUserEmail(email);
            absensi.setUser(user);
            absensi.setTanggalAbsen(tanggalHariIni);
            absensi.setJamMasuk(jamMasukString);
            absensi.setJamShift(absensi.getJamShift());
            absensi.setJamPulang("-");
            absensi.setLokasiMasuk(absensi.getLokasiMasuk());
            absensi.setLokasiPulang("-");
            absensi.setKeteranganTerlambat(absensi.getKeteranganTerlambat() != null ? absensi.getKeteranganTerlambat() : "-");
            absensi.setStatusAbsen(keterangan);
            absensi.setFotoMasuk(absensi.getFotoMasuk());

            Absensi savedAbsensi = absensiRepository.save(absensi);
            logger.info("Smart absensi berhasil disimpan untuk email: {}", email);

            return savedAbsensi;
        } catch (Exception e) {
            logger.error("Error processing smart absensi for email {}: {}", email, e.getMessage());
            throw e;
        }
    }

    private String getWeekRange(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startOfWeek = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endOfWeek = calendar.getTime();
        return startOfWeek.toString() + " - " + endOfWeek.toString();
    }

    @Override
        public Absensi PostAbsensiById(Long userId, Absensi absensi) throws IOException, ParseException {
        List<Absensi> existingAbsensi = absensiRepository.findByUserIdAndTanggalAbsen(userId, truncateTime(new Date()));
        if (!existingAbsensi.isEmpty()) {
            System.out.println("User  sudah melakukan absensi masuk pada hari yang sama sebelumnya.");
            return null;
        } else {
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User dengan id: " + userId + " tidak ditemukan."));
            Shift shift = shiftRepository.findById(user.getShift().getId())
                    .orElseThrow(() -> new NotFoundException("ID shift tidak ditemukan"));

            Date tanggalHariIni = truncateTime(new Date());
            Date masuk = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String jamMasukString = formatter.format(masuk);

            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date waktuMasukShift = timeFormatter.parse(shift.getWaktuMasuk());

            String keterangan = (masuk.before(waktuMasukShift)) ? "Lebih Awal" : "Terlambat";

//            Absensi absensi = new Absensi();
//            absensi.setUser(user);
            absensi.setUser(user);
            absensi.setTanggalAbsen(tanggalHariIni);
            absensi.setJamMasuk(jamMasukString);
            absensi.setJamPulang("-");
            absensi.setLokasiMasuk(absensi.getLokasiMasuk());
            absensi.setLokasiPulang("-");
            absensi.setKeteranganTerlambat(absensi.getKeteranganTerlambat() != null ? absensi.getKeteranganTerlambat() : "-");
            absensi.setStatusAbsen(keterangan);
            absensi.setFotoMasuk(absensi.getFotoMasuk());
            absensi.setUserEmail(user.getEmail());

            return absensiRepository.save(absensi);
        }
    }

    @Override
    public Absensi PostAbsensiSmartById(Long userId, Absensi absensi) throws IOException, ParseException {
        try {
            logger.info("Memproses absensi masuk untuk userId: {}", userId);

            List<Absensi> existingAbsensi = absensiRepository.findByUserIdAndTanggalAbsen(userId, truncateTime(new Date()));
            if (!existingAbsensi.isEmpty()) {
                logger.warn("User {} sudah melakukan absensi masuk pada hari yang sama sebelumnya.", userId);
                return null;
            }

            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User dengan id: " + userId + " tidak ditemukan."));
            Shift shift = shiftRepository.findById(user.getShift().getId())
                    .orElseThrow(() -> new NotFoundException("ID shift tidak ditemukan"));

            Date tanggalHariIni = truncateTime(new Date());
            Date masuk = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String jamMasukString = formatter.format(masuk);

            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date waktuMasukShift = timeFormatter.parse(absensi.getJamShift());

            String keterangan = (masuk.before(waktuMasukShift)) ? "Lebih Awal" : "Terlambat";

            absensi.setUser(user);
            absensi.setTanggalAbsen(tanggalHariIni);
            absensi.setJamMasuk(jamMasukString);
            absensi.setJamShift(absensi.getJamShift());
            absensi.setJamPulang("-");
            absensi.setLokasiMasuk(absensi.getLokasiMasuk());
            absensi.setLokasiPulang("-");
            absensi.setKeteranganTerlambat(absensi.getKeteranganTerlambat() != null ? absensi.getKeteranganTerlambat() : "-");
            absensi.setStatusAbsen(keterangan);
            absensi.setFotoMasuk(absensi.getFotoMasuk());
            absensi.setUserEmail(user.getEmail());

            Absensi savedAbsensi = absensiRepository.save(absensi);
            logger.info("Absensi masuk berhasil disimpan untuk userId: {}", userId);
            return savedAbsensi;
        } catch (EntityNotFoundException | NotFoundException e) {
            logger.error("User atau shift tidak ditemukan: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Terjadi error saat memproses absensi masuk untuk userId {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan dalam memproses absensi masuk", e);
        }
    }

    @Override
    public Absensi Pulang(String email, Absensi absensi) throws IOException, ParseException {
        try {
            logger.info("Memproses absensi pulang untuk email: {}", email);

            Absensi existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsenOptional(email, truncateTime(new Date()))
                    .orElseThrow(() -> new NotFoundException("User belum melakukan absensi masuk hari ini."));

            if (!existingAbsensi.getJamPulang().equals("-")) {
                logger.warn("User dengan email {} sudah melakukan absensi pulang hari ini", email);
                return null;
            }

            Date pulang = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String jamPulangString = formatter.format(pulang);

            existingAbsensi.setKeteranganPulangAwal(absensi.getKeteranganTerlambat() != null ? absensi.getKeteranganPulang() : "-");
            existingAbsensi.setJamPulang(jamPulangString);
            existingAbsensi.setLokasiPulang(absensi.getLokasiPulang());
            existingAbsensi.setFotoPulang(absensi.getFotoPulang());

            Absensi savedAbsensi = absensiRepository.save(existingAbsensi);
            logger.info("Absensi pulang berhasil disimpan untuk email: {}", email);
            return savedAbsensi;
        } catch (NotFoundException e) {
            logger.error("User belum melakukan absensi masuk: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Terjadi error saat memproses absensi pulang untuk email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan dalam memproses absensi pulang", e);
        }
    }

    @Override
    public boolean checkUserAlreadyAbsenToday(Long userId) {
        try {
            logger.debug("Memeriksa apakah userId {} sudah melakukan absensi hari ini", userId);
            Optional<Absensi> absensi = absensiRepository.findByUserIdAndTanggalAbsenOptional(userId, truncateTime(new Date()));
            return absensi.isPresent();
        } catch (Exception e) {
            logger.error("Gagal memeriksa absensi untuk userId {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean checkUserAlreadyAbsenTodayByEmail(String email) {
        try {
            logger.debug("Memeriksa apakah user dengan email {} sudah melakukan absensi hari ini", email);
            Optional<Absensi> absensi = absensiRepository.findByUserEmailAndTanggalAbsenOptional(email, truncateTime(new Date()));
            return absensi.isPresent();
        } catch (Exception e) {
            logger.error("Gagal memeriksa absensi untuk email {}: {}", email, e.getMessage(), e);
            return false;
        }
    }

//    @Override
//    public Absensi checkUserAlpha(Long userId) {
//        UserModel userModel = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("Id user tidak ditemukan"));
//
//        boolean absensiCheck = checkUserAlreadyAbsenToday(userId);
//
//        if (absensiCheck) {
//            throw new BadRequestException("User sudah melakukan absen hari ini");
//        }
//
//        Optional<Absensi> lastAbsensi = absensiRepository.findFirstByUserIdOrderByTanggalAbsenDesc(userId);
//        if (lastAbsensi.isEmpty()) {
//            Absensi absensi = lastAbsensi.get();
//            long hoursSinceLastAbsen = getHoursDifference(absensi.getTanggalAbsen(), new Date());
//            if (hoursSinceLastAbsen < 24) {
//                throw new BadRequestException("Belum 24 jam sejak absen terakhir. Status Alpha tidak dapat ditambahkan.");
//            }
//        }
//
//        Date tanggalHariIni = truncateTime(new Date());
//        Absensi absensi = new Absensi();
//        absensi.setJamMasuk("-");
//        absensi.setJamPulang("-");
//        absensi.setLokasiPulang("-");
//        absensi.setLokasiMasuk("-");
//        absensi.setFotoMasuk("-");
//        absensi.setFotoPulang("-");
//        absensi.setKeteranganTerlambat("-");
//        absensi.setKeteranganPulangAwal("-");
//        absensi.setTanggalAbsen(tanggalHariIni);
//        absensi.setUser(userModel);
//        absensi.setStatusAbsen("Alpha");
//
//        return absensiRepository.save(absensi);
//    }



//    @Override
//    public Absensi checkUserAlpha(Long userId) {
//        UserModel userModel = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("Id user tidak ditemukan"));
//
//        boolean absensiCheck = checkUserAlreadyAbsenToday(userId);
//        Date today = truncateTime(new Date());
//
//        Optional<Absensi> existingAlphaRecord = absensiRepository.findByUserIdAndTanggalAbsenAndStatusAbsen(
//                userId, today, "Alpha");
//
//        if (existingAlphaRecord.isPresent()) {
//            throw new BadRequestException("User sudah dialpha pada hari ini.");
//        }
//
//        if (!absensiCheck) {
//            String waktuPulangString = userModel.getShift().getWaktuPulang();
//
//            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//
//            LocalTime waktuPulang = LocalTime.parse(waktuPulangString, timeFormatter);
//
//            LocalTime currentTime = LocalTime.now();
//
//            if (currentTime.isAfter(waktuPulang)) {
//                Absensi absensi = new Absensi();
//                absensi.setJamMasuk("-");
//                absensi.setJamPulang("-");
//                absensi.setLokasiMasuk("-");
//                absensi.setLokasiPulang("-");
//                absensi.setFotoMasuk("-");
//                absensi.setFotoPulang("-");
//                absensi.setKeteranganTerlambat("-");
//                absensi.setKeteranganPulangAwal("-");
//                absensi.setTanggalAbsen(today);
//                absensi.setUser(userModel);
//                absensi.setStatusAbsen("Alpha");
//
//                return absensiRepository.save(absensi);
//            }
//
//            throw new BadRequestException("user belum terlambat untuk absen Alpha.");
//        }
//
//        throw new BadRequestException("User sudah melakukan absen hari ini");
//    }

    @Override
    public Absensi checkUserAlpha(Long userId) {
        logger.info("Memeriksa status Alpha untuk userId: {}", userId);

        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Id user tidak ditemukan: {}", userId);
                    return new NotFoundException("Id user tidak ditemukan");
                });

        boolean absensiCheck = checkUserAlreadyAbsenToday(userId);
        Date today = truncateTime(new Date());

        if (!absensiCheck) {
            Date endOfDay = truncateTime(today);
            Date currentTime = new Date();

            if (currentTime.after(endOfDay)) {
                Absensi absensi = new Absensi();
                absensi.setJamMasuk("-");
                absensi.setJamPulang("-");
                absensi.setLokasiMasuk("-");
                absensi.setLokasiPulang("-");
                absensi.setFotoMasuk("-");
                absensi.setFotoPulang("-");
                absensi.setKeteranganTerlambat("-");
                absensi.setKeteranganPulangAwal("-");
                absensi.setTanggalAbsen(today);
                absensi.setUser(userModel);
                absensi.setStatusAbsen("Alpha");

                logger.info("Menyimpan absensi dengan status Alpha untuk userId: {}", userId);
                return absensiRepository.save(absensi);
            }
            logger.error("Belum 24 jam sejak pagi untuk userId: {}", userId);
            throw new BadRequestException("Belum 24 jam sejak pagi");
        }

        logger.error("User sudah melakukan absen hari ini: {}", userId);
        throw new BadRequestException("User sudah melakukan absen hari ini");
    }

    @Override
    public boolean hasTakenLeave(Long userId) {
        logger.info("Memeriksa apakah userId {} sudah mengambil izin", userId);
        Optional<Absensi> izin = absensiRepository.findByUserIdAndKeteranganIzin(userId);
        return izin.isPresent();
    }

    @Override
    public Absensi izin(Long userId, String keteranganIzin) {
        logger.info("Mencatat izin untuk userId: {}, keterangan: {}", userId, keteranganIzin);
        List<Absensi> existingAbsensi = absensiRepository.findByUserIdAndTanggalAbsen(userId, truncateTime(new Date()));

        if (!existingAbsensi.isEmpty()) {
            logger.warn("User {} sudah melakukan absensi masuk pada hari ini.", userId);
            return null;
        }

        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User dengan ID {} tidak ditemukan.", userId);
                    return new EntityNotFoundException("User dengan ID: " + userId + " tidak ditemukan.");
                });

        Absensi absensi = new Absensi();
        absensi.setUser(user);
        absensi.setTanggalAbsen(truncateTime(new Date()));
        absensi.setJamMasuk("-");
        absensi.setJamPulang("-");
        absensi.setKeteranganIzin(keteranganIzin);
        absensi.setStatusAbsen("Izin");

        logger.info("Menyimpan absensi izin untuk userId: {}", userId);
        return absensiRepository.save(absensi);
    }

    @Override
    public Absensi izinByEmail(String email, String keteranganIzin) {
        logger.info("Mencatat izin berdasarkan email: {}, keterangan: {}", email, keteranganIzin);
        List<Absensi> existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));

        if (!existingAbsensi.isEmpty()) {
            logger.warn("User dengan email {} sudah melakukan absensi masuk pada hari yang sama sebelumnya.", email);
            return null;
        }

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User dengan email {} tidak ditemukan.", email);
                    return new EntityNotFoundException("User dengan email " + email + " tidak ditemukan.");
                });

        Absensi absensi = new Absensi();
        absensi.setUserEmail(email);
        absensi.setTanggalAbsen(truncateTime(new Date()));
        absensi.setJamMasuk("-");
        absensi.setJamPulang("-");
        absensi.setKeteranganIzin(keteranganIzin);
        absensi.setStatusAbsen("Izin");

        logger.info("Menyimpan absensi izin untuk email: {}", email);
        return absensiRepository.save(absensi);
    }

    @Override
    public Absensi izinTengahHari(Long userId, Absensi keterangaPulangAwal) {
        logger.info("Mencatat izin tengah hari untuk userId: {}", userId);
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserIdAndTanggalAbsenOptional(userId, truncateTime(new Date()));

        if (existingAbsensi.isPresent()) {
            Absensi absensi = existingAbsensi.get();
            String jamPulang = new SimpleDateFormat("HH:mm:ss").format(new Date());
            absensi.setJamPulang(jamPulang);
            absensi.setKeteranganPulangAwal(keterangaPulangAwal.getKeteranganPulangAwal());
            absensi.setStatusAbsen("Izin Tengah Hari");

            logger.info("Menyimpan absensi izin tengah hari untuk userId: {}", userId);
            return absensiRepository.save(absensi);
        }

        logger.error("User belum melakukan absensi masuk pada hari ini: {}", userId);
        throw new NotFoundException("User belum melakukan absensi masuk pada hari ini.");
    }

    @Override
    public Absensi izinTengahHariByEmail(String email, Absensi keterangaPulangAwal) {
        logger.info("Mencatat izin tengah hari berdasarkan email: {}", email);
        List<Absensi> existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));

        if (!existingAbsensi.isEmpty()) {
            Absensi absensi = existingAbsensi.get(0);
            String jamPulang = new SimpleDateFormat("HH:mm:ss").format(new Date());
            absensi.setJamPulang(jamPulang);
            absensi.setKeteranganPulangAwal(keterangaPulangAwal.getKeteranganPulangAwal());
            absensi.setStatusAbsen("Izin Tengah Hari");

            logger.info("Menyimpan absensi izin tengah hari untuk email: {}", email);
            return absensiRepository.save(absensi);
        }

        logger.warn("User dengan email {} belum melakukan absensi masuk pada hari ini.", email);
        return null;
    }

    @Override
    public List<Absensi> getByStatusAbsen(Long userId, String statusAbsen) {
        logger.info("Mengambil absensi dengan status '{}' untuk userId: {}", statusAbsen, userId);
        return absensiRepository.getByStatusAbsen(userId, statusAbsen);
    }

    @Override
    public Optional<Absensi> getAbsensiById(Long id) {
        logger.info("Mencari absensi dengan id: {}", id);
        return absensiRepository.findById(id);
    }

    @Override
    public Absensi updateAbsensi(Long id, Absensi absensi) {
        logger.info("Mengupdate absensi dengan id: {}", id);

        return absensiRepository.findById(id)
                .map(existingAbsensi -> {
                    logger.info("Absensi ditemukan, memproses pembaruan...");
                    existingAbsensi.setTanggalAbsen(absensi.getTanggalAbsen());
                    existingAbsensi.setJamMasuk(absensi.getJamMasuk());
                    existingAbsensi.setJamPulang(absensi.getJamPulang());
                    existingAbsensi.setLokasiMasuk(absensi.getLokasiMasuk());
                    existingAbsensi.setLokasiPulang(absensi.getLokasiPulang());
                    existingAbsensi.setKeteranganTerlambat(absensi.getKeteranganTerlambat());
                    existingAbsensi.setFotoMasuk(absensi.getFotoMasuk());
                    existingAbsensi.setFotoPulang(absensi.getFotoPulang());
                    existingAbsensi.setStatus(absensi.getStatus());
                    existingAbsensi.setStatusAbsen(absensi.getStatusAbsen());
                    existingAbsensi.setKeteranganIzin(absensi.getKeteranganIzin());
                    existingAbsensi.setKeteranganPulang(absensi.getKeteranganPulang());
                    existingAbsensi.setKeteranganPulangAwal(absensi.getKeteranganPulangAwal());

                    Absensi updatedAbsensi = absensiRepository.save(existingAbsensi);
                    logger.info("Absensi berhasil diperbarui: {}", id);
                    return updatedAbsensi;
                })
                .orElseThrow(() -> {
                    logger.error("Absensi tidak ditemukan dengan id: {}", id);
                    return new NotFoundException("Absensi not found with id: " + id);
                });
    }

    @Override
    public void deleteAbsensi(Long id) throws IOException {
        logger.info("Menghapus absensi dengan id: {}", id);

        Optional<Absensi> absensiOptional = absensiRepository.findById(id);
        if (absensiOptional.isPresent()) {
            Absensi absensi = absensiOptional.get();
            logger.info("Absensi ditemukan, memproses penghapusan...");

            String fotoMasukUrl = absensi.getFotoMasuk();
            if (fotoMasukUrl != null) {
                String fileNameMasuk = fotoMasukUrl.substring(fotoMasukUrl.indexOf("/o/") + 3, fotoMasukUrl.indexOf("?alt=media"));
                logger.info("Menghapus foto masuk: {}", fileNameMasuk);
                deleteFoto(fileNameMasuk);
            }

            String fotoPulangUrl = absensi.getFotoPulang();
            if (fotoPulangUrl != null && !fotoPulangUrl.isEmpty()) {
                String fileNamePulang = fotoPulangUrl.substring(fotoPulangUrl.indexOf("/o/") + 3, fotoPulangUrl.indexOf("?alt=media"));
                logger.info("Menghapus foto pulang: {}", fileNamePulang);
                deleteFoto(fileNamePulang);
            }

            absensiRepository.deleteById(id);
            logger.info("Absensi dengan id {} berhasil dihapus", id);
        } else {
            logger.error("Gagal menghapus absensi, tidak ditemukan dengan id: {}", id);
            throw new NotFoundException("Absensi not found with id: " + id);
        }
    }

    @Override
    public List<Absensi> getAbsensiByUserId(Long userId) {
        logger.info("Mengambil absensi untuk userId: {}", userId);
        return absensiRepository.findabsensiByUserId(userId);
    }

    @Override
    public List<Absensi> getAbsensiByEmail(String email) {
        logger.info("Mengambil absensi berdasarkan email: {}", email);
        return absensiRepository.findAbsensiByEmail(email);
    }


    private Date truncateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }




    private int getHourOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private boolean deleteFoto(String fileName) throws IOException {
        logger.info("Menghapus foto dengan nama file: {}", fileName);

        try {
            BlobId blobId = BlobId.of("absensireact.appspot.com", fileName);
            Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("./src/main/resources/FirebaseConfig.json"));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                logger.info("Foto berhasil dihapus: {}", fileName);
            } else {
                logger.warn("Foto tidak ditemukan atau gagal dihapus: {}", fileName);
            }

            return deleted;
        } catch (Exception e) {
            logger.error("Gagal menghapus foto: {}", fileName, e);
            throw new IOException("Terjadi kesalahan saat menghapus foto: " + fileName, e);
        }
    }

    private String uploadFoto(MultipartFile multipartFile) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String base_url = "https://s3.lynk2.co/api/s3/absenMasuk";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", multipartFile.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(base_url, HttpMethod.POST, requestEntity, String.class);
        String fileUrl = extractFileUrlFromResponse(response.getBody());
        return fileUrl;
    }

    private String uploadFotoPUlang(MultipartFile multipartFile) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String base_url = "https://s3.lynk2.co/api/s3/absenPulang";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", multipartFile.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(base_url, HttpMethod.POST, requestEntity, String.class);
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
//    private String uploadFile(MultipartFile multipartFile, String fileName) throws IOException {
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        String folderPath = "/absenMasuk/";
//        String fullPath = folderPath + timestamp + "_" + fileName;
//        BlobId blobId = BlobId.of("absensireact.appspot.com", fullPath);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
//        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("./src/main/resources/FirebaseConfig.json"));
//        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//        storage.create(blobInfo, multipartFile.getBytes());
//        return String.format(DOWNLOAD_URL, URLEncoder.encode(fullPath, StandardCharsets.UTF_8));
//    }
//
//    private String uploadFilePulang(MultipartFile multipartFile, String fileName) throws IOException {
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        String folderPath = "fotoAbsen/fotoPulang/";
//        String fullPath = folderPath + timestamp + "_" + fileName;
//        BlobId blobId = BlobId.of("absensireact.appspot.com", fullPath);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
//        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("./src/main/resources/FirebaseConfig.json"));
//        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//        storage.create(blobInfo, multipartFile.getBytes());
//        return String.format(DOWNLOAD_URL, URLEncoder.encode(fullPath, StandardCharsets.UTF_8));
//    }

    @Override
    public List<Object[]> getAbsensiDataGroupedByRole() {
        try {
            logger.info("Mengambil data absensi yang dikelompokkan berdasarkan role.");
            List<Object[]> result = absensiRepository.findAbsensiGroupedByRole();
            logger.info("Ditemukan {} grup absensi berdasarkan role.", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi berdasarkan role.", e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data absensi berdasarkan role", e);
        }
    }

    @Override
    public List<Absensi> getAbsensiByKelas(Long kelasId) {
        try {
            logger.info("Mengambil data absensi untuk kelas dengan ID: {}", kelasId);
            List<UserModel> users = userRepository.findByKelasId(kelasId);

            if (users.isEmpty()) {
                logger.warn("Tidak ada pengguna yang terkait dengan kelas ID: {}", kelasId);
                throw new NotFoundException("Tidak ada pengguna yang terkait dengan kelas dengan id: " + kelasId);
            }

            List<Absensi> absensiList = new ArrayList<>();
            for (UserModel user : users) {
                logger.info("Mengambil absensi untuk user ID: {}", user.getId());
                List<Absensi> userAbsensi = absensiRepository.findByUser(user);
                absensiList.addAll(userAbsensi);
            }

            logger.info("Ditemukan {} data absensi untuk kelas dengan ID: {}", absensiList.size(), kelasId);
            return absensiList;
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi untuk kelas ID: {}", kelasId, e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data absensi berdasarkan kelas", e);
        }
    }

    @Override
    public Map<String, List<Absensi>> getAbsensiByBulananPerKelas(int bulan, int tahun, Long kelasId) {
        try {
            logger.info("Mengambil data absensi untuk bulan: {}, tahun: {}, kelas ID: {}", bulan, tahun, kelasId);
            List<Absensi> absensiList = absensiRepository.findByBulananAndKelas(bulan, tahun, kelasId);
            Map<String, List<Absensi>> monthlyAbsensiMap = new HashMap<>();

            for (Absensi absensi : absensiList) {
                String monthKey = getMonthKey(absensi.getTanggalAbsen());
                monthlyAbsensiMap.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(absensi);
            }

            logger.info("Ditemukan {} data absensi untuk bulan: {}, tahun: {}, kelas ID: {}", absensiList.size(), bulan, tahun, kelasId);
            return monthlyAbsensiMap;
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi bulanan untuk bulan: {}, tahun: {}, kelas ID: {}", bulan, tahun, kelasId, e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data absensi berdasarkan bulan dan kelas", e);
        }
    }
    private String getMonthKey(Date tanggalAbsen) {
        LocalDate date = new java.sql.Date(tanggalAbsen.getTime()).toLocalDate();
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }

    @Override
    public List<Map<String, Object>> getAbsensiPerHariKelas(Date tanggal, Long idAdmin) {
        logger.info("Memproses absensi per hari untuk idAdmin: {} pada tanggal: {}", idAdmin, tanggal);

        try {
            // Ambil semua kelas
            List<Kelas> kelasList = kelasRepository.findByIdAdmin(idAdmin);
            logger.info("Ditemukan {} kelas untuk idAdmin: {}", kelasList.size(), idAdmin);

            // Atur start dan end of the day
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tanggal);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startOfDay = calendar.getTime();

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MILLISECOND, -1);
            Date endOfDay = calendar.getTime();

            logger.info("Menghitung absensi dari {} hingga {}", startOfDay, endOfDay);

            // List untuk menampung hasil
            List<Map<String, Object>> responseList = new ArrayList<>();

            // Loop untuk setiap kelas
            for (Kelas kelas : kelasList) {
                Long kelasId = kelas.getId();

                // Hitung total siswa dalam kelas
                int jumlahTotalSiswa = userRepository.findByKelasId(kelasId).size();
                logger.info("Kelas {} (ID: {}): Jumlah siswa = {}", kelas.getNamaKelas(), kelasId, jumlahTotalSiswa);

                // Ambil data absensi berdasarkan tanggal dan kelas
                List<Absensi> absensiList = absensiRepository.findByTanggalAndKelas(startOfDay, endOfDay, kelasId);
                logger.info("Kelas {} (ID: {}): Ditemukan {} data absensi", kelas.getNamaKelas(), kelasId, absensiList.size());

                // Hitung jumlah hadir
                int jumlahHadir = (int) absensiList.stream()
                        .map(Absensi::getUser)
                        .distinct()
                        .count();
                int jumlahTidakHadir = jumlahTotalSiswa - jumlahHadir;

                // Log hasil per kelas
                logger.info("Kelas {} (ID: {}): Hadir = {}, Tidak Hadir = {}", kelas.getNamaKelas(), kelasId, jumlahHadir, jumlahTidakHadir);

                // Buat respons dalam bentuk map
                Map<String, Object> response = new HashMap<>();
                response.put("kelasId", kelas.getId());
                response.put("kelasName", kelas.getNamaKelas());
                response.put("jumlahSiswa", jumlahTotalSiswa);
                response.put("hadir", jumlahHadir);
                response.put("tidakHadir", jumlahTidakHadir);

                // Tambahkan ke responseList
                responseList.add(response);
            }

            logger.info("Sukses memproses absensi per hari untuk idAdmin: {}", idAdmin);
            return responseList;

        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat memproses absensi per hari untuk idAdmin: {}", idAdmin, e);
            throw new RuntimeException("Gagal mengambil data absensi", e);
        }
    }

    @Override
    public byte[] exportAbsensiPerHariKelasToExcel(Date tanggal, Long idAdmin) throws IOException {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Memulai proses ekspor absensi ke Excel untuk tanggal {} dan idAdmin {}", tanggal, idAdmin);

        List<Map<String, Object>> absensiData = getAbsensiPerHariKelas(tanggal, idAdmin);
        logger.info("Jumlah data absensi yang ditemukan: {}", absensiData.size());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Absensi");
        int rowNum = 0;

        try {
            // Membuat header
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"No.", "Kelas", "Total Siswa", "Hadir", "Tidak Hadir", "Presentase (%)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int totalSiswa = 0, totalHadir = 0, totalTidakHadir = 0;
            List<Map<String, Object>> guruData = new ArrayList<>();

            for (int i = 0; i < absensiData.size(); i++) {
                Map<String, Object> data = absensiData.get(i);

                if ("GURU".equalsIgnoreCase((String) data.get("kelasName"))) {
                    guruData.add(data);
                    continue;
                }

                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                row.createCell(colNum++).setCellValue(i + 1);
                row.createCell(colNum++).setCellValue((String) data.get("kelasName"));
                int jumlahSiswa = (int) data.get("jumlahSiswa");
                int hadir = (int) data.get("hadir");
                int tidakHadir = (int) data.get("tidakHadir");
                double prosentase = jumlahSiswa > 0 ? (hadir * 100.0) / jumlahSiswa : 0;

                row.createCell(colNum++).setCellValue(jumlahSiswa);
                row.createCell(colNum++).setCellValue(hadir);
                row.createCell(colNum++).setCellValue(tidakHadir);
                row.createCell(colNum).setCellValue(String.format("%.2f", prosentase));

                totalSiswa += jumlahSiswa;
                totalHadir += hadir;
                totalTidakHadir += tidakHadir;
            }

            logger.info("Total siswa: {}, Total hadir: {}, Total tidak hadir: {}", totalSiswa, totalHadir, totalTidakHadir);

            // Baris total
            Row totalRow = sheet.createRow(rowNum++);
            Cell totalCell = totalRow.createCell(0);
            totalCell.setCellValue("Total");
            CellStyle totalTitleStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setColor(IndexedColors.WHITE.getIndex());
            totalTitleStyle.setFont(boldFont);
            totalTitleStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            totalTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalTitleStyle.setAlignment(HorizontalAlignment.CENTER);
            totalCell.setCellStyle(totalTitleStyle);

            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1));

            CellStyle totalDataStyle = workbook.createCellStyle();
            totalDataStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            totalDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font totalDataFont = workbook.createFont();
            totalDataFont.setColor(IndexedColors.WHITE.getIndex());
            totalDataStyle.setFont(totalDataFont);

            totalRow.createCell(2).setCellValue(totalSiswa);
            totalRow.createCell(3).setCellValue(totalHadir);
            totalRow.createCell(4).setCellValue(totalTidakHadir);
            double totalProsentase = totalSiswa > 0 ? (totalHadir * 100.0) / totalSiswa : 0;
            totalRow.createCell(5).setCellValue(String.format("%.2f", totalProsentase));

            for (int i = 2; i <= 5; i++) {
                totalRow.getCell(i).setCellStyle(totalDataStyle);
            }

            for (Map<String, Object> data : guruData) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                row.createCell(colNum++).setCellValue(" ");
                row.createCell(colNum++).setCellValue((String) data.get("kelasName"));
                int jumlahSiswa = (int) data.get("jumlahSiswa");
                int hadir = (int) data.get("hadir");
                int tidakHadir = (int) data.get("tidakHadir");
                double prosentase = jumlahSiswa > 0 ? (hadir * 100.0) / jumlahSiswa : 0;

                row.createCell(colNum++).setCellValue(jumlahSiswa);
                row.createCell(colNum++).setCellValue(hadir);
                row.createCell(colNum++).setCellValue(tidakHadir);
                row.createCell(colNum).setCellValue(String.format("%.2f", prosentase));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            logger.info("Ekspor absensi ke Excel selesai.");
            return outputStream.toByteArray();

        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat mengekspor absensi ke Excel", e);
            throw new IOException("Gagal mengekspor absensi ke Excel", e);
        }
    }
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    @Override
    public Map<String, Object> getAbsensiPerHari(Date tanggal, Long kelasId) {
        logger.info("Memproses absensi per hari untuk kelas ID: {}, tanggal: {}", kelasId, tanggal);

        try {
            // Ambil informasi kelas berdasarkan ID
            Kelas kelas = kelasRepository.findById(kelasId)
                    .orElseThrow(() -> new NotFoundException("Kelas dengan ID " + kelasId + " tidak ditemukan."));
            logger.debug("Kelas ditemukan: {} - {}", kelas.getId(), kelas.getNamaKelas());

            // Hitung total siswa dalam kelas
            int jumlahTotalSiswa = userRepository.findByKelasId(kelasId).size();
            logger.debug("Total siswa dalam kelas: {}", jumlahTotalSiswa);

            // Atur start dan end of the day
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tanggal);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startOfDay = calendar.getTime();

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MILLISECOND, -1);
            Date endOfDay = calendar.getTime();

            logger.debug("Start of day: {}, End of day: {}", startOfDay, endOfDay);

            List<Absensi> absensiList = absensiRepository.findByTanggalAndKelas(startOfDay, endOfDay, kelasId);
            logger.info("Ditemukan {} data absensi untuk kelas ID: {}", absensiList.size(), kelasId);

            int jumlahHadir = (int) absensiList.stream()
                    .map(Absensi::getUser)
                    .distinct()
                    .count();

            int jumlahTidakHadir = jumlahTotalSiswa - jumlahHadir;

            logger.info("Jumlah Hadir: {}, Jumlah Tidak Hadir: {}", jumlahHadir, jumlahTidakHadir);

            // Buat respons dalam bentuk map
            Map<String, Object> response = new HashMap<>();
            response.put("kelasId", kelas.getId());
            response.put("kelasName", kelas.getNamaKelas());
            response.put("jumlahSiswa", jumlahTotalSiswa);
            response.put("hadir", jumlahHadir);
            response.put("tidakHadir", jumlahTidakHadir);

            return response;
        } catch (Exception e) {
            logger.error("Terjadi error saat mengambil absensi per hari untuk kelas ID: {}", kelasId, e);
            throw new RuntimeException("Terjadi kesalahan dalam mengambil data absensi per hari", e);
        }
    }

    @Override
    public Map<String, List<Absensi>> getAbsensiHarianByKelas(Date tanggal, Long kelasId) {
        logger.info("Mengambil absensi harian untuk kelas ID: {}, tanggal: {}", kelasId, tanggal);

        try {
            // Normalize date to start and end of the day
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(tanggal);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date startOfDay = calendar.getTime();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endOfDay = calendar.getTime();

            logger.debug("Start of day: {}, End of day: {}", startOfDay, endOfDay);

            // Fetch data based on the provided date and kelasId
            List<Absensi> absensiList = absensiRepository.findByTanggalAndKelas(startOfDay, endOfDay, kelasId);
            logger.info("Ditemukan {} data absensi untuk kelas ID: {}", absensiList.size(), kelasId);

            Map<String, List<Absensi>> dailyAbsensiMap = new HashMap<>();

            // Use the date as the key
            String dateKey = startOfDay.toString();
            dailyAbsensiMap.put(dateKey, absensiList);

            return dailyAbsensiMap;
        } catch (Exception e) {
            logger.error("Terjadi error saat mengambil absensi harian untuk kelas ID: {}", kelasId, e);
            throw new RuntimeException("Terjadi kesalahan dalam mengambil data absensi harian", e);
        }
    }


    @Override
    public List<Absensi> getAbsensiByOrangTua(Long orangTuaId) {
        try {
            // Fetch the OrangTua entity
            logger.debug("Mengambil data absensi by orangtua dari repository untuk idOrangTua: {}", orangTuaId);
            OrangTua orangTua = orangTuaRepository.findById(orangTuaId)
                    .orElseThrow(() -> new RuntimeException("OrangTua not found"));

            // Fetch all Absensi entries where the associated user has the given orangTuaId
            return absensiRepository.findByOrangTuaId(orangTuaId);
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi by oragntua dari repository untuk idOrangTua: {}", orangTuaId, e);
            throw new RuntimeException("Terjadi kesalahan dalam mengambil data absensi by orangtua", e);
        }
    }

    @Override
    public List<Absensi> getStatusAbsenIzinByOrangTua(Long idOrangTua) {
        try {
            logger.debug("Mengambil data absensi izin dari repository untuk idOrangTua: {}", idOrangTua);

            List<Absensi> absensiList = absensiRepository.getStatusAbsenIzinByOrangTua(idOrangTua);

            logger.debug("Ditemukan {} data absensi izin untuk idOrangTua: {}", absensiList.size(), idOrangTua);

            return absensiList;
        } catch (Exception e) {
            logger.error("Gagal mengambil data absensi izin dari repository untuk idOrangTua: {}", idOrangTua, e);
            throw new RuntimeException("Terjadi kesalahan dalam mengambil data absensi izin", e);
        }
    }


}
