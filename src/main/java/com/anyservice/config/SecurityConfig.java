package com.anyservice.config;

import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.web.security.interceptors.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * @author cdov
 */
@Configuration
@EnableWebSecurity
@ImportResource({"classpath*:/config/security-context.xml"})
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor);
    }

    @Bean
    public FilterRegistrationBean registration2(Filter jwtAuthenticationFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(jwtAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Create special inner user instance with super role
     * That can access anything
     * (For development purposes)
     *
     * @return inner user instance
     */
    @Bean
    public UserDetailed innerUser() {
        return UserDetailed.builder()
                .uuid(UUID.randomUUID())
                .state(UserState.ACTIVE)
                .role(UserRole.ROLE_SUPER_ADMIN)
                .isVerified(true)
                .passwordUpdateDate(OffsetDateTime.now())
                .build();
    }
}
