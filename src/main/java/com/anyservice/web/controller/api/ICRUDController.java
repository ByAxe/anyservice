package com.anyservice.web.controller.api;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICRUDController<BRIEF, DETAILED, ID, VERSION> {
    ResponseEntity<?> create(DETAILED dto);

    ResponseEntity<?> update(DETAILED dto, ID uuid, VERSION version);

    ResponseEntity<?> findById(ID id);

    ResponseEntity<Boolean> existsById(ID id);

    ResponseEntity<?> findAll();

    ResponseEntity<?> findAllById(List<ID> ids);

    ResponseEntity<Long> count();

    ResponseEntity<?> deleteById(ID id, VERSION version);
}
