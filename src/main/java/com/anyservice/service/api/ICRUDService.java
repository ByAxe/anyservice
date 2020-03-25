package com.anyservice.service.api;

import com.anyservice.dto.api.APrimary;

import java.util.Optional;

public interface ICRUDService<BRIEF extends APrimary, DETAILED extends APrimary, ID, VERSION> {
    DETAILED create(DETAILED dto);

    DETAILED update(DETAILED dto, ID uuid, VERSION version);

    Optional<DETAILED> findById(ID id);

    boolean existsById(ID id);

    Iterable<BRIEF> findAll();

    Iterable<BRIEF> findAllById(Iterable<ID> ids);

    long count();

    void deleteById(ID id, VERSION version);
}
