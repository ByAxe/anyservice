package com.anyservice.tests.integration;

import com.anyservice.config.TestConfig;
import com.anyservice.core.DateUtils;
import com.anyservice.core.enums.FileExtension;
import com.anyservice.core.enums.FileType;
import com.anyservice.dto.api.APrimary;
import com.anyservice.dto.file.FileBrief;
import com.anyservice.dto.file.FileDetailed;
import com.anyservice.service.file.FileService;
import com.anyservice.tests.api.ICRUDTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlefebure.spring.boot.minio.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static com.anyservice.core.RandomValuesGenerator.randomNumber;
import static com.anyservice.core.RandomValuesGenerator.randomString;
import static com.anyservice.core.TestingUtilityClass.FAIL;
import static com.anyservice.core.TestingUtilityClass.SUCCESS;
import static com.anyservice.core.enums.FileType.DOCUMENT;
import static com.anyservice.core.enums.FileType.PROFILE_PHOTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

public class FileIntegrationTest extends TestConfig implements ICRUDTest<FileBrief, FileDetailed> {

    private final String baseUrl = "/api/v1/file";

    @Autowired
    private MinioService minioService;

    @Autowired
    private FileService fileService;

    @Override
    public Environment getEnvironment() {
        return environment;
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
        return FileBrief.class;
    }

    @Override
    public Class<? extends APrimary> getDetailedClass() {
        return FileDetailed.class;
    }

    @Override
    public void assertEqualsDetailed(FileDetailed actual, FileDetailed expected) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void assertEqualsListBrief(List<FileBrief> actualList, List<FileBrief> expectedList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileDetailed createNewItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUrl() {
        return "/file";
    }

    @DataProvider
    public static Object[][] uploadGetLoadDeleteDataProvider() {
        String fileName = "file";
        String contentType = "text/plain";

        // Get random photo extension
        List<FileExtension> photoExtensions = FileExtension.getPhotoFormats();
        FileExtension photoExtension = photoExtensions.get(randomNumber(0, photoExtensions.size() - 1));

        FileExtension documentExtension = FileExtension.pdf;

        byte[] tinyFile = randomString(1, 99).getBytes();

        return new Object[][]{
                {new MockMultipartFile(fileName, randomString(1, 50) + "." + photoExtension, photoExtension.getContentType(), tinyFile), PROFILE_PHOTO, SUCCESS},
                {new MockMultipartFile(fileName, randomString(1, 50) + "." + documentExtension, documentExtension.getContentType(), tinyFile), DOCUMENT, SUCCESS},

                // Expect fails because of a wrong file formats for this domain FileTypes
                {new MockMultipartFile(fileName, randomString(1, 50) + "." + randomString(1, 3), contentType, tinyFile), PROFILE_PHOTO, FAIL},
                {new MockMultipartFile(fileName, randomString(1, 50) + "." + randomString(1, 3), contentType, tinyFile), DOCUMENT, FAIL},
        };
    }

    @Test(dataProvider = "uploadGetLoadDeleteDataProvider")
    public void uploadGetLoadDeleteTest(MockMultipartFile originalFile, FileType type, boolean expectSuccess)
            throws Exception {

        // Choose expected status
        ResultMatcher expectedStatus = expectSuccess ? expectCreated : expectBadRequest;

        // Build url
        String url = baseUrl + "/upload/" + type.name();

        // Upload file
        ResultActions resultActions = mockMvc.perform(multipart(url)
                .file(originalFile)
                .headers(getHeaders()))
                .andExpect(expectedStatus);

        // If FAIL was expected - finish test here
        if (!expectSuccess) return;

        // Get header location
        String headerLocation = resultActions.andReturn()
                .getResponse()
                .getHeader("Location");

        // Get created file identifier
        UUID uuid = getUuidFromHeaderLocation(headerLocation);

        // Get created file metadata
        String contentAsString = mockMvc.perform(get(baseUrl + "/" + uuid)
                .headers(getHeaders())
                .contentType(getContentType()))
                .andExpect(expectOk)
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Covert obtained result to object
        FileDetailed obtainedMetadata = getObjectMapper().readValue(contentAsString,
                getObjectMapper().getTypeFactory().constructType(getDetailedClass()));

        // Compare created file metadata with the source one
        Assert.assertEquals(obtainedMetadata.getUuid(), uuid);
        Assert.assertEquals(obtainedMetadata.getName(), originalFile.getOriginalFilename());

        // Load file by uuid
        MockHttpServletResponse obtainedFile = mockMvc.perform(get(baseUrl + "/" + uuid + "/load")
                .headers(getHeaders())
                .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(expectOk)
                .andReturn()
                .getResponse();

        // Compare sizes
        Assert.assertEquals(obtainedFile.getContentAsByteArray().length, originalFile.getBytes().length);

        // Compare content types
        Assert.assertEquals(obtainedFile.getContentType(), originalFile.getContentType());

        // Compare contents
        Assert.assertEquals(obtainedFile.getContentAsByteArray(), originalFile.getBytes());

        long version = DateUtils.convertOffsetDateTimeToMills(obtainedMetadata.getDtCreate());

        // Remove file and expect that there will be no content at the end (actually deleted)
        remove(uuid, version);

        // Get path to file
        Path pathToFile = fileService.getPathToFile(type, uuid);

        // Try to get file from storage directly
        InputStream inputStream = minioService.get(pathToFile);

        // Get amount of available bytes
        int available = inputStream.available();

        // Expect that nothing was found
        Assert.assertEquals(available, 0);
    }
}
