package com.anyservice.tests.integration;

import com.anyservice.api.ICRUDTest;
import com.anyservice.config.TestConfig;
import com.anyservice.core.DateUtils;
import com.anyservice.core.enums.LegalStatus;
import com.anyservice.dto.api.APrimary;
import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.Contacts;
import com.anyservice.entity.Initials;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.anyservice.core.RandomValuesGenerator.*;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserIntegrationTest extends TestConfig implements ICRUDTest<UserBrief, UserDetailed> {

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
    public void assertEqualsDetailed(UserDetailed detailed, UserDetailed otherDetailed) {
        Assert.assertEquals(detailed.getInitials(), otherDetailed.getInitials());
        Assert.assertEquals(detailed.getContacts(), otherDetailed.getContacts());
        Assert.assertEquals(detailed.getUserName(), otherDetailed.getUserName());
        Assert.assertEquals(detailed.getDescription(), otherDetailed.getDescription());
        Assert.assertEquals(detailed.getIsLegalStatusVerified(), otherDetailed.getIsLegalStatusVerified());
        Assert.assertEquals(detailed.getIsVerified(), otherDetailed.getIsVerified());
        Assert.assertEquals(detailed.getLegalStatus(), otherDetailed.getLegalStatus());
    }

    @Override
    public void assertEqualsListBrief(List<UserBrief> briefList, List<UserBrief> otherBriefList) {
        briefList.sort(Comparator.comparing(UserBrief::getUserName));
        otherBriefList.sort(Comparator.comparing(UserBrief::getUserName));

        Assert.assertEquals(briefList.size(), otherBriefList.size());

        for (int element = 0; element < briefList.size(); element++) {
            UserBrief user = briefList.get(element);
            UserBrief otherUser = otherBriefList.get(element);

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
    public void userNameValidationTest_changeOnExistingUserName() throws Exception {
        String userNameOfFirstUser = randomString(1, 50);
        String userNameOfSecondUser = randomString(1, 50);

        // Create first user
        UserDetailed firstUser = createNewItem();
        firstUser.setUserName(userNameOfFirstUser);

        String firstUserAsString = getObjectMapper().writeValueAsString(firstUser);

        String headerLocation = getMockMvc().perform(post(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType())
                .content(firstUserAsString))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        UUID uuidOfFirstUser = getUuidFromHeaderLocation(headerLocation);

        // Create the second user
        UserDetailed secondUser = createNewItem();
        secondUser.setUserName(userNameOfSecondUser);

        String secondUserAsString = getObjectMapper().writeValueAsString(secondUser);

        getMockMvc().perform(post(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType())
                .content(secondUserAsString))
                .andExpect(status().isCreated());

        // Select the first user
        String contentAsString = getMockMvc().perform(get(getExtendedUrl() + "/" + uuidOfFirstUser)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDetailed obtainedResult = getObjectMapper().readValue(contentAsString,
                getObjectMapper().getTypeFactory().constructType(getDetailedClass()));

        // Assert its uuid's are equal
        Assert.assertEquals(obtainedResult.getUuid(), uuidOfFirstUser);

        long version = DateUtils.convertOffsetDateTimeToMills(obtainedResult.getDtUpdate());

        // Try to change userName of firstUser, on the userName of the secondUser
        obtainedResult.setUserName(userNameOfSecondUser);
        obtainedResult.setPassword(randomString(passwordMinLength, passwordMaxLength));

        String updatedUserAsString = getObjectMapper().writeValueAsString(obtainedResult);

        // Make sure we get an exception in the end
        getMockMvc().perform(put(getExtendedUrl() + "/" + uuidOfFirstUser + "/version/" + version)
                .headers(getHeaders())
                .contentType(getContentType())
                .content(updatedUserAsString))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteNotExistingUser() throws Exception {
        UUID uuid = UUID.randomUUID();
        long version = new Date().getTime();

        getMockMvc().perform(delete(getExtendedUrl() + "/" + uuid + "/version/" + version)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUserWithWrongVersion() throws Exception {
        // Create user
        UserDetailed firstUser = createNewItem();

        String firstUserAsString = getObjectMapper().writeValueAsString(firstUser);

        String headerLocation = getMockMvc().perform(post(getExtendedUrl())
                .headers(getHeaders())
                .contentType(getContentType())
                .content(firstUserAsString))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        UUID uuid = getUuidFromHeaderLocation(headerLocation);

        // Set wrong version
        long version = new Date().getTime() + 100_000;

        // Make delete request and expect exception
        getMockMvc().perform(delete(getExtendedUrl() + "/" + uuid + "/version/" + version)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isBadRequest());
    }
}
