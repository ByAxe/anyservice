package com.anyservice.dto.user;

import com.anyservice.dto.api.AEssence;
import com.anyservice.entity.Initials;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBrief extends AEssence {
    private String userName;
    private Initials initials;
}
