package com.anyservice.core;

import com.anyservice.core.enums.FileExtension;
import com.anyservice.core.enums.FileType;
import com.anyservice.dto.file.FileBrief;
import com.anyservice.dto.file.FileDetailed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.hazelcast.cp.internal.util.Tuple2;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.anyservice.core.RandomValuesGenerator.randomString;
import static com.anyservice.core.enums.FileExtension.jpg;
import static com.anyservice.core.enums.FileExtension.pdf;
import static com.anyservice.core.enums.FileType.*;
import static com.anyservice.tests.api.ICRUDOperations.expectCreated;
import static java.util.Comparator.comparing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

public class FileTestUtils {
    private final String fileBaseUrl = "/api/v1/file";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final HttpHeaders headers;

    public FileTestUtils(MockMvc mockMvc, ObjectMapper objectMapper, HttpHeaders headers) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.headers = headers;
    }

    /**
     * Assert that two lists are equal, with help of {@link this#assertFileDTOAreEqual(FileDetailed, Tuple2)}
     *
     * @param actualList   actual file list
     * @param expectedList list of tuples of [original file (element1) --- dto file (element2)]
     */
    public void assertFilesDTOFromListsAreEqual(List<FileDetailed> actualList,
                                                List<Tuple2<MockMultipartFile, FileDetailed>> expectedList) {

        // Sort via name all expected and actual data
        actualList.sort(comparing(FileBrief::getName));
        expectedList.sort(comparing(t -> t.element2.getName()));

        // Assert sizes are equal
        Assert.assertEquals(actualList.size(), expectedList.size());

        // Iterate though each element
        for (int element = 0; element < actualList.size(); element++) {
            FileDetailed actual = actualList.get(element);
            Tuple2<MockMultipartFile, FileDetailed> expected = expectedList.get(element);

            // Compare elements one by one
            assertFileDTOAreEqual(actual, expected);
        }
    }

    /**
     * Assert that actual and file dto from expected are equal
     *
     * @param actual   actual file
     * @param expected tuple of [original file (element1) --- dto file (element2)]
     */
    public void assertFileDTOAreEqual(FileDetailed actual, Tuple2<MockMultipartFile, FileDetailed> expected) {
        // Extract expected objects into separate variables
        FileDetailed expectedFileDTO = expected.element2;
        MockMultipartFile expectedOriginalFile = expected.element1;

        // Assert everything is equal
        Assert.assertEquals(actual.getUuid(), expectedFileDTO.getUuid());
        Assert.assertEquals(actual.getName(), expectedOriginalFile.getOriginalFilename());
        Assert.assertEquals(actual.getFileType(), expectedFileDTO.getFileType());
        Assert.assertEquals(actual.getExtension(), expectedFileDTO.getExtension());
        Assert.assertEquals(actual.getState(), expectedFileDTO.getState());
        Assert.assertEquals(actual.getSize(), expectedFileDTO.getSize());
        Assert.assertEquals(actual.getDtCreate(), expectedFileDTO.getDtCreate());
    }

    /**
     * Creates given amount of {@link FileType#DOCUMENT} files with help of {@link this#createDocument()}
     *
     * @param amount amount of created documents
     * @return list of tuples of documents
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    public List<Tuple2<MockMultipartFile, FileDetailed>> createDocuments(int amount) throws Exception {
        List<Tuple2<MockMultipartFile, FileDetailed>> documents = new ArrayList<>();

        for (int i = 0; i < amount; i++) documents.add(createDocument());

        return documents;
    }

    /**
     * Creates given amount of {@link FileType#PORTFOLIO} files with help of {@link this#createPortfolio()}
     *
     * @param amount amount of created portfolio files
     * @return list of tuples of portfolio files
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    public List<Tuple2<MockMultipartFile, FileDetailed>> createPortfolio(int amount) throws Exception {
        List<Tuple2<MockMultipartFile, FileDetailed>> portfolio = new ArrayList<>();

        for (int i = 0; i < amount; i++) portfolio.add(createPortfolio());

        return portfolio;
    }

    /**
     * Overloaded version of method {@link this#createFile(FileType, FileExtension)}
     * With predefined file type and file extension for {@link FileType#PORTFOLIO}
     *
     * @return tuple of [original file (element1) --- dto file (element2)]
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    public Tuple2<MockMultipartFile, FileDetailed> createPortfolio() throws Exception {
        return createFile(PORTFOLIO, jpg);
    }

    /**
     * Overloaded version of method {@link this#createFile(FileType, FileExtension)}
     * With predefined file type and file extension for {@link FileType#DOCUMENT}
     *
     * @return tuple of [original file (element1) --- dto file (element2)]
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    public Tuple2<MockMultipartFile, FileDetailed> createDocument() throws Exception {
        return createFile(DOCUMENT, pdf);
    }

    /**
     * Overloaded version of method {@link this#createFile(FileType, FileExtension)}
     * With predefined file type and file extension for {@link FileType#PROFILE_PHOTO}
     *
     * @return tuple of [original file (element1) --- dto file (element2)]
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    public Tuple2<MockMultipartFile, FileDetailed> createProfilePhoto() throws Exception {
        return createFile(PROFILE_PHOTO, jpg);
    }

    /**
     * Utility method for creation of file with given {@link FileType} and {@link FileExtension}
     *
     * @param type      file type (domain)
     * @param extension file extension (jpg, png, pdf and etc.)
     * @return tuple of [original file (element1) --- dto file (element2)]
     * @throws Exception if something goes wrong - let interpret it as failed test
     */
    public Tuple2<MockMultipartFile, FileDetailed> createFile(FileType type, FileExtension extension)
            throws Exception {
        // Prepare data for file
        String fileName = "file";
        String contentType = "text/plain";
        byte[] tinyFile = randomString(1, 99).getBytes();
        String originalFileName = randomString(1, 50) + "." + extension;

        // Build file
        MockMultipartFile originalFile = new MockMultipartFile(fileName, originalFileName,
                extension.getContentType(), tinyFile);

        // Build url
        String url = fileBaseUrl + "/upload/" + type.name();

        // Upload file
        String contentAsString = mockMvc.perform(multipart(url)
                .file(originalFile)
                .headers(headers))
                .andExpect(expectCreated)
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Covert returned file metadata to object
        FileDetailed createdFile = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructType(FileDetailed.class));

        return Tuple2.of(originalFile, createdFile);
    }

    /**
     * Extract files from list of tuples via given {@link Function}
     *
     * @param listOfTuples original storage
     * @param function     function of extraction (what to extract from each tuple
     * @param <T>          returning result of extraction from tuple
     * @return List of {@link T}
     */
    public <T> List<T> extractFiles(List<Tuple2<MockMultipartFile, FileDetailed>> listOfTuples,
                                    Function<Tuple2<MockMultipartFile, FileDetailed>, T> function) {
        return listOfTuples.stream()
                .map(function)
                .collect(Collectors.toList());
    }

    /**
     * Method for extraction of element1 from {@link Tuple2}
     *
     * @param tuple source tuple
     * @return element1
     */
    public MockMultipartFile extractOriginalFiles(Tuple2<MockMultipartFile, FileDetailed> tuple) {
        return tuple.element1;
    }

    /**
     * Method for extraction of element1 from {@link Tuple2}
     *
     * @param tuple source tuple
     * @return element2
     */
    public FileDetailed extractDTOFiles(Tuple2<MockMultipartFile, FileDetailed> tuple) {
        return tuple.element2;
    }

}
