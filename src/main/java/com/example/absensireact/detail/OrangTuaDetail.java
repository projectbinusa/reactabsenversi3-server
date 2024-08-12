package com.example.absensireact.detail;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.OrangTua;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

public class OrangTuaDetail implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;

    private String email;

    private String password;
    private  String username;
    private  String image_ortu;

    private Admin orangTua;

    private String role;


    public OrangTuaDetail(Long id, String email, String password, String username, String image_ortu, Admin orangTua, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.image_ortu = image_ortu;
        this.orangTua = orangTua;
        this.role = role;
    }

    public static OrangTuaDetail buildOrangTua(OrangTua orangtua) {
        return new OrangTuaDetail(
                orangtua.getId(),
                orangtua.getEmail(),
                orangtua.getPassword(),
                orangtua.getNama(),
                orangtua.getImageOrtu(),
                orangtua.getAdmin(),
                "Wali Murid"
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage_ortu() {
        return image_ortu;
    }

    public void setImage_ortu(String image_ortu) {
        this.image_ortu = image_ortu;
    }

    public Admin getOrangTua() {
        return orangTua;
    }

    public void setOrangTua(Admin orangTua) {
        this.orangTua = orangTua;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    //    public OrangTua getOrangTua() {
//        return orangTua;
//    }
//
//    public void setOrangTua(OrangTua orangTua) {
//        this.orangTua = orangTua;
//    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
