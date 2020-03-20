package com.anyservice.web.security;

import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.service.user.IUserService;
import com.anyservice.service.user.UserHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityHelper {

    private final UserHolder userHolder;
    private final IUserService userService;

    public SecurityHelper(UserHolder userHolder, IUserService userService) {
        this.userHolder = userHolder;
        this.userService = userService;
    }

    public void initializeUserInRequestScope() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(t -> "ROLE_ANONYMOUS".equals(t.getAuthority())))
            return;

        UserDetailed user;
        final String uuid = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<UserDetailed> userDetailedOptional = userService.findById(UUID.fromString(uuid));

        user = userDetailedOptional.orElse(null);

        userHolder.setUser(user);
    }

    @Bean
    public UserDetailed getInnerUser() {
        return UserDetailed.builder()
                .uuid(UUID.randomUUID())
                .state(UserState.ACTIVE)
                .role(UserRole.ROLE_SUPER_ADMIN)
                .isVerified(true)
                .passwordUpdateDate(OffsetDateTime.now())
                .build();
    }

}
