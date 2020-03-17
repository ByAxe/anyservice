package com.anyservice.entity.user;

import com.anyservice.entity.api.EntityWithUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
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
@SuperBuilder
@Table(name = "users")
@DynamicUpdate
@DynamicInsert
public class UserEntity extends EntityWithUUID {

    @Column(name = "dt_create")
    private OffsetDateTime dtCreate;

    @Column(name = "dt_update")
    private OffsetDateTime dtUpdate;

    @Column(name = "password_update_date")
    private OffsetDateTime passwordUpdateDate;

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
    private String description;
    private String address;
    private String state;
    private String role;

}
