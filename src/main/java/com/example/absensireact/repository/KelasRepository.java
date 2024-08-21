package com.example.absensireact.repository;

import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.Organisasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KelasRepository extends JpaRepository<Kelas ,Long> {

    @Query(value = "SELECT * FROM kelas WHERE id_organisasi = :idOrganisasi", nativeQuery = true)
    List<Kelas>findAllByOrganisasi(Long idOrganisasi);

    @Query(value = "SELECT * FROM kelas WHERE id_admin = :idAdmin", nativeQuery = true)
    List<Kelas> findByIdAdmin(Long idAdmin);

    Optional<Kelas> findByNamaKelasAndAdmin(String namaKelas, Admin admin);

    Boolean existsByNamaKelas (String namaKelas);

    @Query(value = "SELECT * FROM kelas WHERE nama_kelas = :namaKelas" , nativeQuery = true)
    Optional<Kelas> findByNamaKelas(String namaKelas);


}
