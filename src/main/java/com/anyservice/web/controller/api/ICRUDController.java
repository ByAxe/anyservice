package com.anyservice.web.controller.api;

import com.anyservice.dto.api.APrimary;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Main interface defining basic CRUD methods for all "classic" Spring-Controllers
 * <p>
 * DTOs as {@link BRIEF} or {@link DETAILED} used as different representations
 * of the same entity/object, stored in database
 * <p>
 * For the method description watch -> {@link com.anyservice.service.api.ICRUDService}
 *
 * @param <BRIEF>    DTO marked with {@link com.anyservice.dto.api.Brief} and extending {@link APrimary}
 * @param <DETAILED> DTO marked with {@link com.anyservice.dto.api.Detailed} and extending {@link APrimary}
 * @param <ID>       Class used as identifier of target {@link BRIEF} and {@link DETAILED}
 * @param <VERSION>  Class used as version of target {@link BRIEF} and {@link DETAILED}
 */
public interface ICRUDController<BRIEF, DETAILED, ID, VERSION> {
    ResponseEntity<DETAILED> create(DETAILED dto);

    ResponseEntity<DETAILED> update(DETAILED dto, ID uuid, VERSION version);

    ResponseEntity<DETAILED> findById(ID id);

    ResponseEntity<Boolean> existsById(ID id);

    ResponseEntity<Iterable<BRIEF>> findAll();

    ResponseEntity<Iterable<BRIEF>> findAllById(List<ID> ids);

    ResponseEntity<Long> count();

    ResponseEntity<?> deleteById(ID id, VERSION version);
}
