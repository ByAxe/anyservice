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
