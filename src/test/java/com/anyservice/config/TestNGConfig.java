package com.anyservice.config;

import com.anyservice.AnyServiceApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {AnyServiceApplication.class}
)
@TestPropertySource(locations = {
        "classpath:environment.properties"
})
@DirtiesContext
@ActiveProfiles("alone")
public abstract class TestNGConfig extends AbstractTransactionalTestNGSpringContextTests {

    @Value("${security.be.token}")
    public String beToken;
    @Value("${security.be.header}")
    public String beHeader;
    @Value("${security.user.details.token}")
    public String currentUserToken;
    @Value("${security.user.details.header}")
    public String currentUserHeader;
    protected MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);
    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    protected ObjectMapper objectMapper;
    @Resource
    private FilterChainProxy springSecurityFilterChain;
    @Autowired
    private Environment environment;

    @BeforeMethod
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
    }

    @BeforeClass
    public void prepareDataBase() {
//        executeSqlScript("classpath:sql_test/00_directory-user.sql", false);
//        executeSqlScript("classpath:sql_test/01_directory-ddl.sql", false);
    }

    @BeforeSuite(alwaysRun = true)
    protected void setupSpringAutowiring() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        super.springTestContextBeforeTestClass();
        super.springTestContextPrepareTestInstance();
    }

    public String getCurrentUserToken() {
        return currentUserToken;
    }

    public String getCurrentUserHeader() {
        return currentUserHeader;
    }

    public String getBeUser() {
        return beToken;
    }

    public String getBeHeader() {
        return beHeader;
    }
}
