package com.anyservice.tests.integration;

import com.anyservice.config.TestConfig;
import com.anyservice.core.DateUtils;
import com.anyservice.core.FileTestUtils;
import com.anyservice.dto.DetailedWrapper;
import com.anyservice.dto.api.APrimary;
import com.anyservice.dto.file.FileDetailed;
import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.dto.user.UserForChangePassword;
import com.anyservice.entity.user.Contacts;
import com.anyservice.entity.user.CountryEntity;
import com.anyservice.entity.user.Initials;
import com.anyservice.repository.CountryRepository;
import com.anyservice.service.api.IUserService;
import com.anyservice.tests.api.ICRUDTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.cp.internal.util.Tuple2;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static com.anyservice.core.RandomValuesGenerator.randomNumber;
import static com.anyservice.core.RandomValuesGenerator.randomString;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MAX_LENGTH;
import static com.anyservice.core.TestingUtilityClass.PASSWORD_MIN_LENGTH;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


public class UserIntegrationTest extends TestConfig implements ICRUDTest<UserBrief, UserDetailed> {

    @Autowired
    private IUserService userService;

    protected FileTestUtils fileTestUtils;

    private final List<CountryEntity> countries = new ArrayList<>();

    @Autowired
    private CountryRepository countryRepository;

    @Value("${user.validation.password.length.min}")
    private int passwordMinLength;

    @Value("${user.validation.password.length.max}")
    private int passwordMaxLength;

    @Value("${spring.mail.username}")
    private String mailLogin;

