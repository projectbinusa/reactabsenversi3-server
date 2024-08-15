package com.example.absensireact.repository;

import com.example.absensireact.model.OrangTua;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrangTuaRepository extends JpaRepository<OrangTua, Long> {

    @Query(value = "SELECT * FROM orang_tua WHERE id_admin = :idAdmin", nativeQuery = true)
    List<OrangTua> findAllByAdmin(Long idAdmin);

    @Query(value = "SELECT * FROM orang_tua WHERE email = :email", nativeQuery = true)
    Optional<OrangTua> findByEmail (String email);

    Boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM orang_tua WHERE nama = :username", nativeQuery = true)
    Optional<OrangTua> findByUsername (String username);
//    Boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM orang_tua WHERE nama = :namaUser" , nativeQuery = true)
    Optional<OrangTua> findByWaliMurid(String namaUser);
}

