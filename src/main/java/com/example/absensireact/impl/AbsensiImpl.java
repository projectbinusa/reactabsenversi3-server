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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;


@Service
public class AbsensiImpl implements AbsensiService {
//    private final AbsensiService absensiService;
    static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/absensireact.appspot.com/o/%s?alt=media";

    private static final String BASE_URL = "https://s3.lynk2.co/api/s3";

    private static final Logger logger = Logger.getLogger(AbsensiService.class.getName());

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
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Id Admin tidak ditemukan dengan id: " + adminId));

        // Ambil semua pengguna yang terkait dengan admin ini, termasuk pengguna dengan user_id NULL
        List<UserModel> users = userRepository.findByadminIdAbsensi(admin.getId());

        if (users.isEmpty()) {
            throw new NotFoundException("Tidak ada pengguna yang terkait dengan admin dengan id: " + adminId);
        }

        // Inisialisasi absensiList untuk menyimpan data absensi
        List<Absensi> absensiList = new ArrayList<>();
        for (UserModel user : users) {
            // Tambahkan semua data absensi untuk setiap user ke dalam list
            absensiList.addAll(absensiRepository.findByUser(user));
        }

        return absensiList;
    }



    @Override
    public List<Absensi> getAllAbsensi(){
        return  absensiRepository.findAll();
    }

    public List<Absensi> getAbsensiByTanggal(Date tanggalAbsen) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tanggalAbsen);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = calendar.get(Calendar.YEAR);

        logger.info("Fetching absensi for day: " + day + ", month: " + month + ", year: " + year);

        List<Absensi> absensiList = absensiRepository.findByTanggalAbsen(day, month, year);

        logger.info("Number of records found: " + absensiList.size());

        return absensiList;
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
        Admin admin = adminRepository.findById(idAdmin)
                        .orElseThrow(() -> new NotFoundException(" id admin tidak ditemukaan : " + idAdmin));

        List<Absensi> absensiList = absensiRepository.findByMonth(month);

