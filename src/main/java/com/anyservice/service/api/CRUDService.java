package com.anyservice.service.api;

import java.util.Date;
import java.util.Optional;

public interface CRUDService<BRIEF, DETAILED, ID> {
    DETAILED create(DETAILED dto);

    DETAILED update(DETAILED dto, ID uuid, Date version);

    Iterable<BRIEF> saveAll(Iterable<BRIEF> dtoIterable);

    Optional<DETAILED> findById(ID id);

    boolean existsById(ID id);

    Iterable<BRIEF> findAll();

    Iterable<BRIEF> findAllById(Iterable<ID> ids);

    long count();

    void deleteById(ID id);

    void delete(DETAILED dto);

    void deleteAll(Iterable<? extends BRIEF> dtoIterable);

    void deleteAll();
}
