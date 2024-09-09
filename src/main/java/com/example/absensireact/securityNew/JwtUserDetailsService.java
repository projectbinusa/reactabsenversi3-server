package com.example.absensireact.securityNew;



import com.example.absensireact.model.Admin;
import com.example.absensireact.model.OrangTua;
import com.example.absensireact.model.SuperAdmin;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.OrangTuaRepository;
import com.example.absensireact.repository.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.model.UserModel;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userDao;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private OrangTuaRepository orangTuaRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public UserModel save(UserModel user) {
        try {
            if (user.getUsername() != null && user.getPassword() != null && user.getPassword().length() >= 8) {
                if (userDao.findByUsername(user.getUsername()) == null) {
                    UserModel newUser = new UserModel();
                    newUser.setUsername(user.getUsername());
                    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
                    newUser.setRole(user.getRole());
                    return userDao.save(newUser);
                } else {
                    throw new IllegalArgumentException("Username Telah Di Gunakan");
                }
            } else {
                throw new IllegalArgumentException("Password Harus Lebih Dari 8 Karakter");
            }
        } catch (Exception e) {
            // Catch any other exceptions and convert them to a meaningful error message
            throw new IllegalArgumentException("Pendaftaran Gagal: " + e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> optionalUser = userDao.findByUsername(username);
        Optional<SuperAdmin> optionalSuperAdmin = superAdminRepository.findByUsername(username);
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        Optional<OrangTua> optionalOrangTua = orangTuaRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            List<SimpleGrantedAuthority> roles = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
            );
            return new User(user.getUsername(), user.getPassword(), roles);
        } else if (optionalAdmin.isPresent()) {
            Admin user = optionalAdmin.get();
            List<SimpleGrantedAuthority> roles = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
            );
            return new User(user.getUsername(), user.getPassword(), roles);
        }else if (optionalSuperAdmin.isPresent()) {
            SuperAdmin user = optionalSuperAdmin.get();
            List<SimpleGrantedAuthority> roles = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
            );
            return new User(user.getUsername(), user.getPassword(), roles);
        }else if (optionalOrangTua.isPresent()) {
            OrangTua user = optionalOrangTua.get();
            List<SimpleGrantedAuthority> roles = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
            );
            return new User(user.getNama(), user.getPassword(), roles);
        } else {
            throw new UsernameNotFoundException("User not found with username1: " + username);
        }
    }

}