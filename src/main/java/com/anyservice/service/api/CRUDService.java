package com.anyservice.service.api;

import java.util.Optional;

public interface CRUDService<BRIEF, DETAILED, ID, VERSION> {
    DETAILED create(DETAILED dto);

    DETAILED update(DETAILED dto, ID uuid, VERSION version);

    Optional<DETAILED> findById(ID id);

    boolean existsById(ID id);

    Iterable<BRIEF> findAll();

    Iterable<BRIEF> findAllById(Iterable<ID> ids);

    long count();

    void deleteById(ID id, VERSION version);

    void deleteAll();
}
