package com.anyservice.service;

import com.anyservice.dto.UserDTO;
import com.anyservice.entity.UserEntity;
import com.anyservice.repository.UserRepository;
import com.anyservice.service.api.CRUDService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService implements CRUDService<UserDTO, UUID> {

    private final UserRepository userRepository;
    private final ConversionService conversionService;

    public UserService(UserRepository userRepository, ConversionService conversionService) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
    }

    @Override
    public UserDTO create(UserDTO dto) {
        UserEntity entity = conversionService.convert(dto, UserEntity.class);

        UserEntity savedEntity = userRepository.save(entity);

        return conversionService.convert(savedEntity, UserDTO.class);
    }

    @Override
    public UserDTO update(UserDTO dto, UUID uuid, Date version) {
        if (!existsById(uuid)) {
            throw new IllegalArgumentException("No entity was found with this uuid");
        }

        Optional<UserDTO> id = findById(uuid);

        UserEntity savedEntity = userRepository.save(entity);

        return conversionService.convert(savedEntity, UserDTO.class);
    }

    @Override
    public Iterable<UserDTO> saveAll(Iterable<UserDTO> dtoIterable) {

        List<UserEntity> entityList = Stream.of(dtoIterable)
                .map(dto -> conversionService.convert(dto, UserEntity.class))
                .collect(Collectors.toList());

        Iterable<UserEntity> savedEntities = userRepository.saveAll(entityList);

        List<UserDTO> savedDto = Stream.of(savedEntities)
                .map(entity -> conversionService.convert(entity, UserDTO.class))
                .collect(Collectors.toList());

        return savedDto;
    }

    @Override
    public Optional<UserDTO> findById(UUID uuid) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(uuid);

        Optional<UserDTO> userDTOOptional = Optional.empty();

        if (optionalUserEntity.isPresent()) {
            UserEntity entity = optionalUserEntity.get();
            UserDTO userDTO = conversionService.convert(entity, UserDTO.class);
            userDTOOptional = Optional.of(userDTO);
        }

        return userDTOOptional;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return userRepository.existsById(uuid);
    }

    @Override
    public Iterable<UserDTO> findAll() {
        Iterable<UserEntity> userEntities = userRepository.findAll();

        List<UserDTO> savedDto = Stream.of(userEntities)
                .map(entity -> conversionService.convert(entity, UserDTO.class))
                .collect(Collectors.toList());

        return savedDto;
    }

    @Override
    public Iterable<UserDTO> findAllById(Iterable<UUID> uuids) {
        Iterable<UserEntity> userEntities = userRepository.findAllById(uuids);

        List<UserDTO> savedDto = Stream.of(userEntities)
                .map(entity -> conversionService.convert(entity, UserDTO.class))
                .collect(Collectors.toList());

        return savedDto;
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public void deleteById(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Override
    public void delete(UserDTO dto) {
        UserEntity entity = conversionService.convert(dto, UserEntity.class);

        userRepository.delete(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends UserDTO> dtoIterable) {
        List<UserEntity> entityList = Stream.of(dtoIterable)
                .map(dto -> conversionService.convert(dto, UserEntity.class))
                .collect(Collectors.toList());

        userRepository.deleteAll(entityList);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
