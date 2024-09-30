package com.example.absensireact.repository;

import com.example.absensireact.model.Koordinat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KoordinatRepository extends JpaRepository<Koordinat, Long> {

    Optional<Koordinat> findByAdminId(Long idAdmin);
    List<Koordinat> findAllByAdminId(Long idAdmin);
}