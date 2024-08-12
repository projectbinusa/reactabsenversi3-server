package com.example.absensireact.dto;

import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Jabatan;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.Shift;
import com.example.absensireact.model.SuperAdmin;

public class UserDTO {
    private Long id;

    private String email;
    private String password;
    private String username;
    private String fotoUser;
    private String startKerja;
    private String lamaKerja;
    private String statusKerja;
    private Organisasi organisasi;
    private Jabatan jabatan;
    private Shift shift;
    private Admin admin;
    private Kelas kelas;
    private OrangTua orangTua;
    private SuperAdmin superAdmin;

    private String role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFotoUser() {
        return fotoUser;
    }

    public void setFotoUser(String fotoUser) {
        this.fotoUser = fotoUser;
    }

    public String getStartKerja() {
        return startKerja;
    }

    public void setStartKerja(String startKerja) {
        this.startKerja = startKerja;
    }

    public String getLamaKerja() {
        return lamaKerja;
    }

    public void setLamaKerja(String lamaKerja) {
        this.lamaKerja = lamaKerja;
    }

    public String getStatusKerja() {
        return statusKerja;
    }

    public void setStatusKerja(String statusKerja) {
        this.statusKerja = statusKerja;
    }

    public Organisasi getOrganisasi() {
        return organisasi;
    }

    public void setOrganisasi(Organisasi organisasi) {
        this.organisasi = organisasi;
    }

    public Jabatan getJabatan() {
        return jabatan;
    }

    public void setJabatan(Jabatan jabatan) {
        this.jabatan = jabatan;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Kelas getKelas() {
        return kelas;
    }

    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }

    public OrangTua getOrangTua() {
        return orangTua;
    }

    public void setOrangTua(OrangTua orangTua) {
        this.orangTua = orangTua;
    }

    public SuperAdmin getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(SuperAdmin superAdmin) {
        this.superAdmin = superAdmin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
