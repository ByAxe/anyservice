package com.anyservice.entity.api;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Root class for all entities in application
 * <p>
 * Contains identifier {@link UUID} AND special type for conversion jsonb into needed class object
 */
@MappedSuperclass
@Data
@AllArgsConstructor
@SuperBuilder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class EntityWithUUID {

    @Id
    @Type(type = "pg-uuid")
    private UUID uuid;

    public EntityWithUUID() {
        uuid = UUID.randomUUID();
    }
}
