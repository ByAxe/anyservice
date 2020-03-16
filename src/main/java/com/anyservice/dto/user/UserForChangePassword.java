package com.anyservice.dto.user;

import com.anyservice.dto.api.APrimary;
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
public class UserForChangePassword extends APrimary {
    private String oldPassword;
    private String newPassword;
}
