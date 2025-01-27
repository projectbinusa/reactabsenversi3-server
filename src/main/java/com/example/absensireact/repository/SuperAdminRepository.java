package com.example.absensireact.repository;

import com.example.absensireact.model.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperAdminRepository  extends JpaRepository<SuperAdmin , Long> {

    @Query(value = "SELECT * FROM super_admin WHERE email = :email", nativeQuery = true)
    Optional<SuperAdmin> findByEmail (String email);


    @Query(value = "SELECT * FROM super_admin WHERE username = :username", nativeQuery = true)
    Optional<SuperAdmin> findByUsername (String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    Optional<SuperAdmin> findByEmailAndUsername(String email, String username);


}
