package com.example.absensireact.model;

import javax.persistence.*;

@Entity
@Table(name = "lokasi")
public class Lokasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLokasi;
    private String namaLokasi;
    private String alamat;
    @Column(name = "deleted")
    private Integer deleted;
    @OneToOne
    @JoinColumn(name = "idOrganisasi" )
    private Organisasi organisasi;

    @OneToOne
    @JoinColumn(name = "idAdmin" )
    private Admin admin;

    public Lokasi(){}

    public Lokasi(Long idLokasi, String namaLokasi, String alamat, Integer deleted, Organisasi organisasi, Admin admin) {
        this.idLokasi = idLokasi;
        this.namaLokasi = namaLokasi;
        this.alamat = alamat;
        this.deleted = deleted;
        this.organisasi = organisasi;
        this.admin = admin;
    }

    public Long getIdLokasi() {
        return idLokasi;
    }

    public void setIdLokasi(Long idLokasi) {
        this.idLokasi = idLokasi;
    }

    public String getNamaLokasi() {
        return namaLokasi;
    }

    public void setNamaLokasi(String namaLokasi) {
        this.namaLokasi = namaLokasi;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Organisasi getOrganisasi() {
        return organisasi;
    }

    public void setOrganisasi(Organisasi organisasi) {
        this.organisasi = organisasi;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
