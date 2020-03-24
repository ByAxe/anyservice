package com.anyservice.web.security.filters;

import com.anyservice.service.api.IUserService;
import com.anyservice.web.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

@Component
public class TokenFilter extends GenericFilterBean {

    private final CacheManager cacheManager;
    private final IUserService userService;
    private final JwtUtil jwtUtil;

    @Value("${security.jwt.header}")
    private String jwtHeader;

    @Value("${security.jwt.param.token.name}")
    private String paramTokenName;

    @Autowired
    public TokenFilter(CacheManager cacheManager, IUserService userService, JwtUtil jwtUtil) {
        this.cacheManager = cacheManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletRequest wrapper = httpRequest;
        if (httpRequest.getParameterMap().containsKey(paramTokenName)) {
            wrapper = new TokenRequestWrapper(httpRequest);
        }
        chain.doFilter(wrapper, response);
    }

    private class TokenRequestWrapper extends HttpServletRequestWrapper {
        public TokenRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * if  jwtHeader was queried:
         * 1) Find token in params
         * 2) Get user id from the map
         * 3) Get user via id
         * 4) Generate token for it
         * 5) Return as if it was stored in map
         *
         * @param name name of queried header
         * @return user token or any other header
         */
        @Override
        public String getHeader(String name) {
            final Cache uuidTokenMap = cacheManager.getCache("uuidTokenMap");

            if (!name.equalsIgnoreCase(jwtHeader) || uuidTokenMap == null) return super.getHeader(name);

            return Optional.ofNullable(this.getRequest().getParameter(paramTokenName))
                    .map(value -> uuidTokenMap.get(UUID.fromString(value), UUID.class))
                    .flatMap(userService::findById)
                    .map(jwtUtil::generateToken)
                    .orElse(super.getHeader(name));
        }

        /**
         * Add a custom header to enumeration {@link TokenRequestWrapper#getHeader(String)}
         *
         * @param name name of queried header
         * @return user token or any other header
         */
        @Override
        public Enumeration<String> getHeaders(String name) {
            if (name.equalsIgnoreCase(jwtHeader)) {
                return Collections.enumeration(Collections.singletonList(getHeader(name)));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> headerNames = Collections.list(super.getHeaderNames());
            if (super.getParameterMap().containsKey(paramTokenName)) {
                headerNames.add(jwtHeader);
            }
            return Collections.enumeration(headerNames);
        }
    }
}
