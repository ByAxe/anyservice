package com.anyservice.web.controller.api;

import java.util.Optional;

public interface CRUDController<T, ID> {
    T save(T dto);

    Iterable<T> saveAll(Iterable<T> dtoIterable);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    Iterable<T> findAll();

    Iterable<T> findAllById(Iterable<ID> ids);

    long count();

    void deleteById(ID id);

    void delete(T dto);

    void deleteAll(Iterable<? extends T> dtoIterable);

    void deleteAll();
}
