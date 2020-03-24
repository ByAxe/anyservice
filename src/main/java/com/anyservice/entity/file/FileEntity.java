package com.anyservice.entity.file;

import com.anyservice.entity.api.EntityWithUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = "file_description")
@DynamicUpdate
@DynamicInsert
public class FileEntity extends EntityWithUUID {
    @Column(nullable = false)
    private String name;

    private Long size;
    private String extension;

    @Column(name = "dt_create", nullable = false)
    private OffsetDateTime dtCreate;

    private String state;

    @Column(nullable = false)
    private String type;
}
