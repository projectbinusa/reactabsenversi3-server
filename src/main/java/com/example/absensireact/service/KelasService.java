package com.example.absensireact.service;


import com.example.absensireact.model.Kelas;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface KelasService {

    List<Kelas>getAllKelas();

    Optional<Kelas> getKelasById(Long id);

    List<Kelas> getALlByOrganisasi(Long idOrganisasi);

    List<Kelas>getAllByIdAdmin(Long idAdmin);

    Kelas editKelasById(Long id, Kelas updateKelas);

    Kelas tambahKelas(Kelas kelas);

    void deleteKelas(Long id);
}
