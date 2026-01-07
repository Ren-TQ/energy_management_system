package com.campus.energy.config;

import com.campus.energy.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * ============================================
 * 分层架构 - 安全层（Security Layer）
 * ============================================
 * 
 * 架构说明：
 * 本类属于横切关注点架构中的安全层，负责系统安全配置
 * 
 * 安全层架构特点：
 * - 横切所有业务层
 * - 在请求到达Controller之前进行安全验证
 * - 使用过滤器链（Filter Chain）模式
 * 
 * 在本项目中的应用：
 * - JWT Token验证（JwtAuthenticationFilter）
 * - 请求授权配置
 * - CORS跨域配置
 * - 密码加密配置
 * 
 * 架构位置：
 * - 位于HTTP请求和Controller层之间
 * - 所有请求都会经过安全层验证
 * 
 * 数据流转：
 * HTTP请求 → JWT过滤器 → Security配置 → Controller层
 * ============================================
 * 
 * Spring Security 安全配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    // ============================================
    // 安全层组件：JWT认证过滤器
    // 负责从请求中提取和验证JWT Token
    // ============================================
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    
    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（使用JWT不需要CSRF保护）
                .csrf(AbstractHttpConfigurer::disable)
                
                // 启用CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 会话管理：使用无状态会话
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 公开接口：登录、注册、Swagger文档
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/doc.html/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // GET请求对所有登录用户开放
                        .requestMatchers(HttpMethod.GET, "/buildings/**", "/devices/**", 
                                "/energy-data/**", "/alerts/**", "/statistics/**").authenticated()
                        
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                
                // 允许H2控制台的frame
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                
                // 添加JWT过滤器
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 认证提供者
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

