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
    };

    private static final String[] AUTH_AUTHORIZATION = {
            "/api/user/**",
            "/api/absensi/**",
            "/api/karyawan/**",
            "/api/admin/**",
            "/api/organisasi/**",
            "/api/lembur/**",
            "/api/izin/**",
            "/api/shift/**",
            "/api/lokasi/**",
            "/api/jabatan/**",
            "/api/notifications/**",
            "/api/tokens/**",
            "/api/admin/profile/editDetail/**",
            "/api/admin/profile/edit/**",
            "/api/admin/profile/upload/**",
            "/api/superadmin/**"
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
//                .antMatchers(AUTH_AUTHORIZATION).hasRole("ADMIN")
                .antMatchers(AUTH_AUTHORIZATION).hasAnyRole( "USER", "ADMIN", "SUPERADMIN", "WALI MURID")
                .anyRequest()
                .authenticated().and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}