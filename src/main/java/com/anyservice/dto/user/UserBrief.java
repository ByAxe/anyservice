package com.anyservice.dto.user;

import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.api.APrimary;
import com.anyservice.entity.user.Initials;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserBrief extends APrimary {
    private String userName;
    private Initials initials;
    private UserState state;
    private UserRole role;
}
