package com.anyservice.tests.unit;

import com.anyservice.config.TestConfig;
import com.anyservice.service.user.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.anyservice.core.RandomValuesGenerator.randomString;
import static com.anyservice.core.TestingUtilityClass.FAIL;
import static com.anyservice.core.TestingUtilityClass.SUCCESS;

public class PasswordServiceUnitTest extends TestConfig {

    @Autowired
    private PasswordService passwordService;

    @DataProvider
    public static Object[][] passwordHashingDataProvider() {
        return new Object[][]{
                // password is null - wait for SUCCESS
                {null, SUCCESS},

                // password is empty - wait for SUCCESS
                {"", SUCCESS},

                // password is any string - wait for SUCCESS
                {randomString(1, 1000), SUCCESS}
        };
    }

    @DataProvider
    public static Object[][] passwordVerificationDataProvider() {
        return new Object[][]{
                // password is null - wait for SUCCESS
                {null, SUCCESS},

                // password is empty - wait for SUCCESS
                {"", SUCCESS},

                // password is any string - wait for SUCCESS
                {randomString(1, 1000), SUCCESS}
        };
    }

    @Test(dataProvider = "passwordHashingDataProvider")
    public void passwordHashingTest(String password, boolean expected) {
        String hash;

        boolean actual = SUCCESS;

        try {
            hash = passwordService.hash(password);
        } catch (Exception e) {
            actual = FAIL;
        }

        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "passwordVerificationDataProvider")
    public void passwordVerificationTest(String password, boolean expected) {
        String hash = passwordService.hash(password);

        boolean actual = passwordService.verifyHash(password, hash);

        Assert.assertEquals(actual, expected);
    }

}
