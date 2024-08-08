package com.example.absensireact.dto;

public class OrangTuaDTO {
    private Long id;
    private String email;
    private String nama;
    private String imageOrtu;
    private String role;
    private String password;
    private Long userId;

    public OrangTuaDTO() {
    }

    public OrangTuaDTO(Long id, String email, String nama, String imageOrtu, String role, String password, Long userId) {
        this.id = id;
        this.email = email;
        this.nama = nama;
        this.imageOrtu = imageOrtu;
        this.role = role;
        this.password = password;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
