package com.anyservice.tests.unit;

import com.anyservice.config.TestConfig;
import com.anyservice.core.enums.LegalStatus;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.Contacts;
import com.anyservice.entity.Initials;
import com.anyservice.service.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Map;

import static com.anyservice.core.RandomValuesGenerator.*;
import static org.apache.commons.lang3.RandomStringUtils.random;

public class UserValidatorUnitTest extends TestConfig {

    @Autowired
    private UserValidator userValidator;

    /**
     * Fist parameter - User
     * Second parameter - Should be successful or not
     *
     * @return test dataset
     */
    @DataProvider
    public static Object[][] createValidationDataProvider() {
        int passwordMinLength = 8;
        int passwordMaxLength = 50;

        return new Object[][]{
                // Empty user - wait for fail
                {UserDetailed.builder().build(), false},

                // Everything is great except "userName" is null - wait for fail
                {UserDetailed.builder()
                        .initials(Initials.builder()
                                .firstName(random(randomNumber(2, 100), true, false))
                                .middleName(random(randomNumber(2, 100), true, false))
                                .lastName(random(randomNumber(2, 100), true, false))
                                .build())
                        .contacts(Contacts.builder()
                                .phone(random(randomNumber(6, 50), false, true))
                                .email(randomString(5, 100))
                                .google(randomString(5, 100))
                                .facebook(randomString(5, 100))
                                .build())
                        .userName(null)
                        .description(randomString(0, 1000))
                        .isLegalStatusVerified(randomBoolean())
                        .isVerified(randomBoolean())
                        .legalStatus(randomEnum(LegalStatus.class))
                        .password(random(randomNumber(passwordMinLength, passwordMaxLength), true, true))
                        .build(), false},


        };
    }

    @DataProvider
    public static Object[][] updateValidation() {
        return new Object[][]{{}};
    }

    @Test(dataProvider = "createValidationDataProvider")
    public void createValidationTest(UserDetailed user, boolean expected) {
        Map<String, Object> errors = userValidator.validateCreation(user);

        boolean actual = errors.isEmpty();

        Assert.assertEquals(actual, expected);
    }

    //    @Test(dataProvider = "updateValidationDataProvider")
    public void updateValidationTest(UserDetailed user, Date version, boolean expected) {
        // TODO implement
    }

}
