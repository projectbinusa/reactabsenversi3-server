package com.example.absensireact.repository;

import com.example.absensireact.model.Absensi;
import com.example.absensireact.model.UserModel;
import org.hibernate.internal.util.StringHelper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.threeten.bp.LocalDate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AbsensiRepository extends JpaRepository<Absensi , Long> {

    Optional<Absensi> findByUserAndTanggalAbsen(UserModel user, Date tanggalAbsen);

    Optional<Absensi> findByUserIdAndTanggalAbsenAndStatusAbsen(Long userId, Date tanggalAbsen, String statusAbsen);

    @Query(value = "SELECT * FROM absensi WHERE user_id = userId AND tanggal_absen = tanggalAbsen" , nativeQuery = true)
    boolean existsByUserIdAndTanggalAbsen(Long userId, Date tanggalAbsen);

    @Query("SELECT a FROM Absensi a WHERE a.tanggalAbsen BETWEEN :tanggalAwal AND :tanggalAkhir")
    List<Absensi> findByMingguan(@Param("tanggalAwal") Date tanggalAwal, @Param("tanggalAkhir") Date tanggalAkhir);

    List<Absensi> findByUser(UserModel user);
    @Query("SELECT a FROM Absensi a WHERE YEAR(a.tanggalAbsen) = :year AND MONTH(a.tanggalAbsen) = :month AND DAY(a.tanggalAbsen) = :day AND a.user = :user")
    List<Absensi> findByUserAndDate(@Param("user") UserModel user, @Param("year") int year, @Param("month") int month, @Param("day") int day);

    @Query("SELECT a FROM Absensi a WHERE FUNCTION('DAY', a.tanggalAbsen) = :day AND FUNCTION('MONTH', a.tanggalAbsen) = :month AND FUNCTION('YEAR', a.tanggalAbsen) = :year")
    List<Absensi> findByTanggalAbsen(@Param("day") int day, @Param("month") int month, @Param("year") int year);

    @Query("SELECT a FROM Absensi a WHERE FUNCTION('MONTH', a.tanggalAbsen) = :month AND FUNCTION('YEAR', a.tanggalAbsen) = :year")
    List<Absensi> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT * FROM absensi WHERE user_id = :userId AND keterangan_izin IS NOT NULL", nativeQuery = true)
    Optional<Absensi> findByUserIdAndKeteranganIzin(Long userId);


    @Query("SELECT a FROM Absensi a WHERE FUNCTION('MONTH', a.tanggalAbsen) = :month")
    List<Absensi> findByMonth(@Param("month") int month);
    @Query(value = "SELECT * FROM absensi WHERE user_id = :userId" , nativeQuery = true)
    List<Absensi>findByUserId (Long userId);

    @Query(value = "SELECT * FROM absensi WHERE user_id = :userId" , nativeQuery = true)
    List<Absensi>findabsensiByUserId (Long userId);

    @Query(value = "SELECT * FROM absensi WHERE user_email = :email" , nativeQuery = true)
    List<Absensi>findAbsensiByEmail (@Param("email") String email);

    @Query(value = "SELECT * FROM absensi WHERE user_email = :email AND DATE(tanggal_absen) = :tanggalAbsen", nativeQuery = true)
    Optional<Absensi> findByUserEmailAndTanggalAbsen(String email, Date tanggalAbsen);

    @Query(value = "SELECT * FROM absensi WHERE user_id = :userId AND DATE(tanggal_absen) = :tanggalAbsen", nativeQuery = true)
    Optional<Absensi> findByUserIdAndTanggalAbsen(Long userId, Date tanggalAbsen);

    @Query(value = "SELECT * FROM absensi WHERE user_id = :userId AND status_absen = :statusAbsen " , nativeQuery = true)
    List<Absensi> getByStatusAbsen (Long userId  , String statusAbsen);

    @Query("SELECT a FROM Absensi a WHERE DAY(a.tanggalAbsen) = :day AND MONTH(a.tanggalAbsen) = :month AND YEAR(a.tanggalAbsen) = :year AND a.user.kelas.id = :kelasId")
    List<Absensi> findByKelasIdAndDate(@Param("kelasId") Long kelasId, @Param("day") int day, @Param("month") int month, @Param("year") int year);

    @Query("SELECT a FROM Absensi a WHERE MONTH(a.tanggalAbsen) = :month AND YEAR(a.tanggalAbsen) = :year AND a.user.kelas.id = :kelasId")
    List<Absensi> findByKelasIdAndBulan(@Param("kelasId") Long kelasId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT a FROM Absensi a WHERE a.tanggalAbsen BETWEEN :tanggalAwal AND :tanggalAkhir AND a.user.kelas.id = :kelasId")
    List<Absensi> findByMingguanAndKelas(
            @Param("tanggalAwal") Date tanggalAwal,
            @Param("tanggalAkhir") Date tanggalAkhir,
            @Param("kelasId") Long kelasId);

    @Query("SELECT a FROM Absensi a WHERE a.user.kelas.id = :kelasId AND FUNCTION('MONTH', a.tanggalAbsen) = :bulan AND FUNCTION('YEAR', a.tanggalAbsen) = :tahun")
    List<Absensi> findByBulananAndKelas(@Param("bulan") int bulan, @Param("tahun") int tahun, @Param("kelasId") Long kelasId);

    @Query("SELECT a FROM Absensi a WHERE a.user.kelas.id = :kelasId AND a.tanggalAbsen BETWEEN :startOfDay AND :endOfDay")
    List<Absensi> findByTanggalAndKelas(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay, @Param("kelasId") Long kelasId);

    @Query("SELECT a FROM Absensi a WHERE a.user.id IN (SELECT u.id FROM UserModel u WHERE u.orangTua.id = :orangTuaId)")
    List<Absensi> findByOrangTuaId(@Param("orangTuaId") Long orangTuaId);

    @Query("SELECT a FROM Absensi a WHERE a.user.kelas.id = :kelasId")
    List<Absensi> findAbsensiByKelasId(@Param("kelasId") Long kelasId);

    @Query(value = "SELECT a.* FROM absensi a " +
            "INNER JOIN user u ON a.user_id = u.id " +
            "INNER JOIN orang_tua o ON u.id_orang_tua = o.id " +
            "WHERE o.id = :idOrangTua AND a.status_absen = 'Izin'", nativeQuery = true)
    List<Absensi> getStatusAbsenIzinByOrangTua(@Param("idOrangTua") Long idOrangTua);


}
