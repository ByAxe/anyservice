package com.anyservice.web.security;

import com.anyservice.core.DateUtils;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.service.api.IUserService;
import com.anyservice.service.user.UserHolder;
import com.anyservice.web.security.dto.AuthDetails;
import com.anyservice.web.security.exceptions.ClaimsExtractionException;
import com.anyservice.web.security.exceptions.TTLExpirationException;
import com.anyservice.web.security.exceptions.TokenNotFoundException;
import com.anyservice.web.security.exceptions.api.LoginException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Component
public class JwtUtil {
    private final IUserService userService;
    private final UserHolder userHolder;
    private final MessageSource messageSource;

    @Value("${security.jwt.key}")
    private String jwtKey;

    @Value("${security.ttl.period}")
    private Long ttlPeriod;

    @Value("${security.jwt.never}")
    private Long never;

    @Value("${spring.zone.offset.hours}")
    private int zone;

    @Value("${security.inner.key}")
    private String innerKey;

    public JwtUtil(IUserService userService, UserHolder userHolder, MessageSource messageSource) {
        this.userService = userService;
        this.userHolder = userHolder;
        this.messageSource = messageSource;
    }

    /**
     * Safely extract key from {@link Claims}
     *
     * @param body of token with data
     * @param key  key
     * @return value
     * @throws AuthenticationException if something goes wrong - throw everything up
     */
    public static String safeExtractKey(Claims body, String key) throws AuthenticationException {
        return Optional.ofNullable(body)
                .map(b -> b.get(key))
                .map(String::valueOf)
                .orElseThrow(() -> new ClaimsExtractionException("Exception extracting where key = " +
                        key + " from jwt token body"));
    }


    /**
     * Parse the token and return prepared and identified user
     *
     * @param tokenString token as string
     * @return identified user
     * @throws AuthenticationException if something goes wrong - throw everything up
     */
    public Authentication parseToken(String tokenString, String innerToken) throws AuthenticationException {
        AuthDetails authDetails = new AuthDetails();
        Claims body;
        String uuid;

        // Look for special token
        if (innerKey.equals(innerToken)) {
            authDetails.setInner(true);
            body = new DefaultClaims();
            uuid = innerToken;
        } else {

            // If the special token is null
            // Check if we have a usual one
            if (tokenString == null) {
                throw new TokenNotFoundException(
                        messageSource.getMessage("jwt.authentication.filter.attempt.authentication",
                                null, LocaleContextHolder.getLocale()));
            }

            // If we do have usual token - do all the regular procedures to it
            body = extractBodyFromToken(tokenString);

            validateToken(body);

            uuid = safeExtractKey(body, "uuid");
        }

        // Then create Spring Security required token
        final AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                uuid, // User name
                new StringBuffer(uuid).reverse().toString()
        );

        // Set claims to our bearer-object
        authDetails.setBody(body);

        // Set our bearer-object to spring security required one
        authenticationToken.setDetails(authDetails);

        return authenticationToken;
    }

    /**
     * Refresh the token
     *
     * @return new token
     * @throws AuthenticationException if something goes wrong - throw everything up
     */
    public String refreshToken() throws AuthenticationException {
        final UserDetailed user = userHolder.getUser();
        if (user == null)
            throw new IllegalStateException(messageSource.getMessage("jwt.util.refresh.token",
                    null, getLocale()));

        return generateToken(user);
    }

    /**
     * Generate and fill new token
     *
     * @param user for which token generated
     * @return token by itself
     */
    public String generateToken(UserDetailed user) {
        return generateToken(user, System.currentTimeMillis());
    }


    /**
     * Token creation for specified user
     *
     * @param user for which token generated
     * @param ttl  token life period
     * @return new token
     */
    public String generateInfiniteToken(UserDetailed user, Long ttl) {
        return generateToken(user, ttl);
    }

    private String generateToken(UserDetailed user, Long ttl) {
        Claims claims = Jwts.claims();

        OffsetDateTime passwordUpdateDate = Optional.ofNullable(user.getPasswordUpdateDate())
                .orElse(DateUtils.convertLongToOffsetDateTime(never, zone));

        // Save in token passwordUpdateDate in mills format
        // because of a problem with further conversions of OffsetDateTime
        long passwordUpdateDateInMills = DateUtils.convertOffsetDateTimeToMills(passwordUpdateDate);

        claims.put("uuid", user.getUuid());
        claims.put("ttl", ttl);
        claims.put("passwordUpdateDate", passwordUpdateDateInMills);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtKey)
                .compact();
    }

    /**
     * Getting all the roles for specified user
     *
     * @param uuid user id
     * @return collection of roles for this user
     */
    private Collection<? extends GrantedAuthority> getAuthorities(String uuid) {
        return Collections.singletonList(userService.findById(UUID.fromString(uuid))
                .map(UserDetailed::getRole)
                .map(EnumGrantedAuthority::new)
                .orElseThrow(() -> new LoginException(messageSource.getMessage("jwt.util.get.authorities",
                        null, getLocale()))));
    }

    /**
     * Checking the token validity
     *
     * @param body of token with data
     * @throws AuthenticationException if something goes wrong - throw everything up
     */
    private void validateToken(Claims body) throws AuthenticationException {

        final Long ttl = Long.valueOf(safeExtractKey(body, "ttl"));

        // Check ttl
        if ((ttl + ttlPeriod) < System.currentTimeMillis()) {
            throw new TTLExpirationException(messageSource.getMessage("jwt.util.validate.token",
                    null, getLocale()));
        }
    }

    /**
     * Extract the body from token
     *
     * @param tokenString token
     * @return body
     */
    private Claims extractBodyFromToken(final String tokenString) {
        return Jwts.parser()
                .setSigningKey(jwtKey)
                .parseClaimsJws(tokenString)
                .getBody();
    }
}
