package com.anyservice.web.controller.api;

import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface CRUDController<BRIEF, DETAILED, ID> {
    ResponseEntity<?> create(DETAILED dto);

    ResponseEntity<?> update(DETAILED dto, ID uuid, Date version);

    ResponseEntity<?> findById(ID id);

    ResponseEntity<Boolean> existsById(ID id);

    ResponseEntity<?> findAll();

    ResponseEntity<?> findAllById(List<ID> ids);

    ResponseEntity<Long> count();

    ResponseEntity<?> deleteById(ID id);

    ResponseEntity<?> delete(DETAILED dto);

}
