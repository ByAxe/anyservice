package com.anyservice.web.security.interceptors;

import com.anyservice.web.security.SecurityHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    private final SecurityHelper securityHelper;

    public SecurityInterceptor(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        securityHelper.initializeUserInRequestScope();
        return super.preHandle(request, response, handler);
    }
}
