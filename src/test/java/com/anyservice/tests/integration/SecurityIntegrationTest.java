package com.anyservice.tests.integration;

import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.DetailedWrapper;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.user.UserEntity;
import com.anyservice.repository.UserRepository;
import com.anyservice.web.security.dto.InfiniteToken;
import com.anyservice.web.security.dto.Login;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.anyservice.core.RandomValuesGenerator.randomString;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MAX_LENGTH;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MIN_LENGTH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class SecurityIntegrationTest extends UserIntegrationTest {

    @Value("${security.jwt.header}")
    private String jwtHeader;

    @Value("${security.jwt.never}")
    private Long never;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversionService conversionService;

    /**
     * Create user, verify it and try to log in
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
        DetailedWrapper<UserDetailed> userDetailedDetailedWrapper = create(user, expectDefault);
        UUID uuid = userDetailedDetailedWrapper.getUuid();

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

        // Verify created user
        verifyUser(uuid);

        // Access special method that is allowed only for authenticated users
        checkIfAuthenticated(headers, expectOk);
    }

    /**
     * Verify user with given identifier
     *
     * @param uuid user identifier
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    private void verifyUser(UUID uuid) throws Exception {
        // Select user via identifier
        UserDetailed selectedUser = select(uuid);

        // Get verification keys storage
        Cache verificationCodeMap = cacheManager.getCache("verificationCodeMap");

        // Find needed verification code
        UUID verificationCode = verificationCodeMap.get(uuid, UUID.class);

        // Assert it's present
        Assert.assertNotNull(verificationCode);

        // Verify user with this verification code
        UserDetailed actual = verifyUser(uuid, verificationCode, expectOk);

        // Assert that verified user is the one we needed to verify
        assertEqualsDetailed(actual, selectedUser);
    }

    /**
     * Verify user with given verification code
     *
     * @param uuid   user identifier
     * @param code   verification code
     * @param expect what response to expect
     * @return verified user
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    private UserDetailed verifyUser(UUID uuid, UUID code, ResultMatcher expect) throws Exception {
        String contentAsString = getMockMvc().perform(
                get(getExtendedUrl() + "/verification/" + uuid + "/" + code)
                        .headers(getHeaders())
                        .contentType(getContentType()))
                .andExpect(expect)
                .andReturn()
                .getResponse()
                .getContentAsString();

        return getObjectMapper().readValue(contentAsString,
                getObjectMapper().getTypeFactory().constructType(getDetailedClass()));
    }

    /**
     * Access special method that is allowed only for authenticated users
     *
     * @param headers headers that may contain or not the valid token
     * @return special request object that can be parsed further if everthing was ok with query
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    private ResultActions checkIfAuthenticated(HttpHeaders headers, ResultMatcher expect) throws Exception {
        ResultActions resultActions = getMockMvc().perform(get(getExtendedUrl() + "/authenticated")
                .headers(headers))
                .andExpect(expect);

        return resultActions;
    }

    /**
     * Access special method that is allowed only for authenticated users
     * <p>
     * Expect by default that query is ok and parse results into {@link UserRole}
     *
     * @param headers headers that may contain or not the valid token
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    private UserRole checkIfAuthenticated(HttpHeaders headers) throws Exception {
        String userRoleAsString = checkIfAuthenticated(headers, expectOk)
                .andReturn()
                .getResponse()
                .getContentAsString();

        int ordinal = Integer.parseInt(userRoleAsString);

        return UserRole.values()[ordinal];
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
                .andExpect(expectUnauthorized);
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
                .andExpect(expectOk);
    }

    /**
     * Create user and try to get infinite token for it
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void generateInfiniteTokenTest() throws Exception {
        // Build user
        UserDetailed user = createNewItem();

        // Convert it to entity
        UserEntity entity = conversionService.convert(user, UserEntity.class);

        // set all necessary fields to it
        UUID uuid = UUID.randomUUID();
        entity.setUuid(uuid);

        String userName = randomString(1, 50);
        entity.setUserName(userName);

        OffsetDateTime now = OffsetDateTime.now();

        entity.setDtCreate(now);
        entity.setDtUpdate(now);
        entity.setPasswordUpdateDate(now);

        // Set super admin role to be able to test target method further
        UserRole roleSuperAdmin = UserRole.ROLE_SUPER_ADMIN;

        entity.setRole(roleSuperAdmin.name());
        entity.setState(UserState.ACTIVE.name());

        // Create user directly through repository layer
        userRepository.saveAndFlush(entity);

        // Build specific object
        InfiniteToken infiniteToken = InfiniteToken.builder()
                .ttl(never)
                .userName(userName)
                .build();

        // Convert specific object to string representation
        String infiniteTokenAsString = getObjectMapper().writeValueAsString(infiniteToken);

        // generate infinite token for it
        String generatedInfiniteToken = getMockMvc()
                .perform(post(getExtendedUrl() + "/generate/infinite/token")
                        .headers(getHeaders())
                        .contentType(getContentType())
                        .content(infiniteTokenAsString))
                .andExpect(expectOk)
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Ensure generated token is not null
        Assert.assertNotNull(generatedInfiniteToken);

        // Build headers with infinite token
        HttpHeaders headers = new HttpHeaders();
        headers.add(jwtHeader, generatedInfiniteToken);

        // Access special method that is allowed only for authenticated users
        UserRole userRole = checkIfAuthenticated(headers);

        // Ensure it's actually a role we have created
        Assert.assertEquals(userRole, roleSuperAdmin);
    }
}
