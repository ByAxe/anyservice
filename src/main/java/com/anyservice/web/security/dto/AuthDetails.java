package com.anyservice.web.security.dto;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Special class that stores body of data in JWT, wrapping up standard body of {@link Claims}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDetails {
    private Claims body;
    private boolean isInner;
}
