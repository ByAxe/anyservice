package com.anyservice.web.security;

import com.anyservice.core.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
public final class EnumGrantedAuthority implements GrantedAuthority {

    private final UserRole role;

    @Override
    public String getAuthority() {
        return role.name();
    }
}
