package com.anyservice.tests.api;

import com.anyservice.core.DateUtils;
import com.anyservice.dto.DetailedWrapper;
import com.anyservice.dto.api.APrimary;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ICRUDTest<BRIEF extends APrimary, DETAILED extends APrimary>
        extends ICRUDOperations<BRIEF, DETAILED> {

    /**
     * Get url prefix
     *
     * @return url as {@link String}
     */
    default String getUrlPrefix() {
        return "/api/v1";
    }

    /**
     * Creates a few items and then ensures created are equal to those were meant to be created
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void createAndSelectTest() throws Exception {
        List<DETAILED> items = new ArrayList<>();

        // Create items
        for (int i = 0; i < 10; i++) items.add(create().getDetailed());

        // Select all created elements
        List<BRIEF> actual = selectAll();

        // Prepare expected
        List<BRIEF> expected = items.stream()
                .map(e -> (BRIEF) e)
                .collect(Collectors.toList());

        // Compare it
        assertEqualsListBrief(actual, expected);
    }

    /**
     * Measures amount of elements before and after creation of some
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void countTest() throws Exception {
        // Measure amount of elements
        int expected = count();

        // Create detailed
        create();

        // increase expected by amount of created elements
        expected++;

        // Measure amount of elements after creation
        int actual = count();

        Assert.assertEquals(actual, expected);
    }

    /**
     * Creates and then removes detailed
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void deleteAndSelectByUUIDTest() throws Exception {
        // create detailed
        DetailedWrapper<DETAILED> detailedWrapper = create();
        UUID uuid = detailedWrapper.getUuid();

        // select by uuid
        DETAILED obtainedResult = select(uuid);

        // Assert its uuid's are equal
        Assert.assertEquals(obtainedResult.getUuid(), uuid);

        long version = DateUtils.convertOffsetDateTimeToMills(obtainedResult.getDtUpdate());

        // delete this row
        remove(uuid, version);

        // select again by uuid and expect there will be no entity returned
        select(uuid, expectNoContent);
    }

    /**
     * Creates and updates detailed
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void updateTest() throws Exception {
        // Create detailed
        DetailedWrapper<DETAILED> detailedWrapper = create();
        UUID uuid = detailedWrapper.getUuid();

        // select by uuid
        DETAILED obtainedResult = select(uuid);

        // Assert its uuid's are equal
        Assert.assertEquals(obtainedResult.getUuid(), uuid);

        long version = DateUtils.convertOffsetDateTimeToMills(obtainedResult.getDtUpdate());

        // Update whole detailed
        DETAILED updatedItem = update(uuid, version);

        // Select updated row by uuid
        obtainedResult = select(uuid);

        assertEqualsDetailed(obtainedResult, updatedItem);
    }

    /**
     * Create a few elements and them find them all by uuid list
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void createAndFindAllByIdListTest() throws Exception {
        List<DETAILED> items = new ArrayList<>();
        List<UUID> uuidList = new ArrayList<>();

        // Create items
        for (int i = 0; i < 10; i++) {
            DetailedWrapper<DETAILED> detailed = create();

            items.add(detailed.getDetailed());
            uuidList.add(detailed.getUuid());
        }

        // Select list of briefs by list of uuid
        List<BRIEF> actual = selectAll(uuidList);

        // Convert our detailed list to brief list
        List<BRIEF> expected = items.stream()
                .map(e -> (BRIEF) e)
                .collect(Collectors.toList());

        // Ensure that they are equal
        assertEqualsListBrief(actual, expected);
    }

    /**
     * Create an element and make sure exists method will confirm that
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    default void createAndCheckIfExists() throws Exception {
        DetailedWrapper<DETAILED> userWrapper = create();

        UUID uuid = userWrapper.getUuid();

        boolean exists = exists(uuid);

        Assert.assertTrue(exists);
    }

}
