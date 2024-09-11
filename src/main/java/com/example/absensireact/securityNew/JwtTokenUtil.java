package com.example.absensireact.securityNew;

import com.example.absensireact.detail.AdminDetail;
import com.example.absensireact.detail.OrangTuaDetail;
import com.example.absensireact.detail.SuperAdminDetail;
import com.example.absensireact.detail.UserDetail;
import com.example.absensireact.model.UserModel;
import com.example.absensireact.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60; // 1 minggu

    @Autowired
    private UserRepository userRepository;

    private String secret = "Absensi";

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject); // 'Subject' digunakan untuk menyimpan username/email
    }

    public UserModel getUserFromToken(String token) {
        Long userId = getIdFromToken(token); // Ambil id dari token
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    public Long getIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("id", Long.class); // Ambil id dari klaim token
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims); // Ambil klaim sesuai yang diinginkan
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody(); // Parsir token menggunakan secret
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public String generateToken(UserDetails userDetails ) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof SuperAdminDetail) {
            claims.put("id", ((SuperAdminDetail) userDetails).getId());
        }

        if (userDetails instanceof AdminDetail) {
            claims.put("id", ((AdminDetail) userDetails).getId());
        }
        if (userDetails instanceof OrangTuaDetail) {
            claims.put("id", ((OrangTuaDetail) userDetails).getId());
        }
        if (userDetails instanceof UserDetail) {
            claims.put("id", ((UserDetail) userDetails).getId());
        }

        Collection<? extends GrantedAuthority> role = userDetails.getAuthorities();

        if (role.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            claims.put("isAdmin", true);
        }
        if (role.contains(new SimpleGrantedAuthority("ROLE_USER"))){
            claims.put("isUser", true);
        }
        return doGenerateToken(claims, userDetails.getUsername());
    }

    // Proses pembuatan token
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Set klaim dengan id dan username
                .setSubject(subject) // Set subject dengan username
                .setIssuedAt(new Date(System.currentTimeMillis())) // Waktu pembuatan token
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)) // Waktu kedaluwarsa token
                .signWith(SignatureAlgorithm.HS512, secret) // Menandatangani token dengan secret
                .compact(); // Membuat token final
    }

    // Validasi token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token); // Ambil email dari token
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Validasi email dan cek apakah token sudah kadaluarsa
    }
}
