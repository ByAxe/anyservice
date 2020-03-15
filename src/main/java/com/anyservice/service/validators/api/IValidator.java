package com.anyservice.service.validators.api;

import java.util.Map;

public interface IValidator<E> {
    /**
     * Validate creation
     *
     * @param entity that must be validated
     * @return storage for errors obtained during validation
     */
    Map<String, Object> validateCreation(E entity);

    /**
     * Validate updates
     *
     * @param entity that must be validated
     * @return storage for errors obtained during validation
     */
    Map<String, Object> validateUpdates(E entity);
}
