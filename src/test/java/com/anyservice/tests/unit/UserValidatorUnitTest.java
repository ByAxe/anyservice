package com.anyservice.tests.unit;

import com.anyservice.config.TestConfig;
import com.anyservice.core.enums.LegalStatus;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.user.Contacts;
import com.anyservice.entity.user.Initials;
import com.anyservice.service.api.IPasswordService;
import com.anyservice.service.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.anyservice.core.RandomValuesGenerator.*;
import static com.anyservice.core.TestingUtilityClass.*;
import static org.apache.commons.lang3.RandomStringUtils.random;

public class UserValidatorUnitTest extends TestConfig {

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private IPasswordService passwordService;

    /**
     * Fist parameter - User
     * Second parameter - Should be successful or not
     *
     * @return test dataset
     */
    @DataProvider
    public static Object[][] createValidationDataProvider() {
        return new Object[][]{
                // Empty user - wait for FAIL
                {UserDetailed.builder().build(), FAIL},

                // "userName" is null - wait for FAIL
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
                        .password(random(randomNumber(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH)))
                        .build(), FAIL},


                // Everything is great - wait for SUCCESS
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
                        .userName(randomString(1, 50))
                        .description(randomString(0, 1000))
                        .isLegalStatusVerified(randomBoolean())
                        .isVerified(randomBoolean())
                        .legalStatus(randomEnum(LegalStatus.class))
                        .password(random(randomNumber(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH),
                                true, true))
                        .build(), SUCCESS}
        };
    }

    @DataProvider
    public static Object[][] updateValidationDataProvider() {
        return new Object[][]{
                // Empty user - wait for FAIL
                {UserDetailed.builder().build(), FAIL},

                // Everything is great - wait for SUCCESS
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
                        .userName(randomString(1, 50))
                        .description(randomString(0, 1000))
                        .isLegalStatusVerified(randomBoolean())
                        .isVerified(randomBoolean())
                        .legalStatus(randomEnum(LegalStatus.class))
                        .password(random(randomNumber(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH),
                                true, true))
                        .build(), SUCCESS}
        };
    }

    @DataProvider
    public static Object[][] passwordValidationDataProvider() {
        return new Object[][]{
                // wait for SUCCESS
                {random(randomNumber(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH), true, true), SUCCESS},

                // "password" is null - wait for FAIL
                {null, FAIL},

                // "password" is longer then allowed - wait for FAIL
                {random(randomNumber(PASSWORD_MAX_LENGTH + 1, PASSWORD_MAX_LENGTH * 2), true, true), FAIL},

                // "password" contains restricted character - wait for FAIL
                {random(randomNumber(PASSWORD_MIN_LENGTH - 1, PASSWORD_MAX_LENGTH - 1)) + "~", FAIL},

                // "password" is shorter than allowed - wait for FAIL
                {random(randomNumber(0, PASSWORD_MIN_LENGTH - 1), true, true), FAIL}
        };
    }

    @DataProvider
    public static Object[][] lettersOnlyValidationDataProvider() {
        final String fieldName = randomString(1, 100);

        return new Object[][]{
                // "field" contains numbers - wait for SUCCESS
                {randomString(1, 100), fieldName, SUCCESS},

                // "field" contains numbers - wait for FAIL
                {randomString(1, 100) + "111", fieldName, FAIL},

                // "field" contains restricted characters - wait for FAIL
                {"[" + randomString(1, 100) + "]", fieldName, FAIL},

                // "field" is empty - wait for FAIL
                {"", fieldName, FAIL},

                // "fieldName" & "field" are empty - wait for FAIL
                {"", "", FAIL},

                // "fieldName" is empty & "field" contains restricted characters - wait for FAIL
                {"[" + randomString(1, 100) + "]", "", FAIL},

                // "fieldName" is empty - wait for FAIL
                {randomString(1, 100), "", FAIL},

                // "fieldName" is null - wait for FAIL
                {randomString(1, 100), null, FAIL},

                // "fieldName" is null - wait for FAIL
                {null, "", FAIL},

                // "fieldName" & "field" - wait for FAIL
                {null, null, FAIL},
        };
    }

    @DataProvider
    public static Object[][] initialsValidationDataProvider() {
        return new Object[][]{
                // "initials" is null - wait for FAIL
                {null, FAIL},

                // "firstName" is null - wait for FAIL
                {Initials.builder()
                        .lastName(randomString(1, 50))
                        .middleName(randomString(1, 50))
                        .build(), FAIL},

                // "firstName" only present - wait for SUCCESS
                {Initials.builder()
                        .firstName(randomString(1, 50))
                        .build(), SUCCESS},

                // All fields present - wait for SUCCESS
                {Initials.builder()
                        .firstName(randomString(1, 50))
                        .lastName(randomString(1, 50))
                        .middleName(randomString(1, 50))
                        .build(), SUCCESS},

                // "lastName" is empty - wait for FAIL
                {Initials.builder()
                        .firstName(randomString(1, 50))
                        .lastName("")
                        .middleName(randomString(1, 50))
                        .build(), FAIL},

                // "middleName" is empty - wait for FAIL
                {Initials.builder()
                        .firstName(randomString(1, 50))
                        .lastName(randomString(1, 50))
                        .middleName("")
                        .build(), FAIL},
        };
    }

    @DataProvider
    public static Object[][] userNameValidationDataProvider() {
        return new Object[][]{
                // everything is null - wait for FAIL
                {null, null, FAIL},

                // "userName" is null - wait for FAIL
                {null, UUID.randomUUID(), FAIL},

                // "userName" is normal - wait for SUCCESS
                {random(randomNumber(1, 50), true, true), UUID.randomUUID(), SUCCESS},

                // "userName" is normal & uuid is null - wait for SUCCESS
                {random(randomNumber(1, 50), true, true), null, SUCCESS},

                // "userName" contains not only letters and numbers - wait for FAIL
                {randomString(1, 50) + "]}~", null, FAIL},

                // "userName" is empty string - wait for FAIL
                {"", UUID.randomUUID(), FAIL},
        };
    }

    @DataProvider
    public static Object[][] passwordForChangeValidationDataProvider() {
        return new Object[][]{
                // everything is null - wait for fail
                {null, null, null, FAIL},

                // "oldPassword" is null - wait for fail
                {null, randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH), randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH), FAIL},

                // "newPassword" is null - wait for fail
                {randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH), null, randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH), FAIL},

                // "newPassword" is null, and although "old" is correct - wait for fail
                {"1234567890", null, "1234567890", FAIL},

                // Change password on the same one - wait for fail
                {"1234567890", "1234567890", "1234567890", FAIL},

                // wait for success
                {"1234567890", "123456789", "1234567890", SUCCESS},
        };
    }

    @Test(dataProvider = "createValidationDataProvider")
    public void createValidationTest(UserDetailed user, boolean expected) {
        Map<String, Object> errors = userValidator.validateCreation(user);

        boolean actual = errors.isEmpty();

        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "updateValidationDataProvider")
    public void updateValidationTest(UserDetailed user, boolean expected) {
        Map<String, Object> errors = userValidator.validateUpdates(user);

        boolean actual = errors.isEmpty();

        Assert.assertEquals(actual, expected);
    }


    @Test(dataProvider = "passwordValidationDataProvider")
    public void passwordValidationTest(String password, boolean expected) {
        Map<String, Object> errors = new HashMap<>();

        userValidator.validatePassword(password, errors);

        boolean actual = errors.isEmpty();

        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "lettersOnlyValidationDataProvider")
    public void lettersOnlyValidationTest(String field, String fieldName, boolean expected) {
        Map<String, Object> errors = new HashMap<>();

        userValidator.validateLettersOnlyField(field, fieldName, errors);

        boolean actual = errors.isEmpty();

        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "initialsValidationDataProvider")
    public void initialsValidationTest(Initials initials, boolean expected) {
        Map<String, Object> errors = new HashMap<>();

        userValidator.validateInitials(initials, errors);

        boolean actual = errors.isEmpty();

        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "userNameValidationDataProvider")
    public void userNameValidationTest(String userName, UUID userUuid, boolean expected) {
        Map<String, Object> errors = new HashMap<>();

        userValidator.validateUserName(userName, userUuid, errors);

        boolean actual = errors.isEmpty();

        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "passwordForChangeValidationDataProvider")
    public void passwordForChangeValidationTest(String oldPassword, String newPassword, String passwordFromDB,
                                                boolean expected) {
        // Hash the password
        String passwordHashFromDB = passwordService.hash(passwordFromDB);

        // Test the method
        Map<String, Object> errors;
        errors = userValidator.validatePasswordForChange(oldPassword, newPassword, passwordHashFromDB);

        boolean actual = errors.isEmpty();

        Assert.assertEquals(actual, expected);
    }
}
