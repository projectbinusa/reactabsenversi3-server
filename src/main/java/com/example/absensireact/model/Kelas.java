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
}
