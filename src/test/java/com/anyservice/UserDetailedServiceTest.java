package com.anyservice;

import com.anyservice.api.ICRUDTest;
import com.anyservice.config.TestNGConfig;
import com.anyservice.dto.api.APrimary;
import com.anyservice.dto.enums.LegalStatus;
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
import java.util.List;
import java.util.UUID;

import static com.anyservice.core.TestRandom.*;
import static org.apache.commons.lang3.RandomStringUtils.random;


public class UserDetailedServiceTest extends TestNGConfig implements ICRUDTest<UserBrief, UserDetailed> {

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
        Assert.assertEquals(detailed.getUuid(), otherDetailed.getUuid());
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

            Assert.assertEquals(user.getUuid(), otherUser.getUuid());
            Assert.assertEquals(user.getInitials(), otherUser.getInitials());
            Assert.assertEquals(user.getUserName(), otherUser.getUserName());
        }
    }

    @Override
    public UserDetailed createNewItem() {
        return UserDetailed.builder()
                .uuid(UUID.randomUUID())
                .initials(createInitials())
                .contacts(createContacts())
                .userName(random(randomNumber(3, 50), true, true))
                .description(random(randomNumber(0, 1000)))
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
                .email(random(randomNumber(5, 100)))
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
}
