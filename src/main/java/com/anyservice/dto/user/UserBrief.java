package com.anyservice.dto.user;

import com.anyservice.dto.api.APrimary;
import com.anyservice.entity.Initials;
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
}
