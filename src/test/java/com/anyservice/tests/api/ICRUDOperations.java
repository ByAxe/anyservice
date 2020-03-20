package com.anyservice.tests.api;

import com.anyservice.dto.DetailedWrapper;
import com.anyservice.dto.api.APrimary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testng.Assert;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface ICRUDOperations<BRIEF extends APrimary, DETAILED extends APrimary> {

    // Result Matchers
    ResultMatcher expectOk = status().isOk();
    ResultMatcher expectCreated = status().isCreated();
    ResultMatcher expectBadRequest = status().isBadRequest();
    ResultMatcher expectNoContent = status().isNoContent();

    // Use whenever expected default successful response of performing operation
    ResultMatcher expectDefault = null;

    /**
     * Object through that all the queries is made
     *
     * @return {@link MockMvc}
     */
    MockMvc getMockMvc();

    /**
     * Required content type of all queries
     *
     * @return JSON {@link MediaType}
     */
    MediaType getContentType();

    /**
     * Configured and autowired {@link ObjectMapper}
     *
     * @return configured {@link ObjectMapper}
     */
    ObjectMapper getObjectMapper();

    /**
     * Target object representation for lists
     *
     * @return {@link BRIEF}
     */
    Class<? extends APrimary> getBriefClass();

    /**
     * Target object representation for single queries
     *
     * @return {@link DETAILED}
     */
    Class<? extends APrimary> getDetailedClass();

    /**
     * Assertion for two {@link DETAILED} object to be equal
     *
     * @param actual   detailed
     * @param expected detailed
     */
    void assertEqualsDetailed(DETAILED actual, DETAILED expected);

    /**
     * Assertion for two {@link BRIEF} lists of objects to be equal (in all meanings)
     *
     * @param actualList   of briefs
     * @param expectedList of briefs
     */
    void assertEqualsListBrief(List<BRIEF> actualList, List<BRIEF> expectedList);

    /**
     * Method that encapsulates creation of {@link DETAILED} item
     *
     * @return created & fulfilled new {@link DETAILED}
     */
    DETAILED createNewItem();

    /**
     * Get url prefix specific for current version
     *
     * @return url as {@link String}
     */
    String getUrlPrefix();

    /**
     * URL for the target controller to be tested
     *
     * @return url as {@link String}
     */
    String getUrl();

    /**
     * Extends URL with some common prefix
     *
     * @return extended url as {@link String}
     */
    default String getExtendedUrl() {
        return getUrlPrefix() + getUrl();
    }

    /**
     * A special header for unlimited possibilities
     *
     * @return header
     */
    String getInnerHeader();

    /**
     * A special key for unlimited possibilities
     *
     * @return key
     */
    String getInnerKey();

    /**
     * Common operation to get UUID from a raw header "Location"
     *
     * @param headerLocation content of header
     * @return {@link UUID}
     */
    default UUID getUuidFromHeaderLocation(String headerLocation) {
        Assert.assertNotNull(headerLocation);

        final int offset = 36;

        // Get uuid from header
        return UUID.fromString(headerLocation.substring(headerLocation.length() - offset));
    }

    /**
     * One method to add some custom headers if it is required
     *
     * @return {@link HttpHeaders} object with or without custom headers
     */
    default HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(getInnerHeader(), getInnerKey());

        return httpHeaders;
    }

    /**
     * Create {@link DETAILED} and return special object, containing necessary data after creation
     * <p>
     * An overloaded version of {@link ICRUDOperations#create(APrimary, ResultMatcher)}
     * With default {@link DETAILED} and {@link ResultMatcher}
     *
     * @return special object, containing necessary data after creation
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default DetailedWrapper<DETAILED> create() throws Exception {
        return create(createNewItem(), expectCreated);
    }

    /**
     * Creates {@link DETAILED} and return special object, containing necessary data after creation
     *
     * @param detailed that must be created
     * @param expect   expectations about what to expect after execution of a method
     * @return special object, containing necessary data after creation
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default DetailedWrapper<DETAILED> create(DETAILED detailed, ResultMatcher expect) throws Exception {
        String customerAsString = getObjectMapper().writeValueAsString(detailed);

        // If expect was passed as null - interpret it as default expectation
        if (expect == expectDefault) expect = expectCreated;

        String headerLocation = getMockMvc().perform(post(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType())
                .content(customerAsString))
                .andExpect(expect)
                .andReturn()
                .getResponse()
                .getHeader("Location");

        UUID uuid = getUuidFromHeaderLocation(headerLocation);

        return DetailedWrapper.<DETAILED>builder().detailed(detailed).uuid(uuid).build();
    }

    /**
     * Updates object with passed identifiers
     *
     * @param uuid    identifier
     * @param version actual version of it
     * @return created detailed
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default DETAILED update(UUID uuid, long version) throws Exception {
        DETAILED detailed = createNewItem();

        update(detailed, uuid, version, expectOk);
        return detailed;
    }

    /**
     * Updates given {@link DETAILED}
     *
     * @param detailed updated object
     * @param uuid     identifier
     * @param version  actual version of it
     * @param expect   expectations about what to expect after execution of a method
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void update(DETAILED detailed, UUID uuid, long version, ResultMatcher expect) throws Exception {
        String updatedItemAsString = getObjectMapper().writeValueAsString(detailed);

        // If expect was passed as null - interpret it as default expectation
        if (expect == expectDefault) expect = expectOk;

        getMockMvc().perform(put(getExtendedUrl() + "/" + uuid + "/version/" + version)
                .headers(getHeaders())
                .contentType(getContentType())
                .content(updatedItemAsString))
                .andExpect(expect);
    }

    /**
     * Selects {@link DETAILED} by passed uuid
     * <p>
     * An overloaded version of {@link ICRUDOperations#select(UUID, ResultMatcher)}
     * With default {@link ResultMatcher}
     *
     * @param uuid {@link DETAILED} identifier
     * @return {@link DETAILED} with passed uuid
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default DETAILED select(UUID uuid) throws Exception {
        return select(uuid, expectOk);
    }

    /**
     * Selects {@link DETAILED} by passed uuid
     *
     * @param expect expectations about what to expect after execution of a method
     * @param uuid   {@link DETAILED} identifier
     * @return {@link DETAILED} with passed uuid
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default DETAILED select(UUID uuid, ResultMatcher expect) throws Exception {

        // If expect was passed as null - interpret it as default expectation
        if (expect == expectDefault) expect = expectOk;

        String contentAsString = getMockMvc().perform(get(getExtendedUrl() + "/" + uuid)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expect)
                .andReturn()
                .getResponse()
                .getContentAsString();

        // If we are expecting no content here, so return null
        if (expectNoContent.equals(expect)) return null;

        return getObjectMapper().readValue(contentAsString,
                getObjectMapper().getTypeFactory().constructType(getDetailedClass()));
    }

    /**
     * Selects all briefs by given list of uuid
     * <p>
     * An overloaded version of {@link ICRUDOperations#selectAll(List, ResultMatcher)}
     * With default {@link ResultMatcher}
     *
     * @param uuidList identifiers list
     * @return list of briefs
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default List<BRIEF> selectAll(List<UUID> uuidList) throws Exception {
        return selectAll(uuidList, expectOk);
    }

    /**
     * Selects all briefs by given list of uuid
     *
     * @param expect   expectations about what to expect after execution of a method
     * @param uuidList identifiers list
     * @return list of briefs
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default List<BRIEF> selectAll(List<UUID> uuidList, ResultMatcher expect) throws Exception {
        // Convert list to string
        String uuidListAsString = uuidList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // If expect was passed as null - interpret it as default expectation
        if (expect == expectDefault) expect = expectOk;

        // Select items by uuid's
        String result = getMockMvc().perform(get(getExtendedUrl() + "/uuid/list/" + uuidListAsString)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expect)
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Convert it to list of briefs
        return getObjectMapper().readValue(result,
                getObjectMapper().getTypeFactory().constructCollectionType(List.class, getBriefClass()));
    }

    /**
     * Selects all briefs
     * <p>
     * An overloaded version of {@link ICRUDOperations#selectAll(ResultMatcher)}
     * With default {@link ResultMatcher}
     *
     * @return list of all briefs
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default List<BRIEF> selectAll() throws Exception {
        return selectAll(expectOk);
    }

    /**
     * Selects all briefs
     *
     * @param expect expectations about what to expect after execution of a method
     * @return list of all briefs
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default List<BRIEF> selectAll(ResultMatcher expect) throws Exception {

        // If expect was passed as null - interpret it as default expectation
        if (expect == expectDefault) expect = expectOk;

        // Select items
        String result = getMockMvc().perform(get(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expect)
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Convert obtained results
        return getObjectMapper().readValue(result,
                getObjectMapper().getTypeFactory().constructCollectionType(List.class, getBriefClass()));
    }

    /**
     * Gets a count of existing elements
     * <p>
     * An overloaded version of {@link ICRUDOperations#count(ResultMatcher)}
     * With default {@link ResultMatcher}
     *
     * @return count of existing element
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default int count() throws Exception {
        return count(expectOk);
    }

    /**
     * Gets a count of existing elements
     *
     * @param expect expectations about what to expect after execution of a method
     * @return count of existing element
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default int count(ResultMatcher expect) throws Exception {

        // If expect was passed as null - interpret it as default expectation
        if (expect == expectDefault) expect = expectOk;

        String countAsString = getMockMvc().perform(get(getExtendedUrl() + "/count")
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expect)
                .andReturn()
                .getResponse()
                .getContentAsString();

        return Integer.parseInt(countAsString);
    }

    /**
     * Removes detailed with given id and version
     * <p>
     * An overloaded version of {@link ICRUDOperations#remove(UUID, long, ResultMatcher)}
     * With default {@link ResultMatcher}
     *
     * @param uuid    identifier of detailed
     * @param version current version of detailed
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void remove(UUID uuid, long version) throws Exception {
        remove(uuid, version, expectNoContent);
    }

    /**
     * Removes detailed with given id and version
     *
     * @param expect  expectations about what to expect after execution of a method
     * @param uuid    identifier of detailed
     * @param version current version of detailed
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void remove(UUID uuid, long version, ResultMatcher expect) throws Exception {

        // If expect was passed as null - interpret it as default expectation
        if (expect == expectDefault) expect = expectNoContent;

        getMockMvc().perform(delete(getExtendedUrl() + "/" + uuid + "/version/" + version)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expect);
    }

    /**
     * Checks if the user by uuid exists
     *
     * @param uuid identifier of a user
     * @return exists or not
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default boolean exists(UUID uuid) throws Exception {
        String countAsString = getMockMvc().perform(get(getExtendedUrl() + "/exists/" + uuid)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expectOk)
                .andReturn()
                .getResponse()
                .getContentAsString();

        return Boolean.parseBoolean(countAsString);
    }
}
