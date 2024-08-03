package com.example.absensireact.dto;

import com.example.absensireact.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

public class NotificationsDTO {

    private Long id;
    private User user;

    private String message;
    private String tempatAcara;

    private String namaAcara;

    private Date tanggalAcara;

    private Date createdAt;

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
