package com.example.absensireact.dto;


import com.example.absensireact.model.UserModel;

public class LemburDTO {
    private Long id;
    private String tanggalLebur;
    private String jamMulai;
    private String jamSelesai;
    private String keteranganLembur;
    private UserModel user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTanggalLebur() {
        return tanggalLebur;
    }

    public void setTanggalLebur(String tanggalLebur) {
        this.tanggalLebur = tanggalLebur;
    }

    public String getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(String jamMulai) {
        this.jamMulai = jamMulai;
    }

    public String getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(String jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    public String getKeteranganLembur() {
        return keteranganLembur;
    }

    public void setKeteranganLembur(String keteranganLembur) {
        this.keteranganLembur = keteranganLembur;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
