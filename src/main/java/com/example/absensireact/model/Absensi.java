package com.example.absensireact.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "absensi")
public class Absensi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tanggalAbsen")
    private Date tanggalAbsen;

    @Column(name = "jamMasuk")
    private String jamMasuk;

    @Column(name = "lokasiMasuk")
    private String lokasiMasuk;

    @Column(name = "lokasiPulang")
    private String lokasiPulang;

    @Column(name = "keteranganPulang")
    private String keteranganPulang;

    @Column(name = "keteranganIzin")
    private String keteranganIzin;

    @Column(name = "keteranganPulangAwal")
    private String keteranganPulangAwal;

    @Column(name = "jamPulang")
    private String jamPulang;

    @Column(name = "jamShift")
    private String jamShift;

    @Column(name = "keteranganTerlambat")
    private String keteranganTerlambat;


    @Column(name = "fotoMasuk")
    private String fotoMasuk;

    @Column(name = "fotoPulang")
    private String fotoPulang;

    @Column(name = "status")
    private String status;

    @Column(name = "statusAbsen")
    private String statusAbsen;

    @OneToOne
    @JoinColumn(name = "userId")
    private UserModel  user;

    @OneToOne
    @JoinColumn(name = "shiftId")
    private Shift shift;

    @Column(name = "userEmail") // Add this line
    private String userEmail;



    public Absensi() {

    }

    public Absensi(Long id, Date tanggalAbsen, String jamMasuk, String lokasiMasuk, String lokasiPulang, String keteranganPulang, String keteranganIzin, String keteranganPulangAwal, String jamPulang, String jamShift, String keteranganTerlambat, String fotoMasuk, String fotoPulang, String status, String statusAbsen, UserModel user, Shift shift, String userEmail) {
        this.id = id;
        this.tanggalAbsen = tanggalAbsen;
        this.jamMasuk = jamMasuk;
        this.lokasiMasuk = lokasiMasuk;
        this.lokasiPulang = lokasiPulang;
        this.keteranganPulang = keteranganPulang;
        this.keteranganIzin = keteranganIzin;
        this.keteranganPulangAwal = keteranganPulangAwal;
        this.jamPulang = jamPulang;
        this.jamShift = jamShift;
        this.keteranganTerlambat = keteranganTerlambat;
        this.fotoMasuk = fotoMasuk;
        this.fotoPulang = fotoPulang;
        this.status = status;
        this.statusAbsen = statusAbsen;
        this.user = user;
        this.shift = shift;
        this.userEmail = userEmail;
    }

    //    public Absensi(Long id, Date tanggalAbsen, String jamMasuk, String lokasiMasuk, String lokasiPulang, String keteranganPulang, String keteranganIzin, String keteranganPulangAwal, String jamPulang, String keteranganTerlambat, String fotoMasuk, String fotoPulang, String status, String statusAbsen, UserModel user, Shift shift ,  String userEmail) {
//        this.id = id;
//        this.tanggalAbsen = tanggalAbsen;
//        this.jamMasuk = jamMasuk;
//        this.lokasiMasuk = lokasiMasuk;
//        this.lokasiPulang = lokasiPulang;
//        this.keteranganPulang = keteranganPulang;
//        this.keteranganIzin = keteranganIzin;
//        this.keteranganPulangAwal = keteranganPulangAwal;
//        this.jamPulang = jamPulang;
//        this.keteranganTerlambat = keteranganTerlambat;
//        this.fotoMasuk = fotoMasuk;
//        this.fotoPulang = fotoPulang;
//        this.status = status;
//        this.statusAbsen = statusAbsen;
//        this.user = user;
//        this.shift = shift;
//        this.userEmail = userEmail;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTanggalAbsen() {
        return tanggalAbsen;
    }

    public void setTanggalAbsen(Date tanggalAbsen) {
        this.tanggalAbsen = tanggalAbsen;
    }

    public String getJamMasuk() {
        return jamMasuk;
    }

    public void setJamMasuk(String jamMasuk) {
        this.jamMasuk = jamMasuk;
    }

    public String getLokasiMasuk() {
        return lokasiMasuk;
    }

    public void setLokasiMasuk(String lokasiMasuk) {
        this.lokasiMasuk = lokasiMasuk;
    }

    public String getLokasiPulang() {
        return lokasiPulang;
    }

    public void setLokasiPulang(String lokasiPulang) {
        this.lokasiPulang = lokasiPulang;
    }

    public String getKeteranganPulang() {
        return keteranganPulang;
    }

    public void setKeteranganPulang(String keteranganPulang) {
        this.keteranganPulang = keteranganPulang;
    }

    public String getKeteranganIzin() {
        return keteranganIzin;
    }

    public void setKeteranganIzin(String keteranganIzin) {
        this.keteranganIzin = keteranganIzin;
    }

    public String getKeteranganPulangAwal() {
        return keteranganPulangAwal;
    }

    public void setKeteranganPulangAwal(String keteranganPulangAwal) {
        this.keteranganPulangAwal = keteranganPulangAwal;
    }

    public String getJamPulang() {
        return jamPulang;
    }

    public void setJamPulang(String jamPulang) {
        this.jamPulang = jamPulang;
    }

    public String getKeteranganTerlambat() {
        return keteranganTerlambat;
    }

    public void setKeteranganTerlambat(String keteranganTerlambat) {
        this.keteranganTerlambat = keteranganTerlambat;
    }

    public String getFotoMasuk() {
        return fotoMasuk;
    }

    public void setFotoMasuk(String fotoMasuk) {
        this.fotoMasuk = fotoMasuk;
    }

    public String getFotoPulang() {
        return fotoPulang;
    }

    public void setFotoPulang(String fotoPulang) {
        this.fotoPulang = fotoPulang;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusAbsen() {
        return statusAbsen;
    }

    public void setStatusAbsen(String statusAbsen) {
        this.statusAbsen = statusAbsen;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getJamShift() {
        return jamShift;
    }

    public void setJamShift(String jamShift) {
        this.jamShift = jamShift;
    }
}
