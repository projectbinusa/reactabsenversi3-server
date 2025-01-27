package com.example.absensireact.service;

import com.example.absensireact.model.Kelas;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KelasService {

    List<Kelas> getAllKelas();

    Optional<Kelas> getKelasById(Long id);

    List<Kelas> getALlByOrganisasi(Long idOrganisasi);

    List<Kelas> getAllByIdAdmin(Long idAdmin);

    Kelas editKelasById(Long id, Kelas updateKelas);

    Kelas tambahKelas(Kelas kelas, Long idOrganisasi, Long idAdmin);

    Map<String, Boolean> deleteKelas(Long id);

    void DeleteKelasSementara(Long id);

    void PemulihanDataKelas(Long id);

    boolean checkIfHasRelations(Long id);
}
