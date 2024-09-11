package com.example.absensireact.service;



import com.example.absensireact.detail.*;
import com.example.absensireact.model.*;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.OrangTuaRepository;
import com.example.absensireact.repository.SuperAdminRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.securityNew.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService  {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    CustomUserDetails customUserDetails;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    SuperAdminRepository superAdminRepository;

    @Autowired
    OrangTuaRepository orangTuaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;




    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> userOptional = userRepository.findByEmail(username);
        if (userOptional.isPresent() && userOptional.get().getEmail().equals(username)) {
            return UserDetail.buidUser(userOptional.get());
        }

        Optional<Admin> adminOptional = adminRepository.findByEmail(username);
        if (adminOptional.isPresent() && adminOptional.get().getEmail().equals(username)) {
            return AdminDetail.buildAdmin(adminOptional.get());
        }

        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findByEmail(username);
        if (superAdminOptional.isPresent() && superAdminOptional.get().getEmail().equals(username)) {
            return SuperAdminDetail.buildSuperAdmin(superAdminOptional.get());
        }

        Optional<OrangTua> orangTuaOptional = orangTuaRepository.findByEmail(username);
        if (orangTuaOptional.isPresent() && orangTuaOptional.get().getEmail().equals(username)) {
            return OrangTuaDetail.buildOrangTua(orangTuaOptional.get());
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }


    public Map<String, Object> authenticate(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        UserDetails userDetails = loadUserByUsername(email);

        // Password is checked without altering the email case
        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Email atau password yang Anda masukkan salah");
        }

        // Generate token after successful authentication
        String token = jwtTokenUtil.generateToken(userDetails );

        Map<String, Object> response = new HashMap<>();
        response.put("data", userDetails);
        response.put("token", token);
        return response;
    }
}