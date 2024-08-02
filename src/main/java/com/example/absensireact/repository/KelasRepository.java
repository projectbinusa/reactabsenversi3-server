package com.example.absensireact.repository;

import com.example.absensireact.model.Kelas;
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
}
