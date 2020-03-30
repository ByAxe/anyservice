package com.anyservice.service.user;

import com.anyservice.dto.user.UserDetailed;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Request scope bean that holds user that is made a request
 */
@Data
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserHolderDelegate {
    private UserDetailed user;
}
