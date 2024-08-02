package com.example.absensireact.model;

import javax.persistence.*;

@Table(name = "kelas")
@Entity
public class Kelas {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "namaKelas")
    private String namaKelas;

    @OneToOne
    @JoinColumn(name = "idOrganisasi")
    private Organisasi organisasi;

    @OneToOne
    @JoinColumn(name = "idAdmin")
    private Admin admin;

    public Kelas(){

    }

    public Kelas(Long id, String namaKelas, Organisasi organisasi, Admin admin) {
        this.id = id;
        this.namaKelas = namaKelas;
        this.organisasi = organisasi;
        this.admin = admin;
    }

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
}
