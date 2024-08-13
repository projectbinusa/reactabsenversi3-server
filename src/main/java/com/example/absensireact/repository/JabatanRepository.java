package com.example.absensireact.repository;

import com.example.absensireact.model.Jabatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JabatanRepository extends JpaRepository<Jabatan, Long> {
    List<Jabatan> findByAdminId(Long adminId);


    @Query(value = "SELECT * FROM jabatan WHERE nama_jabatan = :namaJabatan" , nativeQuery = true)
    Optional<Jabatan> findByNamaStatus(String namaJabatan);
}
