package com.example.absensireact.dto;

public class KelasDTO {

    private Long id;
    private String namaKelas;
    private Long organisasiId;

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

    public Long getOrganisasiId() {
        return organisasiId;
    }

    public void setOrganisasiId(Long organisasiId) {
        this.organisasiId = organisasiId;
    }
}
