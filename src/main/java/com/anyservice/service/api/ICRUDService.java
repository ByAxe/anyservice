package com.anyservice.service.api;

import com.anyservice.dto.api.APrimary;

import java.util.Optional;

/**
 * Main interface defining basic CRUD methods for all "classic" Spring-Services
 * <p>
 * DTOs as {@link BRIEF} or {@link DETAILED} used as different representations
 * of the same entity/object, stored in database
 *
 * @param <BRIEF>    DTO marked with {@link com.anyservice.dto.api.Brief} and extending {@link APrimary}
 * @param <DETAILED> DTO marked with {@link com.anyservice.dto.api.Detailed} and extending {@link APrimary}
 * @param <ID>       Class used as identifier of target {@link BRIEF} and {@link DETAILED}
 * @param <VERSION>  Class used as version of target {@link BRIEF} and {@link DETAILED}
 */
public interface ICRUDService<BRIEF extends APrimary, DETAILED extends APrimary, ID, VERSION> {

    /**
     * Create operation of {@link DETAILED}
     *
     * @param dto {@link DETAILED} that should be created
     * @return created {@link DETAILED}
     */
    DETAILED create(DETAILED dto);

    /**
     * Update operation of {@link DETAILED}
     *
     * @param dto     {@link DETAILED} that should be updated
     * @param id      {@link ID} identifier of an object
     * @param version {@link VERSION} of an updating object
     * @return updated {@link DETAILED}
     */
    DETAILED update(DETAILED dto, ID id, VERSION version);

    /**
     * Find {@link DETAILED} by id, wrapped in {@link Optional}
     *
     * @param id {@link ID} identifier of an object
     * @return found {@link DETAILED} wrapped in {@link Optional} OR {@link Optional#empty()}
     */
    Optional<DETAILED> findById(ID id);

    /**
     * Check whether {@link DETAILED} exists
     *
     * @param id {@link ID} identifier of an object
     * @return whether {@link DETAILED} was found or not
     */
    boolean existsById(ID id);

    /**
     * Find all {@link BRIEF} in database
     *
     * @return {@link java.util.List} of all found {@link BRIEF} in database
     */
    Iterable<BRIEF> findAll();

    /**
     * Find all {@link BRIEF} in database via given list of identifiers
     *
     * @param idList list of identifiers
     * @return {@link java.util.List} of all found {@link BRIEF} in database
     */
    Iterable<BRIEF> findAllById(Iterable<ID> idList);

    /**
     * Find overall count of object, those can be represented as {@link BRIEF} or {@link DETAILED}
     *
     * @return amount of found objects
     */
    long count();

    /**
     * Delete object via {@link ID} and {@link VERSION}
     *
     * @param id      {@link ID} identifier of an object
     * @param version {@link VERSION} version of an object
     */
    void deleteById(ID id, VERSION version);
}
