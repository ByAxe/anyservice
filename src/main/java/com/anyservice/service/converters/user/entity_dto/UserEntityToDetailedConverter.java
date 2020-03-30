package com.anyservice.service.converters.user.entity_dto;

import com.anyservice.core.enums.LegalStatus;
import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.file.FileDetailed;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.file.FileEntity;
import com.anyservice.entity.user.UserEntity;
import com.anyservice.service.converters.file.entity_dto.FileEntityToDetailedConverter;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

import static com.anyservice.core.enums.FileType.DOCUMENT;
import static com.anyservice.core.enums.FileType.PORTFOLIO;

public class UserEntityToDetailedConverter implements Converter<UserEntity, UserDetailed> {

    private final FileEntityToDetailedConverter fileConverter;

    public UserEntityToDetailedConverter(FileEntityToDetailedConverter fileConverter) {
        this.fileConverter = fileConverter;
    }

    @Override
    public UserDetailed convert(UserEntity source) {

        // Build normal user detailed DTO
        UserDetailed userDetailed = UserDetailed.builder()
                .uuid(source.getUuid())
                .dtUpdate(source.getDtUpdate())
                .dtCreate(source.getDtCreate())
                .passwordUpdateDate(source.getPasswordUpdateDate())
                .userName(source.getUserName())
                .contacts(source.getContacts())
                .description(source.getDescription())
                .isLegalStatusVerified(source.getIsLegalStatusVerified())
                .isVerified(source.getIsVerified())
                .legalStatus(source.getLegalStatus() != null ? LegalStatus.valueOf(source.getLegalStatus()) : null)
                .initials(source.getInitials())
                .role(source.getRole() != null ? UserRole.valueOf(source.getRole()) : null)
                .state(source.getState() != null ? UserState.valueOf(source.getState()) : null)
                .addresses(source.getAddresses())
                .password(source.getPassword())
                .defaultCountry(source.getCountry())
                .profilePhoto(source.getPhoto() != null ? fileConverter.convert(source.getPhoto()) : null)
                .listOfCountriesWhereServicesProvided(source.getCountries())
                .build();

        // Get all the files from source
        List<FileEntity> files = source.getDocuments();

        // If there are some - let's put them in a right places
        if (files != null && !files.isEmpty()) {

            // Get all the documents
            List<FileDetailed> documents = files.stream()
                    .filter(d -> DOCUMENT.name().equals(d.getType()))
                    .map(fileConverter::convert)
                    .collect(Collectors.toList());

            // If there are some - set them to user detailed instance
            if (!documents.isEmpty()) userDetailed.setDocuments(documents);

            // Get all the portfolio documents
            List<FileDetailed> portfolio = files.stream()
                    .filter(d -> PORTFOLIO.name().equals(d.getType()))
                    .map(fileConverter::convert)
                    .collect(Collectors.toList());

            // If there are some - set them to user detailed instance
            if (!portfolio.isEmpty()) userDetailed.setPortfolio(portfolio);
        }

        return userDetailed;
    }
}
