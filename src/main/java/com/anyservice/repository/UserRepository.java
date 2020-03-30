package com.anyservice.repository;

import com.anyservice.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Find first user via given user name
     *
     * @param userName given name of a user
     * @return found user via given user name
     */
    UserEntity findFirstByUserName(String userName);
}
