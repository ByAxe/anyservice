package com.anyservice.tests.integration;

import com.anyservice.api.ICRUDTest;
import com.anyservice.config.TestConfig;
import com.anyservice.core.DateUtils;
import com.anyservice.core.enums.LegalStatus;
import com.anyservice.dto.DetailedWrapper;
import com.anyservice.dto.api.APrimary;
import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.dto.user.UserForChangePassword;
import com.anyservice.entity.user.Contacts;
import com.anyservice.entity.user.Initials;
import com.anyservice.service.api.IPasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.anyservice.core.RandomValuesGenerator.*;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MAX_LENGTH;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MIN_LENGTH;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


public class UserIntegrationTest extends TestConfig implements ICRUDTest<UserBrief, UserDetailed> {

    @Autowired
    private IPasswordService passwordService;

    private String baseUrl = "/";

    @Value("${password.length.min}")
    private int passwordMinLength;

    @Value("${password.length.max}")
    private int passwordMaxLength;

    @Override
    public String getUrl() {
        return "/user";
    }

    @Override
    public MockMvc getMockMvc() {
        return mockMvc;
    }

    @Override
    public MediaType getContentType() {
        return contentType;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public Class<? extends APrimary> getBriefClass() {
        return UserBrief.class;
    }

    @Override
    public Class<? extends APrimary> getDetailedClass() {
        return UserDetailed.class;
    }

    @Override
    public void assertEqualsDetailed(UserDetailed actual, UserDetailed expected) {
        Assert.assertEquals(actual.getInitials(), expected.getInitials());
        Assert.assertEquals(actual.getContacts(), expected.getContacts());
        Assert.assertEquals(actual.getUserName(), expected.getUserName());
        Assert.assertEquals(actual.getDescription(), expected.getDescription());
        Assert.assertEquals(actual.getIsLegalStatusVerified(), expected.getIsLegalStatusVerified());
        Assert.assertEquals(actual.getIsVerified(), expected.getIsVerified());
        Assert.assertEquals(actual.getLegalStatus(), expected.getLegalStatus());
    }

    @Override
    public void assertEqualsListBrief(List<UserBrief> actualList, List<UserBrief> expectedList) {
        actualList.sort(Comparator.comparing(UserBrief::getUserName));
        expectedList.sort(Comparator.comparing(UserBrief::getUserName));

        Assert.assertEquals(actualList.size(), expectedList.size());

        for (int element = 0; element < actualList.size(); element++) {
            UserBrief user = actualList.get(element);
            UserBrief otherUser = expectedList.get(element);

            Assert.assertEquals(user.getInitials(), otherUser.getInitials());
            Assert.assertEquals(user.getUserName(), otherUser.getUserName());
        }
    }

    @Override
    public UserDetailed createNewItem() {
        return UserDetailed.builder()
                .initials(createInitials())
                .contacts(createContacts())
                .userName(randomString(3, 50))
                .description(randomString(0, 1000))
                .isLegalStatusVerified(randomBoolean())
                .isVerified(randomBoolean())
                .legalStatus(randomEnum(LegalStatus.class))
                .password(random(randomNumber(passwordMinLength, passwordMaxLength), true, true))
                .address(randomString(0, 255))
                .build();
    }

    private Initials createInitials() {
        return Initials.builder()
                .firstName(random(randomNumber(2, 100), true, false))
                .middleName(random(randomNumber(2, 100), true, false))
                .lastName(random(randomNumber(2, 100), true, false))
                .build();
    }

    private Contacts createContacts() {
        return Contacts.builder()
                .phone(random(randomNumber(6, 50), false, true))
                .email(randomString(5, 100))
                .google(randomString(5, 100))
                .facebook(randomString(5, 100))
                .build();
    }

    @Test
    @Override
    public void createAndSelectTest() throws Exception {
        ICRUDTest.super.createAndSelectTest();
    }

    @Test
    @Override
    public void countTest() throws Exception {
        ICRUDTest.super.countTest();
    }

    @Test
    @Override
    public void deleteAndSelectByUUIDTest() throws Exception {
        ICRUDTest.super.deleteAndSelectByUUIDTest();
    }

    @Test
    @Override
    public void updateTest() throws Exception {
        ICRUDTest.super.updateTest();
    }

    @Test
    @Override
    public void createAndFindAllByIdListTest() throws Exception {
        ICRUDTest.super.createAndFindAllByIdListTest();
    }

    /**
     * Create two users and then change user name of first on the userName of already existing user (second user)
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void userNameValidationTest_changeOnExistingUserName() throws Exception {
        String userNameOfFirstUser = randomString(1, 50);
        String userNameOfSecondUser = randomString(1, 50);

        // Build the first user
        UserDetailed firstUser = createNewItem();
        firstUser.setUserName(userNameOfFirstUser);

        // Create first user in DB
        DetailedWrapper<UserDetailed> firstUserWrapper = create(firstUser, expectDefault);

        // Get it uuid
        UUID uuidOfFirstUser = firstUserWrapper.getUuid();

        // Build the second user
        UserDetailed secondUser = createNewItem();
        secondUser.setUserName(userNameOfSecondUser);

        // Create second user in DB
        DetailedWrapper<UserDetailed> secondUserWrapper = create(secondUser, expectDefault);

        // Select the first user
        UserDetailed obtainedResult = select(uuidOfFirstUser);

        // Assert its uuid's are equal
        Assert.assertEquals(obtainedResult.getUuid(), uuidOfFirstUser);

        long version = DateUtils.convertOffsetDateTimeToMills(obtainedResult.getDtUpdate());

        // Try to change userName of firstUser, on the userName of the secondUser
        obtainedResult.setUserName(userNameOfSecondUser);
        obtainedResult.setPassword(randomString(passwordMinLength, passwordMaxLength));

        // Make sure we get an exception in the end
        update(obtainedResult, uuidOfFirstUser, version, expectBadRequest);
    }

    /**
     * Removing non-existing user
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void deleteNotExistingUser() throws Exception {
        UUID uuid = UUID.randomUUID();
        long version = new Date().getTime();

        // Wait for fail when removing non-existing user
        remove(uuid, version, expectBadRequest);
    }

    /**
     * Removing user with wrong version and expecting fail
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void deleteUserWithWrongVersion() throws Exception {
        // Create user
        DetailedWrapper<UserDetailed> userWrapper = create();
        UUID uuid = userWrapper.getUuid();

        // Set wrong version
        long version = new Date().getTime() + 100_000;

        // Make delete request and expect exception
        remove(uuid, version, expectBadRequest);
    }

    /**
     * Create user then change its password and make sure it's actually changed
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void changePasswordTest() throws Exception {
        // Generate password
        String password = randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

        // Build a user
        UserDetailed user = createNewItem();
        user.setPassword(password);

        // Create user
        DetailedWrapper<UserDetailed> userWrapper = create(user, expectDefault);

        // Get uuid of created user
        UUID uuid = userWrapper.getUuid();

        // Generate a new password for the user
        String newPassword = randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

        // Change password for the first time
        UserForChangePassword userForChangePassword = changePassword(uuid, password, newPassword, expectOk);

        // Try to change it again, and expect error because password has already changed
        changePassword(userForChangePassword, expectBadRequest);

        // Change password one more time, but this time expect that password already changed,
        // so that previous "newPassword" becomes "oldPassword" for this operation
        password = newPassword;

        // Generate a new password for the second change
        newPassword = randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

        // Change password the second time but expect success now, because everything is correct
        changePassword(uuid, password, newPassword, expectOk);
    }

    /**
     * Change password operation wrapper
     * Builds {@link UserForChangePassword} from given data
     *
     * @param uuid          identifier of a user
     * @param password      old password
     * @param newPassword   new password
     * @param resultMatcher result to expect after execution of method
     * @return {@link UserForChangePassword} that was used for change password operation
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    private UserForChangePassword changePassword(UUID uuid, String password, String newPassword,
                                                 ResultMatcher resultMatcher) throws Exception {
        // Build a special object, required for the second password change operation
        UserForChangePassword userForChangePassword = UserForChangePassword.builder()
                .uuid(uuid)
                .oldPassword(password)
                .newPassword(newPassword)
                .build();

        // Change password
        changePassword(userForChangePassword, resultMatcher);

        return userForChangePassword;
    }

    /**
     * Change password operation wrapper
     * Converts {@link UserForChangePassword} to string and performs change password operation
     *
     * @param userForChangePassword {@link UserForChangePassword} that is used for change password operation
     * @param resultMatcher         result to expect after execution of method
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    private void changePassword(UserForChangePassword userForChangePassword,
                                ResultMatcher resultMatcher) throws Exception {
        String valueAsString = getObjectMapper().writeValueAsString(userForChangePassword);

        getMockMvc().perform(put(getExtendedUrl() + "/change/password")
                .headers(getHeaders())
                .contentType(getContentType())
                .content(valueAsString))
                .andExpect(resultMatcher);
    }

    //        @Test
    public void getUserWithoutPassword() throws Exception {
        // Create user

        // Find user by id

        // make sure password is null
    }

    //    @Test
    public void updateUserButPasswordMustNotChange() throws Exception {
        // Create user

        // Update user

        // Make sure password did not change

    }
}
