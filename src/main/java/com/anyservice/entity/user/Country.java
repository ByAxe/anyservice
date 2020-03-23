package com.anyservice.entity.user;

import com.anyservice.entity.api.EntityWithUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "countries")
@DynamicUpdate
@DynamicInsert
public class Country extends EntityWithUUID {
    private String country;
    private String alpha2;
    private String alpha3;
    private Integer number;
}
