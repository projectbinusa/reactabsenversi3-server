package com.example.absensireact.repository;

import com.example.absensireact.model.Jabatan;
import com.example.absensireact.model.Koordinat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KoordinatRepository extends JpaRepository<Koordinat, Long> {

    List<Koordinat> findByIdAdmin (Long idAdmin);
}
