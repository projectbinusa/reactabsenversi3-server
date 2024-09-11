package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.exception.RelationExistsException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Kelas;
import com.example.absensireact.model.Organisasi;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.KelasRepository;
import com.example.absensireact.repository.OrganisasiRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.KelasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KelasImpl implements KelasService {

    @Autowired
    private KelasRepository kelasRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganisasiRepository organisasiRepository;

    @Autowired
    private UserRepository userRepository;

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
    public Kelas editKelasById(Long id, Kelas updateKelas) {
        // Fetch the class that is being updated
        Kelas kelas = kelasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id kelas tidak ditemukan : " + id));

        // Check if there is an existing class with the same name under the same admin
        Optional<Kelas> existingKelas = kelasRepository.findByNamaKelasAndAdmin(updateKelas.getNamaKelas(), kelas.getAdmin());

        // If another class with the same name exists, throw an exception
        if (existingKelas.isPresent() && !existingKelas.get().getId().equals(id)) {
            throw new NotFoundException("Kelas dengan nama yang sama sudah ada di bawah admin ini");
        }

        // Proceed with updating the class details
        kelas.setNamaKelas(updateKelas.getNamaKelas());

        if (updateKelas.getOrganisasi() != null) {
            kelas.setOrganisasi(updateKelas.getOrganisasi());
        }

        return kelasRepository.save(kelas);
    }

    @Override
    public Kelas tambahKelas(Kelas kelas, Long idOrganisasi, Long idAdmin) {
        Organisasi organisasi = organisasiRepository.findById(idOrganisasi)
                .orElseThrow(() -> new NotFoundException("Organisasi tidak ditemukan"));
        Admin admin = adminRepository.findById(idAdmin)
                .orElseThrow(() -> new NotFoundException("ID admin tidak ditemukan"));

        Optional<Kelas> existingKelas = kelasRepository.findByNamaKelasAndAdmin(kelas.getNamaKelas(), admin);
        if (existingKelas.isPresent()) {
            throw new NotFoundException("Kelas dengan nama yang sama sudah ada di bawah admin ini");
        }

        kelas.setOrganisasi(organisasi);
        kelas.setAdmin(admin);
        kelas.setNamaKelas(kelas.getNamaKelas());

        return kelasRepository.save(kelas);
    }

    @Override
    public void deleteKelas(Long id) {
        if (checkIfHasRelations(id)) {
            throw new RelationExistsException("Data ini sudah memiliki relasi dan tidak dapat dihapus");
        } else {
            kelasRepository.deleteById(id);
        }
    }


    @Override
    public boolean checkIfHasRelations(Long kelasId) {
        // Use the repository method to check if there are users associated with the kelas
        List<UserModel> users = userRepository.findUsersByKelas(kelasId);
        return !users.isEmpty(); // Return true if there are any users associated, otherwise false
    }


}
