package com.anyservice.web.security;

import com.anyservice.web.security.exceptions.TokenNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtUtil jwtUtil;

    @Value("${security.jwt.header}")
    private String jwtHeader;

    @Value("${security.inner.header}")
    private String innerHeader;

    @Value("${security.inner.key}")
    private String innerKey;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super("/**");
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String JWT = request.getHeader(jwtHeader);
        String BE = request.getHeader(innerHeader);

        if (authentication == null) {
            return !StringUtils.isEmpty(JWT) || !StringUtils.isEmpty(BE);
        }
        return false;
    }

    /**
     * Main auth method:
     * parses the token with help of {@link JwtUtil#parseToken(String)}
     *
     * @param request  of a user
     * @param response to user
     * @return authenticated user
     * @throws AuthenticationException if something goes wrong - throw everything up
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final String token = Optional.ofNullable(request.getHeader(jwtHeader))
                .orElseThrow(() -> new TokenNotFoundException(messageSource.getMessage("jwt.authentication.filter.attempt.authentication",
                        null, LocaleContextHolder.getLocale())));

        // Parse token and get user from it
        Authentication authentication = jwtUtil.parseToken(token);

        // Authenticate this user
        return getAuthenticationManager().authenticate(authentication);
    }

    /**
     * Called when user authenticated successfully
     *
     * @param request    of a user
     * @param response   to user
     * @param chain      further filters of a request
     * @param authResult result of an authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        if (authResult.getPrincipal() != null)
            super.successfulAuthentication(request, response, chain, authResult);

        chain.doFilter(request, response);
    }
}
