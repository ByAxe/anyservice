package com.anyservice.web.security;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.service.api.IUserService;
import com.anyservice.service.user.UserHolder;
import com.anyservice.web.security.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityHelper {

    private final UserHolder userHolder;
    private final IUserService userService;
    private final UserDetailed innerUser;
    private final MessageSource messageSource;

    @Value("${security.inner.key}")
    private String innerKey;

    public SecurityHelper(UserHolder userHolder, IUserService userService,
                          UserDetailed innerUser, MessageSource messageSource) {
        this.userHolder = userHolder;
        this.userService = userService;
        this.innerUser = innerUser;
        this.messageSource = messageSource;
    }

    /**
     * Make user our "Request bean" to be able to access it in later stages of a query,
     * after passing the security chain
     *
     * @throws UserNotFoundException if no user was found by passed uuid (userName stores user uuid)
     */
    public void initializeUserInRequestScope() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // We don't need to keep anonymous user data
        if (authentication.getAuthorities().stream().anyMatch(t -> "ROLE_ANONYMOUS".equals(t.getAuthority()))) {
            return;
        }

        UserDetailed user;
        final String uuid = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Check if it's inner user
        if (innerKey.equals(uuid)) {
            user = innerUser;
        } else {
            // Find user by id, or throw an exception if we don't find it
            user = userService.findById(UUID.fromString(uuid))
                    .orElseThrow(() -> new UserNotFoundException(
                            messageSource.getMessage("jwt.authentication.provider.retrieve.user.not.found",
                                    null, LocaleContextHolder.getLocale())));
        }

        // Set user to user holder, to be able to access it during later stages of request
        userHolder.setUser(user);
    }

}
