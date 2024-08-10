package com.example.absensireact.model;

import javax.persistence.*;

@Table(name = "orang_tua")
@Entity
public class OrangTua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "nama")
    private String nama;

    @Column(name = "imageOrtu")
    private String imageOrtu;

    @Column(name = "role")
    private String role;

    @Column(name = "password", unique = true)
    private String password;

    @OneToOne
    @JoinColumn(name = "idAdmin")
    private Admin admin;

    public OrangTua() {
    }

    public OrangTua(Long id, String email, String nama, String imageOrtu, String role, String password, Admin admin) {
        this.id = id;
        this.email = email;
        this.nama = nama;
        this.imageOrtu = imageOrtu;
        this.role = role;
        this.password = password;
        this.admin = admin;
    }

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

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getImageOrtu() {
        return imageOrtu;
    }

    public void setImageOrtu(String imageOrtu) {
        this.imageOrtu = imageOrtu;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}