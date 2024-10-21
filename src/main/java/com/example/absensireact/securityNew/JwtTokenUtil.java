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

import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60; // 1 minggu

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret:Absensi}") // Menggunakan @Value untuk secret
    private String secret;

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public UserModel getUserFromToken(String token) {
        Long userId = getIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    public Long getIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("id", Long.class);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes()) // Menggunakan kunci rahasia sebagai byte array
                .parseClaimsJws(token)
                .getBody(); // Menggunakan secret yang sama dengan saat membuat token
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof SuperAdminDetail) {
            claims.put("id", ((SuperAdminDetail) userDetails).getId());
            claims.put("email", ((SuperAdminDetail) userDetails).getEmail());
        } else if (userDetails instanceof AdminDetail) {
            claims.put("id", ((AdminDetail) userDetails).getId());
            claims.put("email", ((AdminDetail) userDetails).getEmail());
        } else if (userDetails instanceof OrangTuaDetail) {
            claims.put("id", ((OrangTuaDetail) userDetails).getId());
            claims.put("email", ((OrangTuaDetail) userDetails).getEmail());
        } else if (userDetails instanceof UserDetail) {
            claims.put("id", ((UserDetail) userDetails).getId());
            claims.put("email", ((UserDetail) userDetails).getEmail());
        }

        Collection<? extends GrantedAuthority> role = userDetails.getAuthorities();
        if (role.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            claims.put("isAdmin", true);
        } else if (role.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            claims.put("isUser", true);
        }

        String token = doGenerateToken(claims, userDetails.getUsername());

        System.out.println("Generated Token: " + token);
        System.out.println("Token Claims: " + claims);

        return token;
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        System.out.println("Secret: " + secret);

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes()) // Menggunakan secret sebagai byte array
                .compact();

        return token;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
