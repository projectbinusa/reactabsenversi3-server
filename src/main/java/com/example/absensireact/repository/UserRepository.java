package com.example.absensireact.repository;


import com.example.absensireact.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel , Long> {

    @Query(value = "SELECT * FROM user WHERE id_jabatan = :idJabatan" , nativeQuery = true)
    List<UserModel> findByIdJabatan(Long idJabatan);

    @Query(value = "SELECT * FROM user WHERE id_jabatan = :idJabatan" , nativeQuery = true)
    Optional<UserModel> findByJabatanId(Long idJabatan);

    @Query(value = "SELECT * FROM user WHERE id_admin = :adminId" , nativeQuery = true)
    List<UserModel> findByadminIdAbsensi (Long adminId);

    @Query(value = "SELECT * FROM user WHERE id_shift = :idShift" , nativeQuery = true)
    List<UserModel> findByIdShift(Long idShift);


    @Query(value = "SELECT * FROM user WHERE email = :email", nativeQuery = true)
    Optional<UserModel> findByEmail(String email);
    Boolean existsByEmail(String email);
    @Query(value = "SELECT * FROM user WHERE id_admin = :idAdmin", nativeQuery = true)
    List<UserModel> findByIdAdmin (Long idAdmin);

    @Query(value = "SELECT * FROM user WHERE id_admin = :idAdmin AND id_kelas = :kelasId", nativeQuery = true)
    List<UserModel> findByIdAdminAndKelasId(Long idAdmin, Long kelasId);
    @Query(value = "SELECT * FROM user WHERE id_super_admin = :idSuperAdmin", nativeQuery = true)
    List<UserModel> findByIdSuperAdmin (Long idSuperAdmin);

    @Query(value = "SELECT * FROM user WHERE username = :username", nativeQuery = true)
    Optional<UserModel> findByUsername (String username);

//    UserModel findByUsername1(String username);

    @Query(value = "SELECT * FROM user WHERE id_kelas = :idKelas", nativeQuery = true)
    List<UserModel> findUsersByKelas(Long idKelas);

    List<UserModel> findByKelasId(Long kelasId );


    @Query(value = "SELECT * FROM user WHERE id_orang_tua + :idOrangTua" , nativeQuery = true)
    List<UserModel>findByIdOrangTua(Long idOrangTua);
    boolean existsByUsername(String username);

//    User findByEmail(String email);

//    @Query(value = "SELECT * FROM user WHERE nama_organisasi = :namaOrganisasi" , nativeQuery = true)
//    Optional<Organisasi> findByNamaOrganisasi(String namaOrganisasi);



}
