package com.anyservice.entity;

import com.anyservice.entity.api.EntityWithUUID;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users", schema = "anyservice")
public class UserEntity extends EntityWithUUID {

    @Column(name = "dt_create")
    private OffsetDateTime dtCreate;

    @Column(name = "dt_update")
    private OffsetDateTime dtUpdate;

    private String description;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Contacts contacts;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Initials initials;

    @Column(name = "legal_status")
    private String legalStatus;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "is_legal_status_verified")
    private Boolean isLegalStatusVerified;

    @Column(name = "user_name")
    private String userName;

    private String password;
}
