package com.example.absensireact.dto;

import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Organisasi;

public class KelasDTO {

    private Long id;
    private String namaKelas;
    private Organisasi organisasiId;

    private Admin admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public Organisasi getOrganisasiId() {
        return organisasiId;
    }

    public void setOrganisasiId(Organisasi organisasiId) {
        this.organisasiId = organisasiId;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}
