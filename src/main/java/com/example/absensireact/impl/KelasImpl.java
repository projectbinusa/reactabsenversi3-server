package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.repository.KelasRepository;
import com.example.absensireact.service.KelasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KelasImpl implements KelasService {

    @Autowired
    private KelasRepository kelasRepository;

    @Override
    public List<Kelas>getAllKelas(){
        return kelasRepository.findAll();
    }

    @Override
    public Optional<Kelas> getKelasById(Long id){
        return kelasRepository.findById(id);
    }

    @Override
    public List<Kelas>getALlByOrganisasi(Long idOrganisasi){
       return kelasRepository.findAllByOrganisasi(idOrganisasi);
    }

    @Override
    public List<Kelas>getAllByIdAdmin(Long idAdmin){
        return kelasRepository.findByIdAdmin(idAdmin);
    }
    @Override
    public Kelas editKelasById(Long id, Kelas updateKelas){
        Kelas kelas = kelasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id kelas tidak ditemukan : " + id));
        kelas.setNamaKelas(updateKelas.getNamaKelas());
        if (updateKelas.getOrganisasi() != null){
            kelas.setOrganisasi(updateKelas.getOrganisasi());
        }
        return kelasRepository.save(kelas);
    }

    @Override
    public Kelas tambahKelas(Kelas kelas){
        kelas.setNamaKelas(kelas.getNamaKelas());
        kelas.setOrganisasi(kelas.getOrganisasi());
        return kelasRepository.save(kelas);
    }

    @Override
    public void deleteKelas(Long id) {
        kelasRepository.deleteById(id);
    }


}
