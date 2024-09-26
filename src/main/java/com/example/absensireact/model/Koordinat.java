package com.example.absensireact.model;

import javax.persistence.*;

@Entity
@Table(name = "koordinat")
public class Koordinat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "southEastLat")
    private String southEastLat;

    @Column(name = "southEastLng")
    private String southEastLng;

    @Column(name = "southWestLat")
    private String southWestLat;

    @Column(name = "southWestLng")
    private String southWestLng;

    @Column(name = "northEastLat")
    private String northEastLat;

    @Column(name = "northEastLng")
    private String northEastLng;

    @Column(name = "northWestLat")
    private String northWestLat;

    @Column(name = "northWestLng")
    private String northWestLng;

    @OneToOne
    @JoinColumn(name = "idOrganiasi")
    private Organisasi organisasi;

    public Koordinat(){

    }
    public Koordinat(Long id, String southEastLat, String southEastLng, String southWestLat, String southWestLng, String northEastLat, String northEastLng, String northWestLat, String northWestLng, Organisasi organisasi) {
        this.id = id;
        this.southEastLat = southEastLat;
        this.southEastLng = southEastLng;
        this.southWestLat = southWestLat;
        this.southWestLng = southWestLng;
        this.northEastLat = northEastLat;
        this.northEastLng = northEastLng;
        this.northWestLat = northWestLat;
        this.northWestLng = northWestLng;
        this.organisasi = organisasi;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSouthEastLat() {
        return southEastLat;
    }

    public void setSouthEastLat(String southEastLat) {
        this.southEastLat = southEastLat;
    }

    public String getSouthEastLng() {
        return southEastLng;
    }

    public void setSouthEastLng(String southEastLng) {
        this.southEastLng = southEastLng;
    }

    public String getSouthWestLat() {
        return southWestLat;
    }

    public void setSouthWestLat(String southWestLat) {
        this.southWestLat = southWestLat;
    }

    public String getSouthWestLng() {
        return southWestLng;
    }

    public void setSouthWestLng(String southWestLng) {
        this.southWestLng = southWestLng;
    }

    public String getNorthEastLat() {
        return northEastLat;
    }

    public void setNorthEastLat(String northEastLat) {
        this.northEastLat = northEastLat;
    }

    public String getNorthEastLng() {
        return northEastLng;
    }

    public void setNorthEastLng(String northEastLng) {
        this.northEastLng = northEastLng;
    }

    public String getNorthWestLat() {
        return northWestLat;
    }

    public void setNorthWestLat(String northWestLat) {
        this.northWestLat = northWestLat;
    }

    public String getNorthWestLng() {
        return northWestLng;
    }

    public void setNorthWestLng(String northWestLng) {
        this.northWestLng = northWestLng;
    }

    public Organisasi getOrganisasi() {
        return organisasi;
    }

    public void setOrganisasi(Organisasi organisasi) {
        this.organisasi = organisasi;
    }
}
