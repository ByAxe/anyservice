package com.anyservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class UserEntity {

    @Id
    @Column(unique = true, nullable = false)
    private UUID uuid;

    @Column(name = "dt_create")
    private ZonedDateTime dtCreate;

    @Column(name = "dt_update")
    private ZonedDateTime dtUpdate;

    private String description;

    @Embedded
    private Contacts contacts;

    @Column(name = "legal_status")
    private String legalStatus;

    @Embeddable
    public class Contacts {
        private String phone;
        private String email;
        private String google;
        private String facebook;
    }
}
