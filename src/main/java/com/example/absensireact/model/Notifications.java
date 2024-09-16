package com.example.absensireact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "userId")
    private UserModel user;

    @OneToOne
    @JoinColumn(name = "adminId")
    private Admin admin;

    @Lob
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "tempatAcara")
    private String tempatAcara;

    @Column(name = "namaAcara")
    private String namaAcara;

    @Column(name = "tanggalAcara")
    private Date tanggalAcara;

    @Column(name = "cretedAT")
    private Date createdAt;

    @Column(name = "deleted")
    private Integer deleted;

    public Notifications(){

    }

    public Notifications(Long id, UserModel user, Admin admin, String message, String tempatAcara, String namaAcara, Date tanggalAcara, Date createdAt, Integer deleted) {
        this.id = id;
        this.user = user;
        this.admin = admin;
        this.message = message;
        this.tempatAcara = tempatAcara;
        this.namaAcara = namaAcara;
        this.tanggalAcara = tanggalAcara;
        this.createdAt = createdAt;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTempatAcara() {
        return tempatAcara;
    }

    public void setTempatAcara(String tempatAcara) {
        this.tempatAcara = tempatAcara;
    }

    public String getNamaAcara() {
        return namaAcara;
    }

    public void setNamaAcara(String namaAcara) {
        this.namaAcara = namaAcara;
    }



    public Date getTanggalAcara() {
        return tanggalAcara;
    }

    public void setTanggalAcara(Date tanggalAcara) {
        this.tanggalAcara = tanggalAcara;
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
