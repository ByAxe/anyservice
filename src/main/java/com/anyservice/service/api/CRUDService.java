package com.anyservice.service.api;

import com.anyservice.dto.UserDTO;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface CRUDService<T, ID> {
    T create(T dto);

    T update(UserDTO dto, UUID uuid, Date version);

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
