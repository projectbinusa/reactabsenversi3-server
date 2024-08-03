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
    private User user;

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

    public Notifications(){

    }

    public Notifications(Long id, User user, String message, String tempatAcara, String namaAcara, Date tanggalAcara, Date createdAt) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.tempatAcara = tempatAcara;
        this.namaAcara = namaAcara;
        this.tanggalAcara = tanggalAcara;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
}
