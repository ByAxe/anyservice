package com.anyservice.web.controller.api;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CRUDController<T, ID> {
    ResponseEntity<?> save(T dto, UUID uuid);

    ResponseEntity<?> findById(ID id);

    ResponseEntity<Boolean> existsById(ID id);

    ResponseEntity<?> findAll();

    ResponseEntity<?> findAllById(List<ID> ids);

    ResponseEntity<Long> count();

    ResponseEntity<?> deleteById(ID id);

    ResponseEntity<?> delete(T dto);

}
