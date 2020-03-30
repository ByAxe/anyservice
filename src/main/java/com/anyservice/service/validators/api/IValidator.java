package com.anyservice.service.validators.api;

import org.springframework.context.MessageSource;

import java.util.Map;

/**
 * Root interface for all Validator classes
 *
 * @param <E> Type of entity/object that much be validated
 */
public interface IValidator<E> {

    /**
     * Message source for localized messages in errors
     *
     * @return Autowired Spring Bean containing localized messages
     */
    MessageSource getMessageSource();

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
