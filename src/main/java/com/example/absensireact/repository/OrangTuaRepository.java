package com.example.absensireact.repository;

import com.example.absensireact.model.OrangTua;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrangTuaRepository extends JpaRepository<OrangTua, Long> {

    @Query(value = "SELECT * FROM orang_tua WHERE id_super_admin = :idSuperAdmin", nativeQuery = true)
    List<OrangTua> findAllBySuperAdmin(Long idSuperAdmin);
}
