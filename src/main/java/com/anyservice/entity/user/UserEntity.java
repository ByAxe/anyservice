package com.anyservice.entity.user;

import com.anyservice.entity.api.EntityWithUUID;
import com.anyservice.entity.file.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;

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

    @Column(name = "dt_create", nullable = false)
    private OffsetDateTime dtCreate;

    @Column(name = "dt_update", nullable = false)
    private OffsetDateTime dtUpdate;

    @Column(name = "password_update_date", nullable = false)
    private OffsetDateTime passwordUpdateDate;

    @Column(name = "legal_status")
    private String legalStatus;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "is_legal_status_verified")
    private Boolean isLegalStatusVerified;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String role;

    private String description;
    private String address;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Contacts contacts;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private Initials initials;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "country")
    private CountryEntity country;

    @ManyToOne(fetch = LAZY, cascade = REMOVE)
    @JoinColumn(name = "photo")
    private FileEntity photo;

    @ManyToMany(fetch = LAZY, cascade = REMOVE)
    @JoinTable(name = "users_files",
            joinColumns = @JoinColumn(name = "user_uuid"),
            inverseJoinColumns = @JoinColumn(name = "file_uuid")
    )
    private List<FileEntity> documents;
}
