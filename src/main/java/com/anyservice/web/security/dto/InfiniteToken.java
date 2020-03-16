package com.anyservice.web.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfiniteToken {
    private Long ttl;
    private String userName;
}
