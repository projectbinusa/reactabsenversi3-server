package com.example.absensireact.repository;


import com.example.absensireact.model.Absensi;
import com.example.absensireact.model.Cuti;
import com.example.absensireact.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CutiRepository extends JpaRepository<Cuti ,Long> {

    List<Cuti> findByUserId(Long userId);

    List<Cuti> findByUser(UserModel user);

    Cuti findJabatanByid(UserModel user);
}