package com.example.absensireact.config;

import com.google.api.client.util.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;



import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.web.util.ContentCachingRequestWrapper;
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders( "AuthPrs", "X-Requested-With", "auth-tgh" ,"Content-Type", "Origin", "Authorization", "Accept", "Client-Security-Token", "Accept-Encoding")
                .exposedHeaders("Access-Control-Allow-Origin")
//                .allowCredentials(true)
                .maxAge(3600);
    }
    @Bean
    public Filter loggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
                long startTime = System.currentTimeMillis();
                try {
                    filterChain.doFilter(wrappedRequest, response);  // Gunakan wrappedRequest
                } finally {
                    long duration = System.currentTimeMillis() - startTime;

                    MDC.put("remoteAddr", wrappedRequest.getRemoteAddr());
                    MDC.put("method", wrappedRequest.getMethod());
                    MDC.put("requestUri", wrappedRequest.getRequestURI());
                    MDC.put("status", String.valueOf(response.getStatus()));
                    MDC.put("responseTime", duration + "ms");
                    MDC.put("payload", getRequestBody(wrappedRequest));  // Sekarang payload sudah tersedia

                    logger.info("Request completed.");
                    MDC.clear();
                }
            }
        };
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            return new String(buf, StandardCharsets.UTF_8);
        }
        return "";
    }

}