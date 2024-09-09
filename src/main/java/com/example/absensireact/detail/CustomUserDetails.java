package com.example.absensireact.detail;

import com.example.absensireact.model.Admin;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.SuperAdmin;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.OrangTuaRepository;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.securityNew.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetails  implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    SuperAdminRepository superAdminRepository;

    @Autowired
    OrangTuaRepository orangTuaRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();
            return UserDetail.buidUser(user);
        }

        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return AdminDetail.buildAdmin(admin);
        }

        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findByUsername(username);
        if (superAdminOptional.isPresent()) {
            SuperAdmin superAdmin = superAdminOptional.get();
            return SuperAdminDetail.buildSuperAdmin(superAdmin);
        }

        Optional<OrangTua> orangTuaOptional = orangTuaRepository.findByUsername(username);
        if (orangTuaOptional.isPresent()) {
            OrangTua orangTua = orangTuaOptional.get();
            return OrangTuaDetail.buildOrangTua(orangTua);
        }

        throw new UsernameNotFoundException("User Not Found with username2: " + username);
    }


}
