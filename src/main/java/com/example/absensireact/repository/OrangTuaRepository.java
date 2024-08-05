package com.example.absensireact.repository;

import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.OrangTua;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrangTuaRepository extends JpaRepository<OrangTua, Long> {

    @Query(value = "SELECT * FROM orang_tua WHERE id_user = :idUser", nativeQuery = true)
    List<OrangTua> findAllOrangTua(Long idUser);
}
