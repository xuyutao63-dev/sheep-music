package com.example.sheepmusic.config;

import com.example.sheepmusic.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /*密码加密器 Bean*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（因为使用JWT）
            .csrf().disable()
            // 允许跨域
            .cors()
            
            .and()
            // 配置Session管理为无状态
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            .and()
            // 配置请求权限
            .authorizeRequests()
            // 放行 OPTIONS 请求（CORS 预检）
            .antMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
            // 放行登录注册接口
            .antMatchers("/auth/**").permitAll()
            // 放行Swagger文档
            .antMatchers("/doc.html", "/swagger-resources/**", "/webjars/**", "/v2/api-docs").permitAll()
            // 放行静态资源
            .antMatchers("/static/**").permitAll()
            // 放行热门搜索接口（公开接口）
            .antMatchers(org.springframework.http.HttpMethod.GET, "/api/user/search-history/hot").permitAll()
            // 管理员接口（只有管理员可访问）
            .antMatchers("/admin/**").hasRole("ADMIN")
            // 其他请求需要认证
            .anyRequest().authenticated()
            
            .and()
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

