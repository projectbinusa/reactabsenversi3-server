package com.example.absensireact.config;

import com.example.absensireact.securityNew.JwtAuthenticationEntryPoint;
import com.example.absensireact.securityNew.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebSecurity
@EnableSwagger2
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final PasswordEncoderConfig passwordEncoder;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    JwtAuthenticationEntryPoint unauthorizedHandler;
    @Bean
    public JwtRequestFilter authenticationJwtTokenFilter() {
        return new JwtRequestFilter();
    }
    @Autowired
    public WebSecurityConfig(PasswordEncoderConfig passwordEncoder, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                             @Qualifier("customUserDetails") UserDetailsService jwtUserDetailsService,
                             JwtRequestFilter jwtRequestFilter) {
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder.passwordEncoder());
    }


    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-ui/**",
            "/swagger-resources/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // API controller
            "/actuator/**",
            "/api/login",
            "/api/user/register",
            "/api/user/forgot_password",
            "/api/user/validasi_code",
            "/api/user/ubahPassByForgot",
            "/api/admin/forgot_password",
            "/api/admin/validasi_code",
            "/api/admin/ubahPassByForgot",
            "/api/superadmin/forgot_password",
            "/api/superadmin/validasi_code",
            "/api/superadmin/ubahPassByForgot",
            "/api/orangtua/forgot_password",
            "/api/admin/oragnisasi",
            "/api/admin/",
            "/api/admin/get",
            "/api/superadmin/register",
            "/api/absensi/masuk",
            "/api/absensi/pulang",
            "/api/absensi/checkAbsensi",
            "/api/absensi/izin",
            "/absensi/checkIzin/{userId}",
//            "/api/absensi/**",

    };

    private static final String[] AUTH_AUTHORIZATION = {
            "/api/user/**",
            "/api/karyawan/**",
            "/api/admin/**",
            "/api/organisasi/**",
            "/api/lembur/**",
            "/api/izin/**",
            "/api/orang-tua/**",
            "/api/shift/**",
            "/api/lokasi/**",
            "/api/jabatan/**",
            "/api/absensi/**",
            "/api/notifications/**",
            "/api/tokens/**",
            "/api/admin/profile/editDetail/**",
            "/api/admin/profile/edit/**",
            "/api/admin/profile/upload/**",
            "/api/superadmin/**",
//            "/api/absensi/**",
            "/api/absensi/export/absensi-bulanan-simpel",
            "/api/absensi/export/absensi-rekapan-perkaryawan",
            "/api/absensi/export/absensi-bulanan",
            "/api/absensi/export/bulanan-guru/by-kelas",
            "/api/absensi/export/absensi-mingguan",
            "/api/absensi/export/mingguan/by-kelas",
            "/api/absensi/rekap-mingguan",
            "/api/absensi/rekap-mingguan-per-kelas",
            "absensi/rekap-perkaryawan/export",
            "/api/absensi/get-absensi-bulan-simpel",
            "/api/absensi/get-absensi-bulan",
            "/api/absensi/by-tanggal",
            "/api/absensi/export/harian",
            "/api/absensi/getByUserId/{userId}",
            "/api/absensi/get",
            "/api/absensi/admin/{adminId}",
            "/api/absensi/check-alpha",
            "/api/absensi/getAll",
            "/api/absensi/getData/{id}",
            "/api/absensi/getizin/{userId}",
            "/api/absensi/izin-tengah-hari",
            "/api/absensi/update/{id}",
            "/api/absensi/delete/{id}",
            "/api/absensi/by-kelas/{kelasId}",
            "/api/export/absensi/by-kelas/{kelasId}",
            "/api/absensi/export/harian/by-kelas",
            "/api/absensi/export/bulanan/by-kelas",
            "/api/absensi/bulanan/kelas/{kelasId}",
            "/api/absensi/harian/by-kelas/{kelasId}",
            "/api/absensi/rekap/harian/all-kelas/per-hari",
            "/api/absensi/get-data/group-by-role",
            "/api/absensi/rekap/harian/by-kelas/{kelasId}",
            "/api/absensi/by-orang-tua/{orangTuaId}",
            "/api/absensi/izin/by-orangTua/{idOrangTua}"
    };

    @Bean 
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers(AUTH_AUTHORIZATION).hasAnyRole( "USER", "ADMIN", "SUPERADMIN", "WALI MURID")
                .anyRequest()
                .authenticated().and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}