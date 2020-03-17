package com.anyservice.repository;

import com.anyservice.entity.user.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, UUID> {
    UserEntity findFirstByUserName(String userName);
}