    @Override
    public Environment getEnvironment() {
        return environment;
    }

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
        Assert.assertEquals(actual.isLegalStatusVerified(), expected.isLegalStatusVerified());
        Assert.assertEquals(actual.isVerified(), expected.isVerified());
        Assert.assertEquals(actual.getLegalStatus(), expected.getLegalStatus());
    }

    /**
     * Data provider for {@link this#createAndDeleteUserWithDocumentsAndPortfolioTest(int, int)} test
     *
     * @return [documentsAmount, portfolioAmount]
     */
    @DataProvider
    public static Object[][] createAndDeleteUserWithDocumentsAndPortfolioDataProvider() {
        return new Object[][]{
                {5, 20},
                {10, 0},
                {0, 10},
                {0, 0},
                {100, 100}
        };
    }

    @Override
    public void assertEqualsListBrief(List<UserBrief> actualList, List<UserBrief> expectedList) {
        actualList.sort(comparing(UserBrief::getUserName));
        expectedList.sort(comparing(UserBrief::getUserName));

        Assert.assertEquals(actualList.size(), expectedList.size());

        for (int element = 0; element < actualList.size(); element++) {
            UserBrief user = actualList.get(element);
            UserBrief otherUser = expectedList.get(element);

            Assert.assertEquals(user.getInitials(), otherUser.getInitials());
            Assert.assertEquals(user.getUserName(), otherUser.getUserName());
        }
    }

    /**
     * Create fixed amount of countries and return random one
     *
     * @return random created country
     */
    @Synchronized
    private CountryEntity createCountry() {
        if (countries.isEmpty()) {
            // Put all countries into list
            countries.addAll(countryRepository.findAll());
        }

        // Return random one
        return countries.get(randomNumber(0, countries.size() - 1));

    }

    /**
     * Create countries where services is available
     *
     * @return randomly created services countries list
     */
    @Synchronized
    private List<CountryEntity> createServiceCountries() {
        if (countries.isEmpty()) {
            // Put all countries into list
            countries.addAll(countryRepository.findAll());
        }

        // Use Set to avoid duplicates
        Set<CountryEntity> countriesSet = new HashSet<>();

        // Get random amount of countries to generate
        int randomAmountOfAvailableCountries = randomNumber(0, countries.size() - 1);

        // Add to set this random amount of countries
        for (int i = 0; i < randomAmountOfAvailableCountries; i++) {
            CountryEntity country = countries.get(randomNumber(0, countries.size() - 1));

            countriesSet.add(country);
        }

        // Return list without duplicating countries
        return new ArrayList<>(countriesSet);
    }

    /**
     * Generate random initials of a user
     *
     * @return random Initials
     */
    private Initials createInitials() {
        return Initials.builder()
                .firstName(random(randomNumber(2, 100), true, false))
                .middleName(random(randomNumber(2, 100), true, false))
                .lastName(random(randomNumber(2, 100), true, false))
                .build();
    }

    /**
     * Generate random contacts for a user
     *
     * @return random Contacts
     */
    private Contacts createContacts() {
        return Contacts.builder()
                .phone(random(randomNumber(6, 50), false, true))
                .email(mailLogin)
                .google(randomString(5, 100))
                .facebook(randomString(5, 100))
                .build();
    }

    /**
     * Generate random number of random addresses
     *
     * @return map of random addresses
     */
    private Map<String, String> createAddresses() {

        // Generate random amount of address
        int addressesAmount = randomNumber(0, 10);

        // No addresses means null
        if (addressesAmount == 0) return null;

        Map<String, String> addresses = new HashMap<>();

        // Generate random addresses
        for (int i = 0; i < addressesAmount; i++) {
            String randomAddressName = randomString(1, 50);
            String randomAddressLocation = randomString(1, 100);

            addresses.put(randomAddressName, randomAddressLocation);
        }

        return addresses;
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

    @Test
    @Override
    public void createAndCheckIfExists() throws Exception {
        ICRUDTest.super.createAndCheckIfExists();
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

    /**
     * Create user to ensure he does not have a password
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void getUserWithoutPassword() throws Exception {
        // Create user
        DetailedWrapper<UserDetailed> userWrapper = create();

        // Find user by id
        UserDetailed selectedUser = select(userWrapper.getUuid());

        // make sure password is null
        Assert.assertNull(selectedUser.getPassword());
    }

    /**
     * Create&Update user and make sure its password wasn't changed unintentionally, since creation
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void updateUserButPasswordMustNotChange() throws Exception {
        // Generate password
        String password = randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

        // Build a new user
        UserDetailed user = createNewItem();
        user.setPassword(password);

        // Create user
        DetailedWrapper<UserDetailed> userWrapper = create(user, expectDefault);

        // Get uuid of created user
        UUID uuid = userWrapper.getUuid();

        // Select created user
        UserDetailed createdUser = select(uuid);

        // Get its latest "version"
        long version = DateUtils.convertOffsetDateTimeToMills(createdUser.getDtUpdate());

        // Update user
        update(uuid, version);

        /* If changing operation will be successful - password wasn't changed, since creation */

        // Generate a new password for the user
        String newPassword = randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

        // Change password, using our first password
        changePassword(uuid, password, newPassword, expectOk);
    }

    @Override
    public UserDetailed createNewItem() {
        return UserDetailed.builder()
                .initials(createInitials())
                .contacts(createContacts())
                .userName(randomString(3, 50))
                .description(randomString(0, 1000))
                .isVerified(false)
                .isLegalStatusVerified(false)
                .legalStatus(null)
                .password(random(randomNumber(passwordMinLength, passwordMaxLength), true, true))
                .addresses(createAddresses())
                .defaultCountry(createCountry())
                .listOfCountriesWhereServicesProvided(createServiceCountries())
                .build();
    }

    /**
     * Create user and  find it via its userName
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void findUserForLoginTest() throws Exception {
        // Generate password
        String password = randomString(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

        // Build a new user
        UserDetailed user = createNewItem();
        user.setPassword(password);

        // Get user name of a user
        String userName = user.getUserName();

        // Create user
        DetailedWrapper<UserDetailed> userWrapper = create(user, expectDefault);

        // Get uuid
        UUID uuid = userWrapper.getUuid();

        // Select created user
        UserDetailed expectedUser = select(uuid);

        // Try to get user via given criteria
        UserDetailed actualUser = userService.findUserForLogin(userName, password);

        // If we got here, search was successful
        assertEqualsDetailed(actualUser, expectedUser);
    }

    /**
     * Create user with profile photo and delete it when made sure that created one are equal to expected,
     * as well as created profile photo
     *
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test
    public void createAndDeleteUserWithProfilePhoto() throws Exception {
        // Create test utils needed for this test
        initializeFileTestUtils();

        // Prepare user
        UserDetailed userDetailed = createNewItem();

        // Create profile photo
        Tuple2<MockMultipartFile, FileDetailed> photoTuple = fileTestUtils.createProfilePhoto();
        MockMultipartFile multipartFile = photoTuple.element1;
        FileDetailed fileDetailed = photoTuple.element2;

        // Add profile photo
        userDetailed.setProfilePhoto(fileDetailed);

        // Create user
        DetailedWrapper<UserDetailed> userWrapper = create(userDetailed);
        UUID userUuid = userWrapper.getUuid();

        // Select created user
        UserDetailed selectedUser = select(userUuid);

        // Assert users are equal
        assertEqualsDetailed(selectedUser, userDetailed);

        // Get selected user's profile photo
        FileDetailed selectedUserProfilePhoto = selectedUser.getProfilePhoto();

        // Compare created file metadata with the source one
        Assert.assertEquals(selectedUserProfilePhoto.getUuid(), fileDetailed.getUuid());
        Assert.assertEquals(selectedUserProfilePhoto.getName(), multipartFile.getOriginalFilename());

        // Get version from selected user
        long version = DateUtils.convertOffsetDateTimeToMills(selectedUser.getDtCreate());

        // Remove user and file
        remove(userUuid, version);
    }

    /**
     * Create user with given amount of documents and portfolio elements and expect everything was created as expected.
     * Then, delete all the created documents
     *
     * @param documentsAmount amount of documents for user
     * @param portfolioAmount amount of portfolio elements for user
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    @Test(dataProvider = "createAndDeleteUserWithDocumentsAndPortfolioDataProvider")
    public void createAndDeleteUserWithDocumentsAndPortfolioTest(int documentsAmount, int portfolioAmount)
            throws Exception {
        // Create test utils needed for this test
        initializeFileTestUtils();

        // Prepare user
        UserDetailed userDetailed = createNewItem();

        List<Tuple2<MockMultipartFile, FileDetailed>> documents = null;
        List<Tuple2<MockMultipartFile, FileDetailed>> portfolio = null;

        // Create bunch of documents and set it to user
        if (documentsAmount != 0) {
            documents = fileTestUtils.createDocuments(documentsAmount);
            List<FileDetailed> documentsList = fileTestUtils.extractFiles(documents, fileTestUtils::extractDTOFiles);
            userDetailed.setDocuments(documentsList);
        }

        if (portfolioAmount != 0) {
            portfolio = fileTestUtils.createPortfolio(portfolioAmount);
            List<FileDetailed> portfolioList = fileTestUtils.extractFiles(portfolio, fileTestUtils::extractDTOFiles);
            userDetailed.setPortfolio(portfolioList);
        }


        // Create user
        DetailedWrapper<UserDetailed> userWrapper = create(userDetailed);
        UUID userUuid = userWrapper.getUuid();

        // Select created user
        UserDetailed selectedUser = select(userUuid);

        // Assert users are equal
        assertEqualsDetailed(selectedUser, userDetailed);

        // Get selected user's documents and portfolio
        List<FileDetailed> selectedUserDocuments = selectedUser.getDocuments();
        List<FileDetailed> selectedUserPortfolio = selectedUser.getPortfolio();

        // Assert these lists are not null and not empty
        if (documentsAmount != 0) {
            Assert.assertNotNull(selectedUserDocuments);

            if (selectedUserDocuments.isEmpty())
                throw new AssertionError("Selected user documents list should not be empty!");

            // Assert lists of files are equal
            fileTestUtils.assertFilesDTOFromListsAreEqual(selectedUserDocuments, documents);

        }

        // Assert these lists are not null and not empty
        if (portfolioAmount != 0) {
            Assert.assertNotNull(selectedUserPortfolio);

            if (selectedUserPortfolio.isEmpty())
                throw new AssertionError("Selected user portfolio list should not be empty!");

            // Assert lists of files are equal
            fileTestUtils.assertFilesDTOFromListsAreEqual(selectedUserPortfolio, portfolio);
        }

        // Get version from selected user
        long version = DateUtils.convertOffsetDateTimeToMills(selectedUser.getDtCreate());

        // Remove user and all its files
        remove(userUuid, version);
    }

    @Synchronized
    private void initializeFileTestUtils() {
        if (fileTestUtils == null) {
            fileTestUtils = new FileTestUtils(getMockMvc(), getObjectMapper(), getHeaders());
        }
    }
}
