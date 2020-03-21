package com.anyservice.web.security;

import com.anyservice.core.DateUtils;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.service.user.UserService;
import com.anyservice.web.security.dto.AuthDetails;
import com.anyservice.web.security.exceptions.UserNotFoundException;
import com.anyservice.web.security.exceptions.api.LoginException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final UserService userService;
    private final MessageSource messageSource;
    private final UserDetailed innerUser;

    @Value("${security.jwt.never}")
    private Long never;

    public JwtAuthenticationProvider(UserService userService, MessageSource messageSource, UserDetailed innerUser) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.innerUser = innerUser;
    }

    /**
     * Additional checks
     * We do not use it, because it is conducted a bit later than we need it
     *
     * @param userDetails                         user that was sent here from
     *                                            {@link #retrieveUser(String, UsernamePasswordAuthenticationToken)}
     * @param usernamePasswordAuthenticationToken user representation that was sent here from
     *                                            {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     * @throws AuthenticationException if something went wrong - let Spring framework do its job
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
            throws AuthenticationException {
    }

    /**
     * Get parsed user and convert it to SpringSecurity representation with all the needed flags
     *
     * @param s     token
     * @param token user representation that was sent here from {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     * @return SpringSecurity representation with all the needed flags
     * @throws AuthenticationException if something went wrong - let Spring framework do its job
     */
    @Override
    protected UserDetails retrieveUser(String s, UsernamePasswordAuthenticationToken token) throws AuthenticationException {
        final AuthDetails authDetails = (AuthDetails) token.getDetails();

        final String principal = String.valueOf(token.getPrincipal());
        final String credentials = String.valueOf(token.getCredentials());

        final UserDetailed user;
        final boolean isCredentialsNonExpired;

        // If it's an inner user - treat it in special way
        if (authDetails.isInner()) {
            user = innerUser;
            isCredentialsNonExpired = true;
        } else {
            // Otherwise, find user
            user = userService.findById(UUID.fromString(principal))
                    .orElseThrow(() -> new UserNotFoundException(messageSource.getMessage("jwt.authentication.provider.retrieve.user.not.found",
                            null, LocaleContextHolder.getLocale())));

            // Check whether passwordUpdateDates are equal
            final Long passwordUpdateDate = Long.valueOf((JwtUtil.safeExtractKey(authDetails.getBody(), "passwordUpdateDate")));

            final Long actual = Optional.ofNullable(user.getPasswordUpdateDate())
                    .map(DateUtils::convertOffsetDateTimeToMills)
                    .orElse(never);

            isCredentialsNonExpired = passwordUpdateDate.equals(actual);
        }

        // Get all his authorities
        final Collection<GrantedAuthority> authorities = Collections.singletonList(Optional.ofNullable(user)
                .map(UserDetailed::getRole)
                .map(EnumGrantedAuthority::new)
                .orElseThrow(() -> new LoginException(messageSource.getMessage("jwt.authentication.provider.retrieve.user",
                        null, LocaleContextHolder.getLocale()))));

        // Set all the needed flags
        final boolean isAccountNonExpired = true;

        final boolean isEnabled = user.getState().isEnabled();
        final boolean isAccountNonLocked = user.getState().isNonLocked();

        // Return SpringSecurity representation of a user
        return new org.springframework.security.core.userdetails.User(principal, credentials, isEnabled, isAccountNonExpired, isCredentialsNonExpired, isAccountNonLocked, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
