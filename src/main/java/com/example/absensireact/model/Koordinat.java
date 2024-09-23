package com.example.absensireact.model;

import javax.persistence.*;

@Entity
@Table(name = "koordinat")
public class Koordinat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "southEastLat")
    private double southEastLat;

    @Column(name = "southEastLng")
    private double southEastLng;

    @Column(name = "southWestLat")
    private double southWestLat;

    @Column(name = "southWestLng")
    private double southWestLng;

    @Column(name = "northEastLat")
    private double northEastLat;

    @Column(name = "northEastLng")
    private double northEastLng;

    @Column(name = "northWestLat")
    private double northWestLat;

    @Column(name = "northWestLng")
    private double northWestLng;

    @OneToOne
    @JoinColumn(name = "idOrganiasi")
    private Organisasi organisasi;


    public Koordinat(Long id, double southEastLat, double southEastLng, double southWestLat, double southWestLng, double northEastLat, double northEastLng, double northWestLat, double northWestLng, Organisasi organisasi) {
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

    public double getSouthEastLat() {
        return southEastLat;
    }

    public void setSouthEastLat(double southEastLat) {
        this.southEastLat = southEastLat;
    }

    public double getSouthEastLng() {
        return southEastLng;
    }

    public void setSouthEastLng(double southEastLng) {
        this.southEastLng = southEastLng;
    }

    public double getSouthWestLat() {
        return southWestLat;
    }

    public void setSouthWestLat(double southWestLat) {
        this.southWestLat = southWestLat;
    }

    public double getSouthWestLng() {
        return southWestLng;
    }

    public void setSouthWestLng(double southWestLng) {
        this.southWestLng = southWestLng;
    }

    public double getNorthEastLat() {
        return northEastLat;
    }

    public void setNorthEastLat(double northEastLat) {
        this.northEastLat = northEastLat;
    }

    public double getNorthEastLng() {
        return northEastLng;
    }

    public void setNorthEastLng(double northEastLng) {
        this.northEastLng = northEastLng;
    }

    public double getNorthWestLat() {
        return northWestLat;
    }

    public void setNorthWestLat(double northWestLat) {
        this.northWestLat = northWestLat;
    }

    public double getNorthWestLng() {
        return northWestLng;
    }

    public void setNorthWestLng(double northWestLng) {
        this.northWestLng = northWestLng;
    }

    public Organisasi getOrganisasi() {
        return organisasi;
    }

    public void setOrganisasi(Organisasi organisasi) {
        this.organisasi = organisasi;
    }
}