//        logger.info("Number of records found: " + absensiList.size());

        return absensiList;
}


    @Override
    public Map<String, List<Absensi>> getAbsensiByMingguan(Date tanggalAwal, Date tanggalAkhir) {
        List<Absensi> absensiList = absensiRepository.findByMingguan(tanggalAwal, tanggalAkhir);
        Map<String, List<Absensi>> weeklyAbsensiMap = new HashMap<>();

        for (Absensi absensi : absensiList) {
            String weekRange = getWeekRange(absensi.getTanggalAbsen());
            weeklyAbsensiMap.computeIfAbsent(weekRange, k -> new ArrayList<>()).add(absensi);
        }

        return weeklyAbsensiMap;
    }



    @Override
    public Map<String, List<Absensi>> getAbsensiByMingguanPerKelas(Date tanggalAwal, Date tanggalAkhir, Long kelasId) {
        // Fetch data based on the provided dates and kelasId
        List<Absensi> absensiList = absensiRepository.findByMingguanAndKelas(tanggalAwal, tanggalAkhir, kelasId);
        Map<String, List<Absensi>> weeklyAbsensiMap = new HashMap<>();

        for (Absensi absensi : absensiList) {
            String weekRange = getWeekRange(absensi.getTanggalAbsen());
            weeklyAbsensiMap.computeIfAbsent(weekRange, k -> new ArrayList<>()).add(absensi);
        }

        return weeklyAbsensiMap;
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
    public Absensi PostAbsensi(String email, Absensi absensi) throws IOException, ParseException {
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));
        if (existingAbsensi.isPresent()) {
            throw new NotFoundException("User sudah melakukan absensi masuk pada hari yang sama sebelumnya.");
        } else {
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

            return absensiRepository.save(absensi);
        }
    }

    @Override
    public Absensi PostAbsensiSmart(String email, Absensi absensi) throws IOException, ParseException {
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));
        if (existingAbsensi.isPresent()) {
            throw new NotFoundException("User sudah melakukan absensi masuk pada hari yang sama sebelumnya.");
        } else {
            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User dengan email: " + email + " tidak ditemukan."));
            Shift shift = shiftRepository.findById(user.getShift().getId())
                    .orElseThrow(() -> new NotFoundException("ID shift tidak ditemukan"));

            Date tanggalHariIni = truncateTime(new Date());
            Date masuk = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String jamMasukString = formatter.format(masuk);

            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date waktuMasukShift = timeFormatter.parse(absensi.getJamShift());

            String keterangan = (masuk.before(waktuMasukShift)) ? "Lebih Awal" : "Terlambat";

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

            return absensiRepository.save(absensi);
        }
    }

    @Override
        public Absensi PostAbsensiById(Long userId, Absensi absensi) throws IOException, ParseException {
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserIdAndTanggalAbsen(userId, truncateTime(new Date()));
        if (existingAbsensi.isPresent()) {
            throw new NotFoundException("User sudah melakukan absensi masuk pada hari yang sama sebelumnya.");
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
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserIdAndTanggalAbsen(userId, truncateTime(new Date()));
        if (existingAbsensi.isPresent()) {
            throw new NotFoundException("User sudah melakukan absensi masuk pada hari yang sama sebelumnya.");
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
            Date waktuMasukShift = timeFormatter.parse(absensi.getJamShift());

            String keterangan = (masuk.before(waktuMasukShift)) ? "Lebih Awal" : "Terlambat";

//            Absensi absensi = new Absensi();
//            absensi.setUser(user);
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

            return absensiRepository.save(absensi);
        }
    }


    @Override
    public Absensi Pulang(String email, Absensi absensi) throws IOException, ParseException {
        Absensi existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()))
                .orElseThrow(() -> new NotFoundException("User belum melakukan absensi masuk hari ini."));

        if (!existingAbsensi.getJamPulang().equals("-")) {
            throw new NotFoundException("User sudah melakukan absensi pulang hari ini.");
        }

        Date pulang = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String jamPulangString = formatter.format(pulang);

        existingAbsensi.setKeteranganPulangAwal(absensi.getKeteranganTerlambat() != null ? absensi.getKeteranganPulang() : "-");
        existingAbsensi.setJamPulang(jamPulangString);
        existingAbsensi.setLokasiPulang(absensi.getLokasiPulang());
        existingAbsensi.setFotoPulang(absensi.getFotoPulang());

        return absensiRepository.save(existingAbsensi);
    }


    @Override
    public boolean checkUserAlreadyAbsenToday(Long userId) {
        Optional<Absensi> absensi = absensiRepository.findByUserIdAndTanggalAbsen(userId, truncateTime(new Date()));
        return absensi.isPresent();
    }


    @Override
    public boolean checkUserAlreadyAbsenTodayByEmail(String email) {
        Optional<Absensi> absensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));
        return absensi.isPresent();
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
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Id user tidak ditemukan"));

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

                return absensiRepository.save(absensi);
            }
            throw new BadRequestException("Belum 24 jam sejak pagi");
        }

        throw new BadRequestException("User sudah melakukan absen hari ini");
    }



    @Override
    public boolean hasTakenLeave(Long userId) {
        Optional<Absensi> izin = absensiRepository.findByUserIdAndKeteranganIzin(userId);
        return izin.isPresent();
    }

    @Override
    public Absensi izin(Long userId, String keteranganIzin) {
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserIdAndTanggalAbsen(userId, truncateTime(new Date()));
        if (existingAbsensi.isPresent()) {
            throw new NotFoundException("User sudah melakukan absensi masuk pada hari yang sama sebelumnya.");
        } else {
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User dengan ID: " + userId + " tidak ditemukan."));

            Date tanggalHariIni = truncateTime(new Date());
            Absensi absensi = new Absensi();
            absensi.setUser(user);
            absensi.setTanggalAbsen(tanggalHariIni);
            absensi.setJamMasuk("-");
            absensi.setJamPulang("-");
            absensi.setKeteranganIzin(keteranganIzin);
            absensi.setStatusAbsen("Izin");

            return absensiRepository.save(absensi);
        }
    }
    @Override
    public Absensi izinByEmail(String email, String keteranganIzin) {
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));
        if (existingAbsensi.isPresent()) {
            throw new NotFoundException("User sudah melakukan absensi masuk pada hari yang sama sebelumnya.");
        } else {
            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User dengan email " + email + " tidak ditemukan."));

            Date tanggalHariIni = truncateTime(new Date());
            Absensi absensi = new Absensi();
            absensi.setUserEmail(email);
            absensi.setTanggalAbsen(tanggalHariIni);
            absensi.setJamMasuk("-");
            absensi.setJamPulang("-");
            absensi.setKeteranganIzin(keteranganIzin);
            absensi.setStatusAbsen("Izin");

            return absensiRepository.save(absensi);
        }
    }

    @Override
    public Absensi izinTengahHari(Long userId , Absensi keterangaPulangAwal )   {
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserIdAndTanggalAbsen(userId, truncateTime(new Date()));
        if (existingAbsensi.isPresent()) {
            Absensi absensi = existingAbsensi.get();
            Date masuk = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            String jamPulang = formatter.format(masuk);
            absensi.setJamPulang(jamPulang);
            absensi.setKeteranganPulangAwal(keterangaPulangAwal.getKeteranganPulangAwal());
            absensi.setStatusAbsen("Izin Tengah Hari");
            return absensiRepository.save(absensi);
        } else {
            throw new NotFoundException("User belum melakukan absensi masuk pada hari ini.");
        }
    }

 @Override
    public Absensi izinTengahHariByEmail(String email , Absensi keterangaPulangAwal )   {
        Optional<Absensi> existingAbsensi = absensiRepository.findByUserEmailAndTanggalAbsen(email, truncateTime(new Date()));
        if (existingAbsensi.isPresent()) {
            Absensi absensi = existingAbsensi.get();
            Date masuk = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            String jamPulang = formatter.format(masuk);
            absensi.setJamPulang(jamPulang);
            absensi.setKeteranganPulangAwal(keterangaPulangAwal.getKeteranganPulangAwal());
            absensi.setStatusAbsen("Izin Tengah Hari");
            return absensiRepository.save(absensi);
        } else {
            throw new NotFoundException("User belum melakukan absensi masuk pada hari ini.");
        }
    }


    @Override
    public List<Absensi> getByStatusAbsen(Long userId, String statusAbsen) {
        return absensiRepository.getByStatusAbsen(userId, statusAbsen);
    }
    @Override
    public Optional<Absensi> getAbsensiById(Long id) {
        return absensiRepository.findById(id);
    }

    @Override
    public Absensi updateAbsensi(Long id, Absensi absensi) {
        return absensiRepository.findById(id)
                .map(existingAbsensi -> {
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
                    return absensiRepository.save(existingAbsensi);
                })
                .orElseThrow(() -> new NotFoundException("Absensi not found with id: " + id));
    }

    @Override
    public void deleteAbsensi(Long id) throws IOException {
        Optional<Absensi> absensiOptional = absensiRepository.findById(id);
        if (absensiOptional.isPresent()) {
            Absensi absensi = absensiOptional.get();

            String fotoMasukUrl = absensi.getFotoMasuk();
            if (fotoMasukUrl != null) {
                String fileNameMasuk = fotoMasukUrl.substring(fotoMasukUrl.indexOf("/o/") + 3, fotoMasukUrl.indexOf("?alt=media"));
                deleteFoto(fileNameMasuk);
            }

            String fotoPulangUrl = absensi.getFotoPulang();
            if (fotoPulangUrl != null && !fotoPulangUrl.isEmpty()) {
                String fileNamePulang = fotoPulangUrl.substring(fotoPulangUrl.indexOf("/o/") + 3, fotoPulangUrl.indexOf("?alt=media"));
                deleteFoto(fileNamePulang);
            }

            absensiRepository.deleteById(id);

        } else {
            throw new NotFoundException("Absensi not found with id: " + id);
        }
    }
    @Override
    public List<Absensi> getAbsensiByUserId(Long userId) {
        return absensiRepository.findabsensiByUserId(userId);
    }

    @Override
    public List<Absensi> getAbsensiByEmail(String email) {
        System.out.println("Email dari token: " + email);
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
        BlobId blobId = BlobId.of("absensireact.appspot.com", fileName);
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("./src/main/resources/FirebaseConfig.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.delete(blobId);
        return true;
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
    public List<Absensi> getAbsensiByKelas(Long kelasId) {
        List<UserModel> users = userRepository.findByKelasId(kelasId);
        if (users.isEmpty()) {
            throw new NotFoundException("Tidak ada pengguna yang terkait dengan kelas dengan id: " + kelasId);
        }

        List<Absensi> absensiList = new ArrayList<>();
        for (UserModel user : users) {
            List<Absensi> userAbsensi = absensiRepository.findByUser(user);
            absensiList.addAll(userAbsensi);
        }

        return absensiList;
    }

    @Override
    public Map<String, List<Absensi>> getAbsensiByBulananPerKelas(int bulan, int tahun, Long kelasId) {
        // Fetch data based on the provided month, year, and kelasId
        List<Absensi> absensiList = absensiRepository.findByBulananAndKelas(bulan, tahun, kelasId);
        Map<String, List<Absensi>> monthlyAbsensiMap = new HashMap<>();

        for (Absensi absensi : absensiList) {
            String monthKey = getMonthKey(absensi.getTanggalAbsen());
            monthlyAbsensiMap.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(absensi);
        }

        return monthlyAbsensiMap;
    }

    private String getMonthKey(Date tanggalAbsen) {
        LocalDate date = new java.sql.Date(tanggalAbsen.getTime()).toLocalDate();
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }
    @Override
    public List<Map<String, Object>> getAbsensiPerHari(Date tanggal) {
        // Ambil semua kelas
        List<Kelas> kelasList = kelasRepository.findAll();

        // Atur start dan end of the day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tanggal);

        // Set start of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endOfDay = calendar.getTime();

        // List untuk menampung hasil
        List<Map<String, Object>> responseList = new ArrayList<>();

        // Loop untuk setiap kelas
        for (Kelas kelas : kelasList) {
            Long kelasId = kelas.getId();

            // Hitung total siswa dalam kelas
            int jumlahTotalSiswa = userRepository.findByKelasId(kelasId).size();

            // Ambil data absensi berdasarkan tanggal dan kelas
            List<Absensi> absensiList = absensiRepository.findByTanggalAndKelas(startOfDay, endOfDay, kelasId);

            // Hitung jumlah hadir
            int jumlahHadir = (int) absensiList.stream()
                    .map(Absensi::getUser)
                    .distinct()
                    .count();

            // Hitung jumlah tidak hadir
            int jumlahTidakHadir = jumlahTotalSiswa - jumlahHadir;

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

        return responseList;
    }

    @Override
    public Map<String, Object> getAbsensiPerHari(Date tanggal, Long kelasId) {
        // Ambil informasi kelas berdasarkan ID
        Kelas kelas = kelasRepository.findById(kelasId)
                .orElseThrow(() -> new NotFoundException("Kelas dengan ID " + kelasId + " tidak ditemukan."));

        // Hitung total siswa dalam kelas
        int jumlahTotalSiswa = userRepository.findByKelasId(kelasId).size();

        // Atur start dan end of the day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tanggal);

        // Set start of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endOfDay = calendar.getTime();

        List<Absensi> absensiList = absensiRepository.findByTanggalAndKelas(startOfDay, endOfDay, kelasId);

        int jumlahHadir = (int) absensiList.stream()
                .map(Absensi::getUser)
                .distinct()
                .count();

        int jumlahTidakHadir = jumlahTotalSiswa - jumlahHadir;

        // Buat respons dalam bentuk map
        Map<String, Object> response = new HashMap<>();
        response.put("kelasId", kelas.getId());
        response.put("kelasName", kelas.getNamaKelas());
        response.put("jumlahSiswa", jumlahTotalSiswa);
        response.put("hadir", jumlahHadir);
        response.put("tidakHadir", jumlahTidakHadir);

        return response;
    }



    @Override
    public Map<String, List<Absensi>> getAbsensiHarianByKelas(Date tanggal, Long kelasId) {
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

        // Fetch data based on the provided date and kelasId
        List<Absensi> absensiList = absensiRepository.findByTanggalAndKelas(startOfDay, endOfDay, kelasId);
        Map<String, List<Absensi>> dailyAbsensiMap = new HashMap<>();

        // Use the date as the key
        String dateKey = startOfDay.toString();
        dailyAbsensiMap.put(dateKey, absensiList);

        return dailyAbsensiMap;
    }

    @Override
    public List<Absensi> getAbsensiByOrangTua(Long orangTuaId) {
        // Fetch the OrangTua entity
        OrangTua orangTua = orangTuaRepository.findById(orangTuaId)
                .orElseThrow(() -> new RuntimeException("OrangTua not found"));

        // Fetch all Absensi entries where the associated user has the given orangTuaId
        return absensiRepository.findByOrangTuaId(orangTuaId);
    }

    @Override
    public List<Absensi> getStatusAbsenIzinByOrangTua(Long idOrangTua) {
        return absensiRepository.getStatusAbsenIzinByOrangTua(idOrangTua);
    }

}
