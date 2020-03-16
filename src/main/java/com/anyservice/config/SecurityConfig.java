package com.anyservice.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.Filter;

/**
 * @author cdov
 */
@Configuration
@EnableWebSecurity
@ImportResource({"classpath*:/config/security-context.xml"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public FilterRegistrationBean registration2(Filter jwtAuthenticationFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(jwtAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }

}
