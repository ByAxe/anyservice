package com.anyservice.api;

import com.anyservice.core.DateUtils;
import com.anyservice.dto.api.APrimary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface ICRUDTest<BRIEF extends APrimary, DETAILED extends APrimary> {

    String urlPrefix = "/api/v1";

    String getUrl();

    MockMvc getMockMvc();

    MediaType getContentType();

    ObjectMapper getObjectMapper();

    Class<? extends APrimary> getBriefClass();

    Class<? extends APrimary> getDetailedClass();

    void assertEqualsDetailed(DETAILED detailed, DETAILED otherDetailed);

    void assertEqualsListBrief(List<BRIEF> briefList, List<BRIEF> otherBriefList);

    DETAILED createNewItem();

    default String getExtendedUrl() {
        return urlPrefix + getUrl();
    }

    default UUID getUuidFromHeaderLocation(String headerLocation) {
        Assert.assertNotNull(headerLocation);

        // Get uuid from header
        return UUID.fromString(headerLocation.substring(headerLocation.length() - 36));
    }

    default HttpHeaders getHeaders() {
        return new HttpHeaders();
    }

    default void createAndSelectTest() throws Exception {

        List<DETAILED> items = new ArrayList<>();

        // Create items
        for (int i = 0; i < 10; i++) {
            DETAILED item = createNewItem();
            items.add(item);

            String customerAsString = getObjectMapper().writeValueAsString(item);

            getMockMvc().perform(post(getExtendedUrl())
                    .headers(getHeaders())
                    .contentType(getContentType())
                    .content(customerAsString))
                    .andExpect(status().isCreated());
        }

        // Select items
        String result = getMockMvc().perform(get(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BRIEF> obtainedResult = getObjectMapper().readValue(result,
                getObjectMapper().getTypeFactory().constructCollectionType(List.class, getBriefClass()));

        List<BRIEF> itemsB = items.stream()
                .map(e -> (BRIEF) e)
                .collect(Collectors.toList());

        assertEqualsListBrief(obtainedResult, itemsB);
    }

    default void countTest() throws Exception {
        int expectedAmount = Integer.parseInt(
                getMockMvc().perform(get(getExtendedUrl() + "/count")
                        .headers(getHeaders())
                        .contentType(getContentType()))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString());

        // Create item
        DETAILED item = createNewItem();
        String customerAsString = getObjectMapper().writeValueAsString(item);
        getMockMvc().perform(post(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType())
                .content(customerAsString))
                .andExpect(status().isCreated());

        expectedAmount++;

        int result = Integer.parseInt(
                getMockMvc().perform(get(getExtendedUrl() + "/count")
                        .headers(getHeaders())
                        .contentType(getContentType()))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString());

        Assert.assertEquals(result, expectedAmount);
    }

    default void deleteAndSelectByUUIDTest() throws Exception {
        DETAILED item = createNewItem();
        String customerAsString = getObjectMapper().writeValueAsString(item);
        String headerLocation = getMockMvc().perform(post(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType())
                .content(customerAsString))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        UUID uuid = getUuidFromHeaderLocation(headerLocation);
        // select by uuid
        String contentAsString = getMockMvc().perform(get(getExtendedUrl() + "/" + uuid)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DETAILED obtainedResult = getObjectMapper().readValue(contentAsString,
                getObjectMapper().getTypeFactory().constructType(getDetailedClass()));

        // Assert its uuid's are equal
        Assert.assertEquals(obtainedResult.getUuid(), uuid);

        long version = DateUtils.convertOffsetDateTimeToMills(obtainedResult.getDtUpdate());

        // delete this row
        getMockMvc().perform(delete(getExtendedUrl() + "/" + uuid + "/version/" + version)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isOk());


        // select again by uuid and expect there will be no entity returned
        getMockMvc().perform(get(getExtendedUrl() + "/" + uuid)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isOk());
    }

    default void updateTest() throws Exception {
        DETAILED item = createNewItem();
        String customerAsString = getObjectMapper().writeValueAsString(item);
        String headerLocation = getMockMvc().perform(post(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType())
                .content(customerAsString))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        UUID uuid = getUuidFromHeaderLocation(headerLocation);

        // select by uuid
        String contentAsString = getMockMvc().perform(get(getExtendedUrl() + "/" + uuid)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DETAILED obtainedResult = getObjectMapper().readValue(contentAsString,
                getObjectMapper().getTypeFactory().constructType(getDetailedClass()));

        // Assert its uuid's are equal
        Assert.assertEquals(obtainedResult.getUuid(), uuid);

        long version = DateUtils.convertOffsetDateTimeToMills(obtainedResult.getDtUpdate());

        // Update
        DETAILED updatedItem = createNewItem();

        String updatedItemAsString = getObjectMapper().writeValueAsString(updatedItem);

        getMockMvc().perform(put(getExtendedUrl() + "/" + uuid + "/version/" + version)
                .headers(getHeaders())
                .contentType(getContentType())
                .content(updatedItemAsString))
                .andExpect(status().isOk());

        // Select updated row by uuid
        contentAsString = getMockMvc().perform(get(getExtendedUrl() + "/" + uuid)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        obtainedResult = getObjectMapper().readValue(contentAsString,
                getObjectMapper().getTypeFactory().constructType(getDetailedClass()));

        assertEqualsDetailed(obtainedResult, updatedItem);
    }
}
