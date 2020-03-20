package com.anyservice.tests.integration;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.web.security.JwtUtil;
import com.anyservice.web.security.dto.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static com.anyservice.core.RandomValuesGenerator.randomString;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MAX_LENGTH;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MIN_LENGTH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityIntegrationTest extends UserIntegrationTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void loginTest() throws Exception {
        // Generate password
        String password = randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

        // Build a user
        UserDetailed user = createNewItem();
        user.setPassword(password);

        // Create user
        create(user, expectDefault);

        // Get user name of created user
        String userName = user.getUserName();

        Login login = Login.builder()
                .password(password)
                .userName(userName)
                .build();

        String loginAsString = getObjectMapper().writeValueAsString(login);

        // try to login
        getMockMvc().perform(post(getExtendedUrl() + "/login")
                .headers(getHeaders())
                .contentType(getContentType())
                .content(loginAsString))
                .andExpect(expectOk);
    }

    @Test
    public void logoutTest() throws Exception {
        getMockMvc().perform(get(getExtendedUrl() + "/logout")
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expectOk);
    }

    @Test
    public void refreshTokenWithoutUserTest() throws Exception {
        getMockMvc().perform(put(getExtendedUrl() + "/refresh")
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void refreshTokenTest() throws Exception {


        getMockMvc().perform(put(getExtendedUrl() + "/refresh")
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(status().isInternalServerError());
    }

}
