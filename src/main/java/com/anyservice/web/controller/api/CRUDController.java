package com.anyservice.web.controller.api;

import com.anyservice.dto.user.UserDetailed;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface CRUDController<T, ID> {
    ResponseEntity<?> create(UserDetailed dto);

    ResponseEntity<?> update(UserDetailed dto, UUID uuid, Date version);

    ResponseEntity<?> findById(ID id);

    ResponseEntity<Boolean> existsById(ID id);

    ResponseEntity<?> findAll();

    ResponseEntity<?> findAllById(List<ID> ids);

    ResponseEntity<Long> count();

    ResponseEntity<?> deleteById(ID id);

    ResponseEntity<?> delete(T dto);

}
