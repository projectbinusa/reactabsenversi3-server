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

    public Kelas(){

    }

    public Kelas(Long id, String namaKelas, Organisasi organisasi) {
        this.id = id;
        this.namaKelas = namaKelas;
        this.organisasi = organisasi;
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
}
