package com.anyservice.web.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Special object for login operation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Login {
    private String userName;
    private String password;
}
