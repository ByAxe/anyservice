package com.anyservice;

import com.anyservice.config.TestNGConfig;
import com.anyservice.dto.UserDTO;
import com.anyservice.dto.enums.LegalStatus;
import com.anyservice.entity.Contacts;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.random;


public class UserServiceTest extends TestNGConfig {

    private String baseUrl = "/";

    @Test
    public void contextLoads() {
    }

    //    @Test
    public void createAndFindById() {

        UUID userUuid = UUID.randomUUID();

        UserDTO userDTO = UserDTO.builder()
                .uuid(userUuid)
                .legalStatus(LegalStatus.LLC)
                .isVerified(false)
                .isLegalStatusVerified(false)
                .description(random(255))
                .contacts(Contacts.builder()
                        .email(random(255))
                        .facebook(random(255))
                        .google(random(255))
                        .phone(random(255))
                        .build()
                )
                .dtCreate(OffsetDateTime.now())
                .dtUpdate(OffsetDateTime.now())
                .build();


    }

}
