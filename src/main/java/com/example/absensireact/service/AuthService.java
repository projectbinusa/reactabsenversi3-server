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
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private TelegramNotificationService telegramNotificationService;



    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> userOptional = userRepository.findByEmail(username);
        if (userOptional.isPresent()) {
            return UserDetail.buidUser(userOptional.get());
        }

        Optional<Admin> adminOptional = adminRepository.findByEmail(username);
        if (adminOptional.isPresent()) {
            return AdminDetail.buildAdmin(adminOptional.get());
        }

        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findByEmail(username);
        if (superAdminOptional.isPresent()) {
            return SuperAdminDetail.buildSuperAdmin(superAdminOptional.get());
        }

        Optional<OrangTua> orangTuaOptional = orangTuaRepository.findByEmail(username);
        if (orangTuaOptional.isPresent()) {
            return OrangTuaDetail.buildOrangTua(orangTuaOptional.get());
        }

        throw new UsernameNotFoundException("User tidak ditemukan dengan email: " + username);
    }

//    public Map<String, Object> authenticate(LoginRequest loginRequest) {
//        String email = loginRequest.getEmail();
//        UserDetails userDetails = loadUserByUsername(email);
//
//        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
//            String errorMessage = "❌ *Percobaan Login Gagal* ❌\n"
//                    + "📧 Email: " + email + "\n"
//                    + "🔑 Kesalahan: Password salah";
//
//            telegramNotificationService.sendErrorNotification(errorMessage);
//            throw new BadCredentialsException("Email atau password salah.");
//        }
//
//        // Generate token setelah berhasil login
//        String token = jwtTokenUtil.generateToken(userDetails);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("data", userDetails);
//        response.put("token", token);
//        return response;
//    }

    public Map<String, Object> authenticate(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        UserDetails userDetails = loadUserByUsername(email);

        // Password is checked without altering the email case
        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            String errorMessage = "❌ *Percobaan Login Gagal* ❌\n"
                    + "📧 Email: " + email + "\n"
                    + "🔑 Kesalahan: Password salah";

            telegramNotificationService.sendErrorNotificationLogin(errorMessage);
            throw new BadCredentialsException("Email atau password salah.");
         }

        // Generate token after successful authentication
        String token = jwtTokenUtil.generateToken(userDetails );

        Map<String, Object> response = new HashMap<>();
        response.put("data", userDetails);
        response.put("token", token);
        return response;
    }

}