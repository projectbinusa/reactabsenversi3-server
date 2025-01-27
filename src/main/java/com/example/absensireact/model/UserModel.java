package com.example.absensireact.model;


import javax.persistence.*;

@Entity
@Table(name = "user")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password" , unique = true)
    private String password;

    @Column(name = "username")
    private  String username;

    @Column(name = "fotoUser")
    private String fotoUser;

    @Column(name = "startKerja")
    private String startKerja;

    @Column(name = "statusKerja")
    private String statusKerja;

    @ManyToOne
    @JoinColumn(name = "idOrganisasi")
    private Organisasi organisasi;

    @Column(name = "Status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_shift")
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "idAdmin")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "idKelas")
    private Kelas kelas;

    @OneToOne
    @JoinColumn(name = "idOrangTua")
    private OrangTua orangTua;

    @ManyToOne
    @JoinColumn(name = "idSuperAdmin")
    private SuperAdmin superAdmin;

    @Column(name = "role")
    private String role;

    @Column(name = "deleted")
    private Integer deleted;

    public UserModel(){

    }

    public UserModel(Long id, String email, String password, String username, String fotoUser, String startKerja, String statusKerja, Organisasi organisasi, String status, Shift shift, Admin admin, Kelas kelas, OrangTua orangTua, SuperAdmin superAdmin, String role, Integer deleted) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.fotoUser = fotoUser;
        this.startKerja = startKerja;
        this.statusKerja = statusKerja;
        this.organisasi = organisasi;
        this.status = status;
        this.shift = shift;
        this.admin = admin;
        this.kelas = kelas;
        this.orangTua = orangTua;
        this.superAdmin = superAdmin;
        this.role = role;
        this.deleted = deleted;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFotoUser() {
        return fotoUser;
    }

    public void setFotoUser(String fotoUser) {
        this.fotoUser = fotoUser;
    }

    public String getStartKerja() {
        return startKerja;
    }

    public void setStartKerja(String startKerja) {
        this.startKerja = startKerja;
    }



    public String getStatusKerja() {
        return statusKerja;
    }

    public void setStatusKerja(String statusKerja) {
        this.statusKerja = statusKerja;
    }

    public Organisasi getOrganisasi() {
        return organisasi;
    }

    public void setOrganisasi(Organisasi organisasi) {
        this.organisasi = organisasi;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Kelas getKelas() {
        return kelas;
    }

    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }

    public SuperAdmin getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(SuperAdmin superAdmin) {
        this.superAdmin = superAdmin;
    }

    public OrangTua getOrangTua() {
        return orangTua;
    }

    public void setOrangTua(OrangTua orangTua) {
        this.orangTua = orangTua;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
