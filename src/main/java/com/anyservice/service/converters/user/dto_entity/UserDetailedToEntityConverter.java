package com.anyservice.service.converters.user.dto_entity;

import com.anyservice.dto.file.FileDetailed;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.file.FileEntity;
import com.anyservice.entity.user.UserEntity;
import com.anyservice.service.converters.file.dto_entity.FileDetailedToEntityConverter;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailedToEntityConverter implements Converter<UserDetailed, UserEntity> {

    private final FileDetailedToEntityConverter fileConverter;

    public UserDetailedToEntityConverter(FileDetailedToEntityConverter fileConverter) {
        this.fileConverter = fileConverter;
    }

    @Override
    public UserEntity convert(UserDetailed source) {
        UserEntity userEntity = UserEntity.builder()
                .uuid(source.getUuid())
                .dtCreate(source.getDtCreate())
                .dtUpdate(source.getDtUpdate())
                .password(source.getPassword())
                .passwordUpdateDate(source.getPasswordUpdateDate())
                .contacts(source.getContacts())
                .description(source.getDescription())
                .isLegalStatusVerified(source.isLegalStatusVerified())
                .isVerified(source.isVerified())
                .legalStatus(source.getLegalStatus() != null ? source.getLegalStatus().name() : null)
                .userName(source.getUserName())
                .initials(source.getInitials())
                .role(source.getRole() != null ? source.getRole().name() : null)
                .state(source.getState() != null ? source.getState().name() : null)
                .addresses(source.getAddresses())
                .country(source.getDefaultCountry())
                .photo(source.getProfilePhoto() != null ? fileConverter.convert(source.getProfilePhoto()) : null)
                .countries(source.getListOfCountriesWhereServicesProvided())
                .build();

        // Create overall list for all files
        List<FileDetailed> allFiles = new ArrayList<>();

        // If there are some documents - add them to main list
        List<FileDetailed> documents = source.getDocuments();
        if (documents != null && !documents.isEmpty()) {
            allFiles.addAll(documents);
        }

        // If there are some portfolio - add them to main list
        List<FileDetailed> portfolio = source.getPortfolio();
        if (portfolio != null && !portfolio.isEmpty()) {
            allFiles.addAll(portfolio);
        }

        // Convert them to entities
        List<FileEntity> allFileEntities = allFiles.stream()
                .map(fileConverter::convert)
                .collect(Collectors.toList());

        userEntity.setDocuments(allFileEntities);

        return userEntity;
    }
}
