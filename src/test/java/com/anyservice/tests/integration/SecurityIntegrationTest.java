package com.anyservice.tests.integration;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.web.security.dto.Login;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testng.annotations.Test;

import static com.anyservice.core.RandomValuesGenerator.randomString;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MAX_LENGTH;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MIN_LENGTH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityIntegrationTest extends UserIntegrationTest {

    @Value("${security.jwt.header}")
    private String jwtHeader;

    /**
     * Create user and try to log in
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void loginTest() throws Exception {
        // Create empty headers
        HttpHeaders headers = new HttpHeaders();

        // Access special method that is allowed only for authenticated users
        checkIfAuthenticated(headers, expectUnauthorized);

        // Generate password
        String password = randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

        // Build a user
        UserDetailed user = createNewItem();
        user.setPassword(password);

        // Create user
        create(user, expectDefault);

        // Get user name of created user
        String userName = user.getUserName();

        // Build login-specific object
        Login login = Login.builder()
                .password(password)
                .userName(userName)
                .build();

        // Convert it to string
        String loginAsString = getObjectMapper().writeValueAsString(login);

        // try to login
        String token = getMockMvc().perform(post(getExtendedUrl() + "/login")
                .headers(headers)
                .contentType(getContentType())
                .content(loginAsString))
                .andExpect(expectOk)
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Check if it's not null
        Assert.assertNotNull(token);

        // Put token into headers
        headers.add(jwtHeader, token);

        // Access special method that is allowed only for authenticated users
        checkIfAuthenticated(headers, expectOk);
    }

    /**
     * Access special method that is allowed only for authenticated users
     *
     * @param headers headers that may contain or not the valid token
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    private void checkIfAuthenticated(HttpHeaders headers, ResultMatcher expect) throws Exception {
        getMockMvc().perform(get(getExtendedUrl() + "/authenticated")
                .headers(headers))
                .andExpect(expect);
    }

    /**
     * Log out of application
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void logoutTest() throws Exception {
        getMockMvc().perform(get(getExtendedUrl() + "/logout")
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expectOk);
    }

    /**
     * Try to refresh token without token in headers
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void refreshTokenWithoutUserTest() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();

        getMockMvc().perform(put(getExtendedUrl() + "/refresh")
                .headers(httpHeaders)
                .contentType(getContentType()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Refresh token for existing user
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void refreshTokenTest() throws Exception {
        getMockMvc().perform(put(getExtendedUrl() + "/refresh")
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isOk());
    }

}
