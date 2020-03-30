package com.anyservice.web.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token with manually chosen expiration time for given user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManualToken {
    private Long ttl;
    private String userName;
}
